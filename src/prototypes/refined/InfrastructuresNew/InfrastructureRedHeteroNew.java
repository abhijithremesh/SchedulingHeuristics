package org.cloudsimplus.examples.InfrastructuresNew;


import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.examples.HybridModel.GeneticAlgorithmOne;
import org.cloudsimplus.examples.HybridModel.GeneticAlgorithmTwo;
import org.cloudsimplus.examples.HybridModel.MyBroker;
import org.cloudsimplus.examples.HybridStrategy.MyHeuristicBroker;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.*;

/**
 * A minimal but organized, structured and re-usable CloudSim Plus example
 * which shows good coding practices for creating simulation scenarios.
 *
 * <p>It defines a set of constants that enables a developer
 * to change the number of Hosts, VMs and Cloudlets to create
 * and the number of {@link Pe}s for Hosts, VMs and Cloudlets.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class InfrastructureRedHeteroNew {

    private static final double INTERVAL = 3600;

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;
    private static final int HOST_RAM = 20000;
    private static final int HOST_BW = 100000;
    private static final int HOST_SIZE = 1000000;

    private static final int VMS = 25;
    private static final int VM_PES = 2;
    private static int VM_RAM = 1000;  // 128 - 15360
    private static int VM_BW = 1000; // 128 - 15360
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 1000;

    private static final int CLOUDLETS = 1000;   // 100-1000
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 2000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = 20;
    //private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz"; // 28476
    private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private CloudSim simulation;
    //private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private Datacenter datacenter1;
    int heuristicIndex = 1;
    int schedulingHeuristic;
    MyHeuristicBroker broker0;

    HashMap<Long, Long> cloudletLengthsMap = new HashMap<Long, Long>();

    List<Integer> VM_MIPS_list = new ArrayList<Integer>(){{
        add(500);
        add(1000);
        add(2500);
        add(5000);
        add(7500);
        add(10000);
        add(20000);
        add(30000);
    }};

    ArrayList<Integer> solutionCandidate = new ArrayList<>();
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args) {
        new InfrastructureRedHeteroNew();
    }

    private InfrastructureRedHeteroNew() {

        Log.setLevel(Level.OFF);

        // Generating Initial Population
        GeneticAlgorithmTwo ga = new GeneticAlgorithmTwo();
        ArrayList<ArrayList> solutionCandidatesList = ga.createInitialPopulation(1, 10);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        // Identifying and Storing the best solution candidates of each generation
        double generationAvgFittestValue;
        double generationBestFittestValue;
        double bestFittestValue;
        ArrayList<Integer> bestFittestCandidate = new ArrayList<>();
        ArrayList<Double> generationAvgFitnessValuesList = new ArrayList<Double>();
        ArrayList<Double> generationBestFitnessValuesList = new ArrayList<Double>();
        ArrayList<Integer> generationBestSolutionCandidate = new ArrayList<>();
        ArrayList<ArrayList> generationBestSolutionCandidateList = new ArrayList<>();

        for (int generations = 0; generations < 1; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();

            System.out.printf("%n=================================== GENERATION " + generations + " STARTS ==========================================%n");

            System.out.printf("%nsolutionCandidatesList: " + solutionCandidatesList + "%n%n");

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                heuristicIndex = 0;

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" STARTS ****************%n");

                simulation = new CloudSim();
                datacenter0 = createDatacenterOne();
                datacenter0.setSchedulingInterval(0.5);
                datacenter1 = createDatacenterTwo();
                datacenter1.setSchedulingInterval(0.5);

                //broker0 = new DatacenterBrokerSimple(simulation);
                broker0 = new MyHeuristicBroker(simulation);

                //vmList = createVmsSpaceShared();
                vmList = createVmsTimeShared();

                //cloudletList = createCloudlets();
                cloudletList = createCloudletsFromWorkloadFile();

                considerSubmissionTimes(1);

                //modifyCloudletsForSpaceShared();

                broker0.submitVmList(vmList);
                broker0.submitCloudletList(cloudletList);

                simulation.addOnClockTickListener(this::pauseSimulation);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

                solutionCandidate = solutionCandidatesList.get(i);
                System.out.printf("%nSolution Candidate: "+solutionCandidate+"%n%n");
                schedulingHeuristic = solutionCandidate.get(heuristicIndex);
                System.out.println("Heuristic Switched to "+schedulingHeuristic);
                broker0.selectSchedulingPolicy(schedulingHeuristic,vmList);

                simulation.start();

          /*
                -makespan
                -totalResponseTime
                -avgResponseTime
                -totalWaitingTime
                -avgWaitingTime
                -totalExecutionTime
                -avgExecutionTime
                -SlowdownRatio
                -totalVmRunTime
                -processorUtilization
                -degreeOfImbalance
                -totalVmCost
                -throughput
         */
                double fitness = evaluatePerformanceMetrics("makespan");

                System.out.println("vms_created: "+broker0.getVmCreatedList().size());
                System.out.println("Simulation Time: " + simulation.clock());
                System.out.println("Total cloudlets processed: " + broker0.getCloudletFinishedList().size());

                solutionCandidatesFitnessList.add(fitness);
                System.out.println("Solution Candidate Fitness List: "+solutionCandidatesFitnessList);

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" ENDS ****************%n");

                //new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();

            }


            System.out.println("solutionCandidatesList:" + solutionCandidatesList);
            System.out.println("solutionCandidatesFitnessList: " + solutionCandidatesFitnessList);
            System.out.println("solutionCandidatesListSize: " + solutionCandidatesList.size());
            System.out.println("solutionCandidatesFitnessListSize: " + solutionCandidatesFitnessList.size());

/*
            generationAvgFittestValue = ga.getGenerationAvgFittestValue(solutionCandidatesFitnessList);
            generationAvgFitnessValuesList.add(generationAvgFittestValue);
            generationBestFittestValue = ga.getGenerationBestFittestValue(solutionCandidatesFitnessList,"min");
            generationBestFitnessValuesList.add(generationBestFittestValue);
            generationBestSolutionCandidate = ga.getGenerationBestFittestSolutionCandidate(solutionCandidatesList, solutionCandidatesFitnessList,"min");
            generationBestSolutionCandidateList.add(generationBestSolutionCandidate);
            bestFittestValue = ga.getBestFittestValue(generationBestFitnessValuesList,"min");
            bestFittestCandidate = ga.getBestFittestSolutionCandidate(generationBestSolutionCandidateList,generationBestFitnessValuesList,"min");
            System.out.println("generationAvgFitnessValue: "+generationAvgFittestValue);
            System.out.println("generationAvgFitnessValuesList: "+generationAvgFitnessValuesList);
            System.out.println("generationBestFittestValue: "+generationBestFittestValue);
            System.out.println("generationBestFittestValuesList: "+generationBestFitnessValuesList);
            System.out.println("generationBestSolutionCandidate: "+generationBestSolutionCandidate);
            System.out.println("generationBestSolutionCandidateList: "+generationBestSolutionCandidateList);
            System.out.println("bestFittestValue: "+bestFittestValue);
            System.out.println("bestFittestCandidate: "+bestFittestCandidate);


            System.out.println("=================================== GENERATION "+generations+" ENDS ==========================================");

            String flag = "min";
            int eliteCount = 3;
            int tournamentCount = 4;
            double crossoverRate = 0.5;
            double mutationRate = 0.4;

            solutionCandidatesList = ga.generationEvolve(solutionCandidatesList,solutionCandidatesFitnessList,flag,eliteCount,tournamentCount, crossoverRate, mutationRate);

            System.out.println("=================================== GENERATION "+generations+" EVOLVED ==========================================");



 */

        }

    }

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {

        heuristicIndex ++;

        schedulingHeuristic = solutionCandidate.get((heuristicIndex%24));
        System.out.println("Heuristic Switched to "+schedulingHeuristic);
        broker0.selectSchedulingPolicy(schedulingHeuristic, vmList);

        simulation.resume();
        System.out.println("simulation resumed...");

    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * (heuristicIndex + 1)){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            postSimulationHeuristicSpecificFinishedCloudlets(broker0);
            System.out.printf("Total Cloudlets processed: "+broker0.getCloudletFinishedList().size()+"%n");
            cloudletList.removeAll(broker0.getCloudletFinishedList());
            restoreCloudletLengths();
            System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
        }
    }

    private Datacenter createDatacenterOne() {
        final List<Host> hostListOne= new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostListOne.add(host);
        }
        return new DatacenterSimple(simulation, hostListOne);
    }

    private Datacenter createDatacenterTwo() {
        final List<Host> hostListTwo = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostListTwo.add(host);
        }
        return new DatacenterSimple(simulation, hostListTwo);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(75000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVmsSpaceShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPS_list.get(i%7);
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            list.add(vm);
        }
        return list;
    }

    private List<Vm> createVmsTimeShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPS_list.get(i%7);
            final Vm vm = new VmSimple(VM_MIPS , VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        System.out.printf("# Created %12d Cloudlets for %n", this.cloudletList.size());
        storeCloudletLengths();
        return cloudletList;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setSubmissionDelay(0);
            list.add(cloudlet);
        }
        return list;
    }

    private void modifyCloudletsForSpaceShared() {
        cloudletList.forEach(c->c.setLength(c.getTotalLength()));
        cloudletList.forEach(c->c.setNumberOfPes(1));
    }

    private void considerSubmissionTimes(int n) {

        if (n == 1) {
            double minSubdelay = cloudletList.get(0).getSubmissionDelay();
            for (Cloudlet c : cloudletList
            ) {
                c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
            }
        } else if (n == 0){
            cloudletList.forEach(c->c.setSubmissionDelay(0));
        }

    }

    private double evaluatePerformanceMetrics(String metric) {

        double metricValue = 0;
        double makespan = broker0.getCloudletFinishedList().get(broker0.getCloudletFinishedList().size() - 1).getFinishTime();

        double totalResponseTime = 0.0;
        double totalWaitingTime = 0.0;
        double totalExecutionTime = 0.0;
        for (Cloudlet c : broker0.getCloudletFinishedList()
        ) {

            totalResponseTime = totalResponseTime + (c.getSubmissionDelay() + c.getWaitingTime() + c.getActualCpuTime());
            totalWaitingTime = totalWaitingTime + c.getWaitingTime();
            totalExecutionTime = totalExecutionTime + c.getActualCpuTime();

        }

        double totalVmRunTime = 0.0;
        for (Vm v : vmList
        ) {
            totalVmRunTime = totalVmRunTime + v.getTotalExecutionTime();
        }

        double degreeOfImbalance = 0;
        List <Double> vmExecTimeList = new ArrayList<Double>();
        for (Vm v: broker0.getVmCreatedList()
        ) {
            vmExecTimeList.add(v.getTotalExecutionTime());
        }
        degreeOfImbalance = (Collections.max(vmExecTimeList) - Collections.min(vmExecTimeList))/vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0);
        //degreeOfImbalance = (Collections.max(vmExecTimeList) + Collections.min(vmExecTimeList))/vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0);

        double costPerSecond = (0.12 + 0.13 + 0.17 + 0.48 + 0.52 + 0.96)/3600 ;
        double totalVmCost = totalVmRunTime * costPerSecond;

        double throughput = broker0.getCloudletFinishedList().size() / makespan;


        if (metric == "makespan") {
            metricValue = makespan;
            System.out.println("makespan: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "totalResponseTime") {
            metricValue = totalResponseTime;
            System.out.println("totalResponseTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgResponseTime") {
            metricValue = totalResponseTime / cloudletList.size();
            System.out.println("avgResponseTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalWaitingTime") {
            metricValue = totalWaitingTime;
            System.out.println("totalWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgWaitingTime") {
            metricValue = totalWaitingTime / cloudletList.size();
            System.out.println("avgWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalExecutionTime"){
            metricValue = totalExecutionTime;
            System.out.println("Total Execution Time: "+((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "avgExecutionTime"){
            metricValue = totalExecutionTime/cloudletList.size();
            System.out.println("avgExecutionTime: "+ ((double)Math.round(metricValue *  100.0)/100)  );
        } else if (metric == "totalVmRunTime"){
            metricValue = totalVmRunTime;
            System.out.println("totalVmRunTime: "+totalVmRunTime);
        } else if (metric == "SlowdownRatio") {
            metricValue = (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size());
            System.out.println("SlowdownRatio: " +((double)Math.round(metricValue *  100.0)/100)  );
        } else if(metric == "processorUtilization"){
            metricValue = totalVmRunTime/simulation.getLastCloudletProcessingUpdate();
            System.out.println("processorUtilization: "+((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "degreeOfImbalance") {
            metricValue = degreeOfImbalance;
            System.out.println("degreeOfImbalance: " + ((double) Math.round(metricValue * 100.0) / 100));
        } else if (metric == "totalVmCost")   {
            metricValue = totalVmCost;
            System.out.println("totalVmCost: " + ((double) Math.round(metricValue * 100.0) / 100));
        } else if (metric == "throughput") {
            metricValue = throughput;
            System.out.println("throughput: " + ((double) Math.round(metricValue * 100.0) / 100));
        }

        return ((double)Math.round(metricValue *  100.0)/100);
        //return metricValue;

    }

    public void postSimulationHeuristicSpecificFinishedCloudlets(MyHeuristicBroker myBroker){

        List<Cloudlet> allFinishedCloudlets = myBroker.getCloudletFinishedList();
        heuristicSpecificFinishedCloudletsList.add(allFinishedCloudlets);
        int items = heuristicSpecificFinishedCloudletsList.size();
        List<Cloudlet> heuristicSpecificFinishedCloudlets = new ArrayList<Cloudlet>();
        //if (brokerh.getCloudletSubmittedList().size() > brokerh.getCloudletFinishedList().size()) {
        if (items == 1) {
            heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(0);
        } else if (items > 1) {
            List<Cloudlet> lastItem = heuristicSpecificFinishedCloudletsList.get(items - 1);
            List<Cloudlet> secondLastItem = heuristicSpecificFinishedCloudletsList.get(items - 2);
            List<Cloudlet> differences = new ArrayList<>(lastItem);
            differences.removeAll(secondLastItem);
            //heuristicSpecificFinishedCloudletsList.get(items - 1).removeAll(heuristicSpecificFinishedCloudletsList.get(items - 2));
            //heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(items - 1);
            heuristicSpecificFinishedCloudlets = differences;
        }
        //}

        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();
        System.out.printf("Heuristic Cloudlets processed: "+heuristicSpecificFinishedCloudlets.size()+"%n");
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }

    public void storeCloudletLengths(){
        for (Cloudlet c : cloudletList
        ) {
            cloudletLengthsMap.put(c.getId(),c.getLength());
        }

    }

    public void restoreCloudletLengths(){
        for (Cloudlet c : cloudletList
        ) {
            c.setLength(cloudletLengthsMap.get(c.getId()));
        }

    }
}
