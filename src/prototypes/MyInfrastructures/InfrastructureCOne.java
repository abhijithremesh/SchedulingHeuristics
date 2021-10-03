package org.cloudsimplus.examples.MyInfrastructures;


import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.HybridApproach.GeneticAlgorithm;
import org.cloudsimplus.examples.MyHeuristics.MyBroker;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
public class InfrastructureCOne {

    private static final double INTERVAL = 3600;

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;
    private static final int HOST_RAM = 1600000;
    private static final int HOST_BW = 1000000;
    private static final int HOST_SIZE = 100000000;

    private static final int VMS = 25;
    private static final int VM_PES = 1;
    private static int VM_RAM = 500;
    private static int VM_BW = 1000;
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 100;

    private static final int CLOUDLETS = 100;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";

    private CloudSim simulation;
    //private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int heuristicIndex = 1;
    int schedulingHeuristic;
    MyBroker myBroker;

    List<Integer> VM_MIPSList = new ArrayList<Integer>() {{
        add(500);
        add(1000);
        add(1500);
        add(2000);
        add(2500);
        add(3000);
        add(3500);
        add(4000);
        add(4500);
        add(5000);
    } };

    ArrayList<Integer> solutionCandidate = new ArrayList<>();
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args) {
        new InfrastructureCOne();
    }

    private InfrastructureCOne() {

        Log.setLevel(Level.OFF);

        // Generating Initial Population
        GeneticAlgorithm mh_ga_2 = new GeneticAlgorithm();
        ArrayList<ArrayList> solutionCandidatesList = mh_ga_2.createInitialPopulation(10, 11);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        // Identifying and Storing the best solution candidates of each generation
        double generationAvgFittestValue;
        double generationBestFittestValue;
        ArrayList<Double> generationAvgFitnessValuesList = new ArrayList<Double>();
        ArrayList<Double> generationBestFitnessValuesList = new ArrayList<Double>();
        ArrayList<Integer> generationBestSolutionCandidate = new ArrayList<>();

        for (int generations = 0; generations < 2; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();

            System.out.printf("%n=================================== GENERATION " + generations + " STARTS ==========================================%n");

            System.out.printf("%nsolutionCandidatesList: " + solutionCandidatesList + "%n%n");

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                heuristicIndex = 1;

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" STARTS ****************%n");

                simulation = new CloudSim();
                datacenter0 = createDatacenter();
                datacenter0.setSchedulingInterval(0.5);

                //broker0 = new DatacenterBrokerSimple(simulation);
                myBroker = new MyBroker(simulation);

                vmList = createVms();
                //cloudletList = createCloudlets();
                cloudletList = createCloudletsFromWorkloadFile(100);
                modifySubmissionTimes();
                //modifyLength();  // sets length = length * npe
                //modifyReqPes();  // sets the reqPE as 1

                myBroker.submitVmList(vmList);
                myBroker.submitCloudletList(cloudletList);

                simulation.addOnClockTickListener(this::pauseSimulation);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

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
                 */

                System.out.println(myBroker.getVmCreatedList().size());

                double fitness = evaluatePerformanceMetrics("makespan");

                System.out.println("Simulation Time: " + simulation.clock());
                System.out.println("Total cloudlets processed: " + myBroker.getCloudletFinishedList().size());
                //System.out.println("Total cloudlets processed: " + broker0.getCloudletFinishedList().size());

                solutionCandidatesFitnessList.add(fitness);
                System.out.println("Solution Candidate Fitness List: "+solutionCandidatesFitnessList);

                System.out.println("Total cloudlets processed: "+myBroker.getCloudletFinishedList().size());
                System.out.println("Any cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" ENDS ****************%n");

            }

            System.out.println("solutionCandidatesList:" + solutionCandidatesList);
            System.out.println("solutionCandidatesFitnessList: " + solutionCandidatesFitnessList);
            System.out.println("solutionCandidatesListSize: " + solutionCandidatesList.size());
            System.out.println("solutionCandidatesFitnessListSize: " + solutionCandidatesFitnessList.size());

            generationAvgFittestValue = mh_ga_2.getGenerationAvgFittestValue(solutionCandidatesFitnessList);
            generationAvgFitnessValuesList.add(generationAvgFittestValue);
            generationBestFittestValue = mh_ga_2.getGenerationBestFittestValue(solutionCandidatesFitnessList,"min");
            generationBestFitnessValuesList.add(generationBestFittestValue);
            //generationBestSolutionCandidate = mh_ga_2.getGenerationBestFittestSolutionCandidate(solutionCandidatesList, solutionCandidatesFitnessList,"min");
            System.out.println("generationAvgFitnessValue: "+generationAvgFittestValue);
            System.out.println("generationAvgFitnessValuesList: "+generationAvgFitnessValuesList);
            System.out.println("generationBestFittestValue: "+generationBestFittestValue);
            System.out.println("generationBestFitnessValuesList: "+generationBestFitnessValuesList);
            //System.out.println("generationBestSolutionCandidate: "+generationBestSolutionCandidate);

            System.out.println("=================================== GENERATION "+generations+" ENDS ==========================================");

            String flag = "min";
            int eliteCount = 3;
            int tournamentCount = 4;
            double crossoverRate = 0.5;
            double mutationRate = 0.4;

            solutionCandidatesList = mh_ga_2.generationEvolve(solutionCandidatesList,solutionCandidatesFitnessList,flag,eliteCount,tournamentCount, crossoverRate, mutationRate);

            System.out.println("=================================== GENERATION "+generations+" EVOLVED ==========================================");

        }

    }

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {

        if (heuristicIndex < 24){
            heuristicIndex ++;
        }
        else if (heuristicIndex >= 24 ){
            heuristicIndex = 1;
        }

        schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
        System.out.println("Heuristic Switched to "+schedulingHeuristic);
        myBroker.selectSchedulingPolicy(schedulingHeuristic, vmList);

        simulation.resume();
        System.out.println("simulation resumed...");
    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            postSimulationHeuristicSpecificFinishedCloudlets(myBroker);
            System.out.printf("Total Cloudlets processed: "+myBroker.getCloudletFinishedList().size()+"%n");
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
            System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
        }
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(20000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPSList.get(i%10);
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile(int count) {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        System.out.printf("# Created %12d Cloudlets for %n", this.cloudletList.size());
        List<Cloudlet> list = new ArrayList<>();
        for (int i=0; i < count; i++){
            list.add(cloudletList.get(i));
        }
        cloudletList = list;
        return cloudletList;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1500);
            cloudlet.setSubmissionDelay(0);
            list.add(cloudlet);
        }
        return list;
    }

    private void modifySubmissionTimes() {
        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
        }
    }

    private void modifyLength(){
        for (Cloudlet c: cloudletList
             ) {
            long length = c.getLength();
            long pes = c.getNumberOfPes();
            c.setLength(length* pes);

        }
    }

    private void modifyReqPes(){
        for (Cloudlet c : cloudletList
             ) {
            c.setNumberOfPes(1);
        }
    }

    public void postSimulationHeuristicSpecificFinishedCloudlets(MyBroker myBroker){

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

    private double evaluatePerformanceMetrics(String metric) {

        double metricValue = 0;
        double makespan = myBroker.getCloudletFinishedList().get(myBroker.getCloudletFinishedList().size() - 1).getFinishTime();

        double totalResponseTime = 0.0;
        double totalWaitingTime = 0.0;
        double totalExecutionTime = 0.0;
        for (Cloudlet c : myBroker.getCloudletFinishedList()
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
        }

        return ((double)Math.round(metricValue *  100.0)/100);

    }


}

