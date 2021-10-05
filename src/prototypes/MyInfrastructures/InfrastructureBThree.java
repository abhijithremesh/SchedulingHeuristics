package org.cloudsimplus.examples.MyInfrastructures;


import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin;
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
public class InfrastructureBThree {

    private static final double INTERVAL = 3600;

    private static final int HOSTS_DUALCORE = 2;
    private static final int HOSTS_QUADCORE = 2;
    private static final int HOST_PES = 2;
    private static final int HOST_RAM = 20000;
    private static final int HOST_SIZE = 1000000;
    private static final int HOST_BW = 10000;

    private static final int VMS = 20;
    private static final int VM_PES = 1;
    private static int VM_RAM = 512;
    private static int VM_BW = 1000;
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 1000;

    private static final int CLOUDLETS = 20;  // limit:1200
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 2000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private CloudSim simulation;
    //private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int heuristicIndex = 1;
    int schedulingHeuristic;
    MyBroker myBroker;

    List<Integer> VM_MIPSList = new ArrayList<Integer>() {{
        add(1000);
        add(2000);
        add(3000);
        add(4000);
        add(5000);
        add(6000);
        add(7000);
        add(8000);
        add(9000);
        add(10000);
    } };

    ArrayList<Integer> solutionCandidate = new ArrayList<>(Arrays.asList(6, 6, 4, 1, 2, 8, 0, 5, 3, 9, 2, 7, 0, 5, 9, 3, 7, 4, 8, 1, 2, 5, 4, 0));

    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args) {
        new InfrastructureBThree();
    }

    private InfrastructureBThree() {

        Log.setLevel(Level.OFF);

        heuristicIndex = 0;

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        datacenter0.setSchedulingInterval(0.5);

        //broker0 = new DatacenterBrokerSimple(simulation);
        myBroker = new MyBroker(simulation);

        vmList = createVms();
        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile(90);
        modifySubmissionTimes();
        //modifyLength();  // sets length = length * npe
        //modifyReqPes();  // sets the reqPE as 1

        myBroker.submitVmList(vmList);
        myBroker.submitCloudletList(cloudletList);

        simulation.addOnClockTickListener(this::pauseSimulation);
        simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

        System.out.printf("%nSolution Candidate: "+solutionCandidate+"%n%n");

        schedulingHeuristic = solutionCandidate.get(heuristicIndex);
        System.out.println("Heuristic Switched to "+schedulingHeuristic);
        myBroker.selectSchedulingPolicy(schedulingHeuristic,vmList);

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

        double makespan = evaluatePerformanceMetrics("makespan");
        double avgResponseTime = evaluatePerformanceMetrics("avgResponseTime");
        double totalResponseTime = evaluatePerformanceMetrics("totalResponseTime");
        double totalExecutionTime = evaluatePerformanceMetrics("totalExecutionTime");
        double avgExecutionTime = evaluatePerformanceMetrics("avgExecutionTime");
        double SlowdownRatio = evaluatePerformanceMetrics("SlowdownRatio");
        double Throughput = evaluatePerformanceMetrics("Throughput");

        System.out.println("Simulation Time: " + simulation.clock());
        System.out.println("Total cloudlets processed: " + myBroker.getCloudletFinishedList().size());


        }


    public void switchSchedulingHeuristics(EventInfo pauseInfo) {

        heuristicIndex ++;

        schedulingHeuristic = solutionCandidate.get((heuristicIndex%24));
        System.out.println("Heuristic Switched to "+schedulingHeuristic);
        myBroker.selectSchedulingPolicy(schedulingHeuristic, vmList);

        simulation.resume();
        System.out.println("simulation resumed...");

    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * (heuristicIndex + 1)){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            postSimulationHeuristicSpecificFinishedCloudlets(myBroker);
            System.out.printf("Total Cloudlets processed: "+myBroker.getCloudletFinishedList().size()+"%n");
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
            System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
        }
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS_DUALCORE+HOSTS_QUADCORE);
        for(int i = 0; i < HOSTS_DUALCORE; i++) {
            Host host = createHostDualCore();
            hostList.add(host);
        }
        for(int i = 0; i < HOSTS_QUADCORE; i++) {
            Host host = createHostQuadCore();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicyRoundRobin());
    }

    private Host createHostDualCore() {
        final List<Pe> peList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            peList.add(new PeSimple(500000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private Host createHostQuadCore() {
        final List<Pe> peList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            peList.add(new PeSimple(250000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            Random r = new Random();
            int n = r.nextInt(11-1) + 1;
            final Vm vm = new VmSimple( n * VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            list.add(vm);
        }
        return list;
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

    private List<Cloudlet> createCloudletsFromWorkloadFile(int count) {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1000);
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

        /*
        double degreeOfImb = 0.0;
        List<Double> vmExecTimeList = new ArrayList<Double>();
        for (Vm v:myBroker.getVmCreatedList() ) {
            vmExecTimeList.add(v.getTotalExecutionTime());
        }
        degreeOfImb = (Collections.max(vmExecTimeList)+Collections.min(vmExecTimeList))/vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0);


         */

        if (metric == "makespan") {
            metricValue = makespan;
            System.out.println("makespan: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "totalResponseTime") {
            metricValue = totalResponseTime;
            System.out.println("totalResponseTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgResponseTime") {
            metricValue = totalResponseTime / myBroker.getCloudletFinishedList().size();
            System.out.println("avgResponseTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalWaitingTime") {
            metricValue = totalWaitingTime;
            System.out.println("totalWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgWaitingTime") {
            metricValue = totalWaitingTime / myBroker.getCloudletFinishedList().size();
            System.out.println("avgWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalExecutionTime"){
            metricValue = totalExecutionTime;
            System.out.println("Total Execution Time: "+((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "avgExecutionTime"){
            metricValue = totalExecutionTime/myBroker.getCloudletFinishedList().size();
            System.out.println("avgExecutionTime: "+ ((double)Math.round(metricValue *  100.0)/100)  );
        } else if (metric == "totalVmRunTime"){
            metricValue = totalVmRunTime;
            System.out.println("totalVmRunTime: "+totalVmRunTime);
        } else if (metric == "SlowdownRatio") {
            metricValue = (totalResponseTime / myBroker.getCloudletFinishedList().size()) / (totalExecutionTime / myBroker.getCloudletFinishedList().size());
            System.out.println("SlowdownRatio: " +((double)Math.round(metricValue *  100.0)/100)  );
        } else if(metric == "processorUtilization"){
            metricValue = totalVmRunTime/simulation.getLastCloudletProcessingUpdate();
            System.out.println("processorUtilization: "+((double)Math.round(metricValue *  100.0)/100));
        } else if(metric == "Throughput"){
            metricValue = myBroker.getCloudletFinishedList().size()/totalExecutionTime;
            System.out.println("Throughput: "+((double)Math.round(metricValue *  100.0)/100));
        }
        else if(metric == "fitnessFunction"){
            metricValue = makespan + (totalResponseTime / myBroker.getCloudletFinishedList().size());
            System.out.println("processorUtilization: "+((double)Math.round(metricValue *  100.0)/100));
        }

        return ((double)Math.round(metricValue *  100.0)/100);

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



}

