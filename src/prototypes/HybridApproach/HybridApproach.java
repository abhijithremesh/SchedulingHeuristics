package org.cloudsimplus.examples.HybridApproach;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.SchedullingHeuristics.HeuristicBroker;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HybridApproach {

    private static final double INTERVAL = 3600;
    private static final int HOSTS = 1;
    private static final int HOST_PES = 64;

    private static final int VMS = 1;
    private static final int VM_PES = 64;
    private static final int  VM_MIPS = 1000;
    private static final long VM_SIZE = 2000;
    private static final int  VM_RAM = 1000;
    private static final long VM_BW = 50000;

    private static final int CLOUDLETS = 28485;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";

    private CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    HybridHeuristicBroker brokerh;
    int heuristicIndex;
    int schedulingHeuristic;

    ArrayList<Integer> solutionCandidate = new ArrayList<>();
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args)  {
        new HybridApproach();
    }

    private HybridApproach() {

        Log.setLevel(Level.OFF);

        // Generating Initial Population
        GeneticAlgorithm mh_ga_2 = new GeneticAlgorithm();
        ArrayList<ArrayList> solutionCandidatesList = mh_ga_2.createInitialPopulation(1, 2);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        // Identifying and Storing the best solution candidates of each generation
        double generationBestFittestValue;
        ArrayList<Double> generationBestFitnessValuesList = new ArrayList<Double>();
        ArrayList<Integer> generationBestSolutionCandidate = new ArrayList<>();

        for (int generations = 0; generations < 1; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();

            System.out.printf("%n=================================== GENERATION "+generations+" STARTS ==========================================%n");

            System.out.printf("%nsolutionCandidatesList: "+solutionCandidatesList+"%n%n");

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                heuristicIndex = 1;

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" STARTS ****************%n");

                simulation = new CloudSim();

                brokerh = new HybridHeuristicBroker(simulation);
                //broker0 = new DatacenterBrokerSimple(simulation);

                createCloudletsFromWorkloadFile();
                limitCloudlets(1000);
                modifyCloudletts();
                modifySubmissionTimes();
                cloudletList.removeIf(c -> c.getNumberOfPes() > 64);              // removing cloudlets which requires more than 64 PEs

                createVms();

                datacenter0 = createDatacenter();
                datacenter0.setSchedulingInterval(10);

                brokerh.submitVmList(vmList);
                brokerh.submitCloudletList(cloudletList);
                //broker0.submitVmList(vmList);
                //broker0.submitCloudletList(cloudletList);

                simulation.addOnClockTickListener(this::pauseSimulation);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

                solutionCandidate = solutionCandidatesList.get(i);
                System.out.printf("%nSolution Candidate: "+solutionCandidate+"%n%n");

                schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
                brokerh.selectSchedulingHeuristics(schedulingHeuristic,vmList,cloudletList);

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

                double fitness = evaluatePerformanceMetrics("avgResponseTime");


                System.out.println("Simulation Time: " + simulation.clock());
                System.out.println("Total cloudlets processed: " + brokerh.getCloudletFinishedList().size());
                //System.out.println("Total cloudlets processed: " + broker0.getCloudletFinishedList().size());

                solutionCandidatesFitnessList.add(fitness);
                System.out.println("Solution Candidate Fitness List: "+solutionCandidatesFitnessList);

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" ENDS ****************%n");

            }

            System.out.println("solutionCandidatesList:" + solutionCandidatesList);
            System.out.println("solutionCandidatesFitnessList: " + solutionCandidatesFitnessList);
            System.out.println("solutionCandidatesListSize: " + solutionCandidatesList.size());
            System.out.println("solutionCandidatesFitnessListSize: " + solutionCandidatesFitnessList.size());

            //generationBestFittestValue = mh_ga_2.getGenerationBestFittestValue(solutionCandidatesFitnessList,"min");
            //generationBestFitnessValuesList.add(generationBestFittestValue);
            //generationBestSolutionCandidate = mh_ga_2.getGenerationBestFittestSolutionCandidate(solutionCandidatesList, solutionCandidatesFitnessList,"min");
           //System.out.println("generationBestFitnessValue: "+generationBestFittestValue);
            //System.out.println("generationBestFitnessValuesList: "+generationBestFitnessValuesList);
            //System.out.println("generationBestSolutionCandidate: "+generationBestSolutionCandidate);

            System.out.println("=================================== GENERATION "+generations+" ENDS ==========================================");

            String flag = "min";
            int eliteCount = 2;
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
        else if (heuristicIndex >= 24){
            heuristicIndex = 1;

        }

        System.out.println("Heuristic Switched to "+heuristicIndex);
        schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
        brokerh.selectSchedulingHeuristics(schedulingHeuristic,vmList,cloudletList);


        simulation.resume();
        System.out.printf("Simulation resumed%n%n");


    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n", Math.floor(simulation.clock()));
            postSimulationHeuristicSpecificFinishedCloudlets(brokerh);
            System.out.printf("Total Cloudlets processed: "+brokerh.getCloudletFinishedList().size()+"%n");
            cloudletList.removeAll(brokerh.getCloudletFinishedList());
            System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
            //System.out.println("Total Cloudlets processed: "+broker0.getCloudletFinishedList().size());
        }
    }

    private Datacenter createDatacenter() {
        List<Host> hostList = createHosts(HOSTS);
        Datacenter datacenter = new DatacenterSimple(simulation, hostList);
        System.out.printf("# Added   %12d Hosts to %s%n", HOSTS, datacenter0);
        return datacenter;
    }

    private List<Host> createHosts(final long hostsNumber) {
        final long ram = VM_RAM * 100;
        final long storage = VM_SIZE * 1000;
        final long bw = VM_BW * 1000;

        final List<Host> list = new ArrayList<>((int)hostsNumber);

        for (int i = 0; i < hostsNumber; i++) {
            List<Pe> peList = createPeList(VM_MIPS);
            Host host = new HostSimple(ram, bw, storage, peList);
            list.add(host);
        }
        return list;
    }

    private List<Pe> createPeList(final long mips) {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(mips));
        }
        return peList;
    }

    private void createVms() {

        //final double totalCloudletPes = cloudletList.stream().mapToDouble(Cloudlet::getNumberOfPes).sum();

        /* The number to multiple the VM_PES was chosen at random.
         * It's used to reduce the number of VMs to create. */
        //final int totalVms = (int)Math.ceil(totalCloudletPes / (VM_PES*6));

        vmList = new ArrayList<>();
        for (int i = 0; i < VMS; i++) {
            Vm vm = new VmSimple(VM_MIPS, VM_PES)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
            vmList.add(vm);
        }

        System.out.printf("# Created %12d VMs%n", vmList.size());
    }

    private void createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, VM_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();

        System.out.printf("# Created %12d Cloudlets%n", this.cloudletList.size());
    }

    private void showCloudletsDetails(){
        for (Cloudlet c: cloudletList
        ) {
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" PEs: "+c.getNumberOfPes()+" Submission delay: "+c.getSubmissionDelay()+" Waiting Time: "+c.getWaitingTime()+" Start time: "+c.getExecStartTime()+" CPU time: "+c.getActualCpuTime()+" Finish time: "+c.getFinishTime());
        }
    }

    private void showFinishedCloudletsDetails(){
        for (Cloudlet c: brokerh.getCloudletFinishedList()
        ) {
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" PEs: "+c.getNumberOfPes()+" Submission delay: "+c.getSubmissionDelay()+" Waiting Time: "+c.getWaitingTime()+" Start time: "+c.getExecStartTime()+" CPU time: "+c.getActualCpuTime()+" Finish time: "+c.getFinishTime());
        }
    }

    private void limitCloudlets(int n){

        List<Cloudlet> list = new ArrayList<>();

        for (int i=0; i<n; i++){
            list.add(cloudletList.get(i));
        }

        cloudletList = list;
        System.out.println("Cloudlets limited to "+cloudletList.size());

    }

    private void modifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
        }
    }

    private void modifyCloudletts(){
        for (Cloudlet c: cloudletList
        ) {
            //c.setSubmissionDelay(0);
            //c.setLength(10000000);
            //c.setNumberOfPes(1);
        }
    }

    public void postSimulationHeuristicSpecificFinishedCloudlets(HybridHeuristicBroker brokerh){

        List<Cloudlet> allFinishedCloudlets = brokerh.getCloudletFinishedList();
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
        System.out.printf("Heuristic Cloudlets processed: "+heuristicSpecificFinishedCloudlets.size()+"%n");
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }

    private double evaluatePerformanceMetrics(String metric) {

        double metricValue = 0;
        double makespan = brokerh.getCloudletFinishedList().get(brokerh.getCloudletFinishedList().size() - 1).getFinishTime();

        double totalResponseTime = 0.0;
        double totalWaitingTime = 0.0;
        double totalExecutionTime = 0.0;
        for (Cloudlet c : brokerh.getCloudletFinishedList()
        ) {

            totalResponseTime = totalResponseTime + (c.getFinishTime() - c.getExecStartTime());
            totalWaitingTime = totalWaitingTime + c.getWaitingTime();
            totalExecutionTime = totalExecutionTime + c.getActualCpuTime();

        }

        double totalVmRunTime = 0.0;
        for (Vm v : vmList
        ) {
            totalVmRunTime = totalVmRunTime + v.getTotalExecutionTime();
        }

        if (metric == "makespan") {
            System.out.println("makespan: " + makespan);
            metricValue = makespan;
        } else if (metric == "totalResponseTime") {
            System.out.println("totalResponseTime: " + totalResponseTime);
            metricValue = totalResponseTime;
        } else if (metric == "avgResponseTime") {
            System.out.println("avgResponseTime: " + totalResponseTime / cloudletList.size());
            metricValue = totalResponseTime / cloudletList.size();
        } else if (metric == "totalWaitingTime") {
            System.out.println("totalWaitingTime: " + totalWaitingTime);
            metricValue = totalWaitingTime;
        } else if (metric == "avgWaitingTime") {
            System.out.println("avgWaitingTime: " + totalWaitingTime / cloudletList.size());
            metricValue = totalWaitingTime / cloudletList.size();
        } else if (metric == "totalExecutionTime"){
            System.out.println("Total Execution Time: "+totalExecutionTime);
            metricValue = totalExecutionTime;
        } else if (metric == "avgExecutionTime"){
            System.out.println("avgExecutionTime: "+totalExecutionTime/cloudletList.size());
            metricValue = totalExecutionTime/cloudletList.size();
        } else if (metric == "totalVmRunTime"){
            System.out.println("totalVmRunTime: "+totalVmRunTime);
            metricValue = totalVmRunTime;
        } else if (metric == "SlowdownRatio") {
            System.out.println("SlowdownRatio: " + (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size()));
            metricValue = (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size());
        } else if(metric == "processorUtilization"){
            System.out.println("processorUtilization: "+totalVmRunTime/simulation.getLastCloudletProcessingUpdate());
            metricValue = totalVmRunTime/simulation.getLastCloudletProcessingUpdate();
        }

     return metricValue;

    }

}
