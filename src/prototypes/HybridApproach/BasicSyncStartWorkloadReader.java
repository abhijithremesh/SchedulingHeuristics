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
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BasicSyncStartWorkloadReader {

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
    private double previousClock;

    int heuristicIndex = 1;
    int j=1;

    public static void main(String[] args) throws Exception {
        new BasicSyncStartWorkloadReader();
    }

    private BasicSyncStartWorkloadReader()  {

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();

        broker0 = new DatacenterBrokerSimple(simulation);

        createCloudletsFromWorkloadFile();
        limitCloudlets(5);
        modifyCloudlets();
        modifySubmissionTimes();
        showCloudletsDetails();
        cloudletList.removeIf(c -> c.getNumberOfPes() > 64 );    // removing cloudlets which requires more than 64 PEs

        createVms();

        datacenter0 = createDatacenter();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        System.out.println("********************** SIMULATION STARTS WITH SYNC ***************************************************");

        simulation.addOnSimulationPauseListener(this::pauseSimulation);

        simulation.startSync();

        //boolean alreadyExecuted = false;

        while(simulation.isRunning()){

            simulation.runFor(INTERVAL);

            simulation.pause(INTERVAL + simulation.clock());

        }

        System.out.println("Total cloudlets processed: "+broker0.getCloudletFinishedList().size());
        System.out.println("Total simulation time: "+simulation.getLastCloudletProcessingUpdate());



    }



    private void pauseSimulation(EventInfo eventInfo) {
        System.out.println("simulation paused at "+simulation.clock());
        System.out.println("Cloudlets processed: "+broker0.getCloudletFinishedList().size());
        simulation.resume();
    }

    private Datacenter createDatacenter() {
        List<Host> hostList = createHosts(HOSTS);
        Datacenter datacenter = new DatacenterSimple(simulation, hostList);
        System.out.printf("# Added   %12d Hosts to %s%n", hostList.size(), datacenter);
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

    private void limitCloudlets(int n){

        List<Cloudlet> list = new ArrayList<>();

        for (int i=0; i<n; i++){
            list.add(cloudletList.get(i));
        }

        cloudletList = list;

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
        System.out.println("******************************************************");

    }

    private void modifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
        }
    }

    private void modifyCloudlets(){
        for (Cloudlet c: cloudletList
             ) {
            //c.setSubmissionDelay(0);
            //c.setNumberOfPes(1);
            //c.setLength(10000000);
        }
    }







}
