package org.cloudsimplus.examples.Standalone;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyRoundRobin;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
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
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.HybridModel.MyBroker;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfraGreenHetero2 {

    private static final int HOSTS_DUALCORE = 1;
    private static final int HOSTS_QUADCORE = 1;
    private static final int HOST_RAM = 20_000;
    private static final int HOST_BW = 10_000;
    private static final int HOST_SIZE = 10_00_000;

    private static final int VMS = 20;
    private static final int VM_PES = 1;
    private static final int  VM_RAM = 512;
    private static final int VM_BW = 1000;
    private static final int VM_SIZE = 10_000;

    private static int VM_MIPS;

    private static final int CLOUDLETS = 500;  // limit:1200
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 5000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = 30;
    //private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private CloudSim simulation;
    //private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private Datacenter datacenter1;
    MyBroker broker0;

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
        new InfraGreenHetero2();
    }

    private InfraGreenHetero2() {

        Log.setLevel(Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenterOne();
        datacenter1 = createDatacenterTwo();

        broker0 = new MyBroker(simulation);

        //vmList = createVmsSpaceShared();
        vmList = createVmsTimeShared();

        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile();

        considerSubmissionTimes(1);

        //modifyCloudletsForSpaceShared();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        broker0.Random(vmList);
        //broker0.FirstComeFirstServe(vmList);
        //broker0.LongestJobFirst(vmList);
        //broker0.ShortestJobFirst(vmList);
        //broker0.ShortestCloudletFastestPE(vmList);
        //broker0.LongestCloudletFastestPE(vmList);
        //broker0.MinimumCompletionTime(vmList);
        //broker0.MinimumExecutionTime(vmList);
        //broker0.MaxMin(vmList);
        //broker0.MinMin(vmList);
        //broker0.Sufferage(vmList);
        //broker0.ShortestJobFirstFirstFit(vmList);
        //broker0.LongestJobFirstFirstFit(vmList);


        simulation.start();


        double makespan = evaluatePerformanceMetrics("makespan");
        double avgResponseTime = evaluatePerformanceMetrics("avgResponseTime");
        double avgWaitingTime = evaluatePerformanceMetrics("avgWaitingTime");
        double avgExecutionTime = evaluatePerformanceMetrics("avgExecutionTime");
        double SlowdownRatio = evaluatePerformanceMetrics("SlowdownRatio");
        double totalVmRunTime = evaluatePerformanceMetrics("totalVmRunTime");
        double degreeOfImbalance = evaluatePerformanceMetrics("degreeOfImbalance");
        double totalVmCost = evaluatePerformanceMetrics("totalVmCost");
        double throughput = evaluatePerformanceMetrics("throughput");


        System.out.println(datacenter0.getHostList());
        System.out.println(datacenter1.getHostList());

        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        System.out.println("finished cloudlets: "+finishedCloudlets.size());
        System.out.println("vms created: "+broker0.getVmCreatedList().size());
        System.out.println("simulation_time: "+simulation.getLastCloudletProcessingUpdate());

        new CloudletsTableBuilder(finishedCloudlets).build();








    }

    private Datacenter createDatacenterOne() {
        final List<Host> hostList = new ArrayList<>(HOSTS_DUALCORE+HOSTS_QUADCORE);
        for(int i = 0; i < HOSTS_DUALCORE; i++) {
            Host host = createHostDualCore();
            //host.setId(1);
            hostList.add(host);
        }
        for(int i = 0; i < HOSTS_QUADCORE; i++) {
            Host host = createHostQuadCore();
            //host.setId(2);
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Datacenter createDatacenterTwo() {
        final List<Host> hostList = new ArrayList<>(HOSTS_DUALCORE+HOSTS_QUADCORE);
        for(int i = 0; i < HOSTS_DUALCORE; i++) {
            Host host = createHostDualCore();
            //host.setId(3);
            hostList.add(host);
        }
        for(int i = 0; i < HOSTS_QUADCORE; i++) {
            Host host = createHostQuadCore();
            hostList.add(host);
            //host.setId(4);
        }
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHostDualCore() {
        final List<Pe> peList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            peList.add(new PeSimple(200000)); //10000 500000 200000
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private Host createHostQuadCore() {
        final List<Pe> peList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            peList.add(new PeSimple(200000)); // 10000 250000 200000
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVmsTimeShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPSList.get(i%10);
            final Vm vm = new VmSimple(VM_MIPS , VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Vm> createVmsSpaceShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPSList.get(i%10);
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 100);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        System.out.printf("# Created %12d Cloudlets for %n", this.cloudletList.size());
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

        //return ((double)Math.round(metricValue *  100.0)/100);
        return metricValue;

    }

    private void modifyCloudletsForSpaceShared() {
        cloudletList.forEach(c->c.setLength(c.getTotalLength()));
        cloudletList.forEach(c->c.setNumberOfPes(1));
    }







}
