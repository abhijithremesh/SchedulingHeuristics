package org.cloudsimplus.examples.BenchmarkCheck;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin;
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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.examples.MyHeuristics.MyBroker;
import org.cloudsimplus.examples.MyInfrastructures.InfrastructureBOne_Test;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MaxMinCheckSpaceShared1 {

    private static final double INTERVAL = 50;

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

    private static final int CLOUDLETS = 100;  // limit:1200
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    MyBroker myBroker;
    int heuristicIndex = 1;

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

    public static void main(String[] args) {
        new MaxMinCheckSpaceShared1();
    }

    private MaxMinCheckSpaceShared1() {

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        //datacenter0.setSchedulingInterval(0.66);

        myBroker = new MyBroker(simulation);

        vmList = createVms();
        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile(100);
        //nullifySubmissionTimes();
        modifySubmissionTimes();
        //modifyLength();  // sets length = length * npe
        //modifyReqPes();  // sets the reqPE as 1

        myBroker.submitVmList(vmList);
        myBroker.submitCloudletList(cloudletList);


        //myBroker.FirstComeFirstServe(vmList);
        //myBroker.LongestJobFirst(vmList);
        //myBroker.ShortestJobFirst(vmList);
        //myBroker.ShortestCloudletFastestPE(vmList);
        //myBroker.LongestCloudletFastestPE(vmList);
        //myBroker.MinimumCompletionTime(vmList);
        //myBroker.MinimumExecutionTime(vmList);
        myBroker.MaxMin(vmList);
        //myBroker.MinMin(vmList);
        //myBroker.Sufferage(vmList);
        //myBroker.ShortestJobFirstFirstFit(vmList);
        //myBroker.LongestJobFirstFirstFit(vmList);

        //simulation.addOnClockTickListener(this::pauseSimulation);
        //simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

        simulation.start();

        List<Cloudlet> finishedCloudlets = myBroker.getCloudletFinishedList();

        System.out.println("Number of cloudlets finished: "+finishedCloudlets.size());
        System.out.println("vms created: "+myBroker.getVmCreatedList().size());


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
                -Throughput
         */


        double makespan = evaluatePerformanceMetrics("makespan");
        double totalResponseTime = evaluatePerformanceMetrics("totalResponseTime");
        double avgResponseTime = evaluatePerformanceMetrics("avgResponseTime");
        double totalWaitingTime = evaluatePerformanceMetrics("totalWaitingTime");
        double avgWaitingTime = evaluatePerformanceMetrics("avgWaitingTime");
        double totalExecutionTime = evaluatePerformanceMetrics("totalExecutionTime");
        double avgExecutionTime = evaluatePerformanceMetrics("avgExecutionTime");
        double SlowdownRatio = evaluatePerformanceMetrics("SlowdownRatio");
        double totalVmRunTime = evaluatePerformanceMetrics("totalVmRunTime");
        double processorUtilization = evaluatePerformanceMetrics("processorUtilization");
        double degreeOfImbalance = evaluatePerformanceMetrics("degreeOfImbalance");
        double Throughput = evaluatePerformanceMetrics("Throughput");



    }


    public void switchSchedulingHeuristics(EventInfo pauseInfo) {
        simulation.resume();
        heuristicIndex++;
        System.out.println("Heuristics switched....");
        System.out.println("simulation resumed...");
    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            //System.out.printf("Total Cloudlets processed: "+broker0.getCloudletFinishedList().size()+"%n");
            //cloudletList.removeAll(broker0.getCloudletFinishedList());
            //System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
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
            VM_MIPS = VM_MIPSList.get(i%10);
            final Vm vm = new VmSimple( VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
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

    private void nullifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(0);
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
        System.out.println(vmExecTimeList);
        double maxVm = Collections.max(vmExecTimeList);
        double minVm = Collections.min(vmExecTimeList);
        System.out.println("maxVm: "+maxVm);
        System.out.println("minVm: "+minVm);
        double avgVm = 0.0;
        double sumVm = 0.0;
        for (Double v : vmExecTimeList
             ) {
            sumVm = sumVm + v;
        }
        avgVm = sumVm / myBroker.getVmCreatedList().size();
        System.out.println("avgVm: "+avgVm);
        degreeOfImb = (maxVm - minVm)/avgVm;

         */




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
        } //else if(metric == "degreeOfImbalance"){
        // metricValue = degreeOfImb;
        // System.out.println("degreeOfImbalance: "+((double)Math.round(metricValue *  100.0)/100));}
        else if(metric == "Throughput"){
            metricValue = myBroker.getCloudletFinishedList().size()/totalExecutionTime;
            System.out.println("Throughput: "+((double)Math.round(metricValue *  100.0)/100));
        }
        else if(metric == "fitnessFunction"){
            metricValue = makespan + (totalResponseTime / cloudletList.size());
            System.out.println("processorUtilization: "+((double)Math.round(metricValue *  100.0)/100));
        }

        return ((double)Math.round(metricValue *  100.0)/100);

    }


}
