package org.cloudsimplus.examples;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PerformanceMetrics {

    private static final int HOSTS = 3;
    private static final int HOST_PES = 3;

    private static final int VMS = 5;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 50;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new PerformanceMetrics();
    }

    private PerformanceMetrics() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        /*

        Makespan (execution time of all cloudlets)
        TWT (Total Waiting Time)
        TFT (Total Finish Time)
        Throughput (number of executed jobs)
        Total cost (the processing time and the amount of data transferred)
        Completion time
        Resource Utilization (the ratio between the total busy time of Virtual Machine and the total finish execution time of the parallel application)
        Turnaround Time (time interval from the time of submission of a process to the time of the completion of the process)
                        (Difference b/w Completion Time and Arrival Time is called Turnaround Time)
        Waiting Time
        Degree of Imbalance = (Tmax + Tmin)/Tavg maximum, minimum and average total execution time (in seconds) of all VMs

         */

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        // Total Waiting Time
        double twt = getTotalWaitingTime(finishedCloudlets);
        System.out.println("Total Waiting Time: "+twt);

        // Makespan
        double ms = getMakespan(finishedCloudlets);
        System.out.println("Makespan: "+ms);

        // Total Finish Time
        double tft = getTotalFinishTime(finishedCloudlets);
        System.out.println("Total Finish Time: "+tft);

        // Turn around Time (Difference b/w Completion Time and Arrival Time is called Turnaround Time)
        double tat = getTurnAroundTime(finishedCloudlets);
        System.out.println("Turn Around Time: "+tat);

        /*
        for (Cloudlet c:finishedCloudlets ) {
            //System.out.println(c.getCostPerBw());
            //System.out.println(c.getCostPerSec());
            System.out.println(c.getUtilizationOfBw());
            System.out.println(c.getUtilizationOfBw());
            System.out.println(c.getUtilizationOfRam());
            System.out.println(c.getUtilizationOfRam());
            System.out.println(c.getUtilizationOfCpu());
            System.out.println(c.getUtilizationOfCpu());
            System.out.println("***********************");
        }
        */

        // Resource Utilization = Busy / Available
       double totalVmExecutionTime = 0;
        for (Vm v:broker0.getVmCreatedList() ) {
            totalVmExecutionTime = totalVmExecutionTime + v.getTotalExecutionTime();
        }
        System.out.println("total Vm Execution Time: "+totalVmExecutionTime);
        double resourceUtilization1 = totalVmExecutionTime/getMakespan(finishedCloudlets);
        System.out.println("Resource Utilization: "+resourceUtilization1);

        // Degree of Imbalance
        double dib = degreeOfImbalance();
        System.out.println("Degree of Imbalance: "+dib);

        //Throughput : No. of cloudlets preocessed within a specific period of time.
        double througput = finishedCloudlets.size()/getMakespan(finishedCloudlets);
        System.out.println("througput: "+througput);

    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
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
            Random random = new Random();
            int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH+randomLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    private double getTotalWaitingTime(List<Cloudlet> finishedCloudletList ){
        double twt = 0.0;
        for (Cloudlet c:finishedCloudletList ) {
            twt = twt + c.getWaitingTime();
        }
        return twt;
    }

    private double getMakespan(List<Cloudlet> finishedCloudletList ){
        double ms = 0.0;
        for (Cloudlet c:finishedCloudletList ) {
            ms = ms + c.getActualCpuTime();
        }
        return ms;
    }

    private double getTotalFinishTime(List<Cloudlet> finishedCloudletList ){
        Cloudlet c = finishedCloudletList.get(finishedCloudletList.size()-1);
        double tft = c.getFinishTime();
        return tft;
    }

    private double getTurnAroundTime(List<Cloudlet> finishedCloudletList ){
        Cloudlet c = finishedCloudletList.get(finishedCloudletList.size()-1);
        double turnAroundTime = c.getFinishTime() - c.getArrivalTime(datacenter0);
        return turnAroundTime;
    }

    private double degreeOfImbalance(){
        List<Double> vmExecTimeList = new ArrayList<Double>();
        for (Vm v:broker0.getVmCreatedList() ) {
            //System.out.println(v.getId());
            //System.out.println(v.getTotalExecutionTime());
            vmExecTimeList.add(v.getTotalExecutionTime());
            //System.out.println(Collections.max(vmExecTimeList));
            //System.out.println(Collections.min(vmExecTimeList));
            //System.out.println(vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0));
            //System.out.println((Collections.max(vmExecTimeList)+Collections.min(vmExecTimeList))/vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0));
            //System.out.println("***********************");
        }
        return (Collections.max(vmExecTimeList)+Collections.min(vmExecTimeList))/vmExecTimeList.stream().mapToDouble(d -> d).average().orElse(0.0);
    }




}





