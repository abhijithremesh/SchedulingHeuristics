package org.cloudsimplus.examples.HybridApproach;

import ch.qos.logback.classic.Level;
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
import org.cloudsimplus.examples.SchedullingHeuristics.GAMetaHeuristic2;
import org.cloudsimplus.examples.SchedullingHeuristics.HeuristicBroker;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HybridModel {

    private static final double INTERVAL = 3600;
    private static final int HOSTS = 1;
    private static final int HOST_PES = 1;

    private static final int VMS = 1;
    private static final int VM_PES = 1;
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
    int heuristicIndex = 1;
    int schedulingHeuristic;

    ArrayList<Integer> solutionCandidate = new ArrayList<>();

    public static void main(String[] args)  {
        new HybridModel();
    }

    private HybridModel()  {

        Log.setLevel(Level.WARN);

        // Generating Initial Population
        GeneticAlgorithm mh_ga_2 = new GeneticAlgorithm();
        ArrayList<ArrayList> solutionCandidatesList = mh_ga_2.createInitialPopulation(1, 2);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        for (int generations = 0; generations < 1 ; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();
            System.out.println("=================================== GENERATION "+generations+" STARTS ==========================================");
            System.out.println("solutionCandidatesList: "+solutionCandidatesList);

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                simulation = new CloudSim();

                broker0 = new DatacenterBrokerSimple(simulation);
                //brokerh = new HybridHeuristicBroker(simulation);
                createCloudletsFromWorkloadFile();
                modifySubmissionTimes();
                limitCloudlets(1000);
                modifyCloudletts();
                cloudletList.removeIf(c -> c.getNumberOfPes() > 64);

                createVms();

                datacenter0 = createDatacenter();
                datacenter0.setSchedulingInterval(1);

                broker0.submitVmList(vmList);
                broker0.submitCloudletList(cloudletList);

                simulation.addOnClockTickListener(this::pauseSimulationAtSpecificTime);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

                solutionCandidate = solutionCandidatesList.get(i);
                System.out.println("Solution Candidate: "+solutionCandidate);

                //schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
                //broker0.selectSchedulingHeuristics(schedulingHeuristic,vmList,cloudletList);

                System.out.println("********************** SIMULATION STARTS WITH CLOCK LISTENER ***************************************************");

                simulation.start();


                System.out.println("Total: "+broker0.getCloudletFinishedList().size());
                System.out.println(simulation.getLastCloudletProcessingUpdate());

            }


        }

    }

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {

        System.out.println("Total Cloudlets processed: "+broker0.getCloudletFinishedList().size());
        if (heuristicIndex <= 24){
            heuristicIndex ++;
            System.out.println("Heuristic Switched to "+heuristicIndex);
        }
        else if (heuristicIndex > 24){
            heuristicIndex = 1;
            System.out.println("Heuristic Switched to "+heuristicIndex);
        }

        simulation.resume();
        System.out.println("Simulation resumed");

    }

    public void pauseSimulationAtSpecificTime(EventInfo eInfo) {

        if (Math.floor(simulation.clock()) == INTERVAL * heuristicIndex) {
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(eInfo.getTime()));
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

        System.out.printf("# Created %12d VMs for the %s%n", vmList.size(), broker0);
    }

    private void createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, VM_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();

        System.out.printf("# Created %12d Cloudlets for %s%n", this.cloudletList.size(), broker0);
    }

    private void showCloudletsDetails(){
        for (Cloudlet c: cloudletList
        ) {
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" PEs: "+c.getNumberOfPes()+" Submission delay: "+c.getSubmissionDelay()+" Waiting Time: "+c.getWaitingTime()+" Start time: "+c.getExecStartTime()+" CPU time: "+c.getActualCpuTime()+" Finish time: "+c.getFinishTime());
        }
    }

    private void showFinishedCloudletsDetails(){
        for (Cloudlet c: broker0.getCloudletFinishedList()
        ) {
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" PEs: "+c.getNumberOfPes()+" Submission delay: "+c.getSubmissionDelay()+" Waiting Time: "+c.getWaitingTime()+" Start time: "+c.getExecStartTime()+" CPU time: "+c.getActualCpuTime()+" Finish time: "+c.getFinishTime());
        }
    }

    private void evaluatePerformanceMetrics(){

        System.out.println("Makespan : "+broker0.getCloudletFinishedList().get(broker0.getCloudletFinishedList().size()-1).getFinishTime());

        double totalResponseTime = 0.0;
        double totalWaitingTime = 0.0;
        double totalExecutionTime = 0.0;
        for (Cloudlet c: broker0.getCloudletFinishedList()
        ) {

            totalResponseTime = totalResponseTime + (c.getFinishTime()-c.getExecStartTime());
            totalWaitingTime = totalWaitingTime + c.getWaitingTime();
            totalExecutionTime = totalExecutionTime + c.getActualCpuTime();

        }
        System.out.println("Total Response Time: "+totalResponseTime);
        System.out.println("Avg Response Time: "+totalResponseTime/cloudletList.size());
        System.out.println("Total Waiting Time: "+totalWaitingTime);
        System.out.println("Avg Waiting Time: "+totalWaitingTime/cloudletList.size());
        System.out.println("Total Execution Time: "+totalExecutionTime);
        System.out.println("Avg Execution Time: "+totalExecutionTime/cloudletList.size());

        double totalVmRunTime = 0.0;
        for (Vm v: vmList
        ) {
            totalVmRunTime = totalVmRunTime + v.getTotalExecutionTime();
        }
        System.out.println("Total VM Run time: "+totalVmRunTime);

        System.out.println("Slow down ratio: "+(totalResponseTime/cloudletList.size())/(totalExecutionTime/cloudletList.size()));
        System.out.println("Processor Utilization: "+totalVmRunTime/simulation.getLastCloudletProcessingUpdate());

    }

    private void limitCloudlets(int n){

        List<Cloudlet> list = new ArrayList<>();

        for (int i=0; i<n; i++){
            list.add(cloudletList.get(i));
        }

        cloudletList = list;

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
            c.setSubmissionDelay(0);
            //c.setLength(10000);
            //c.setNumberOfPes(1);
        }
    }

}
