package org.cloudsimplus.examples.MyHeuristics;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HeterogeneousOne {

    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz"; //HPC2N
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz"; //KTH
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz"; //NASA

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private static final int VMS = 50;
    private static int VM_MIPS = 1000;
    private static final int VM_PES = 2;
    private static long VM_SIZE = 10000; //in Megabytes
    private static int VM_RAM = 512; //in Megabytes
    private static long VM_BW = 1000; //in Megabytes

    private static final int HOSTS = 4;
    private final int HOST_PES = 2;
    private static final long HOST_SIZE = 1000000; //in Megabytes
    private static final int HOST_RAM = 20000; //in Megabytes
    private static final long HOST_BW = 10000; //in Megabytes

    private static final int CLOUDLETS = 100;

    private CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    MyBroker myBroker;



    public static void main(String[] args) {
        new HeterogeneousOne();
    }

    private HeterogeneousOne(){

        Log.setLevel(Level.INFO);

        simulation = new CloudSim();

        datacenter0 = createDatacenter();

        myBroker = new MyBroker(simulation);

        cloudletList = createCloudletsFromWorkloadFile();
        limitCloudlets(CLOUDLETS);
        modifySubmissionTimes();
        modifyCloudlets();
        //showCloudletsDetails();

        vmList = createVms();


        myBroker.submitVmList(vmList);
        myBroker.submitCloudletList(cloudletList);

        myBroker.selectSchedulingPolicy(1);

        simulation.start();

        System.out.println(myBroker.getVmCreatedList().size());
        System.out.println(myBroker.getVmWaitingList().size());
        System.out.println(myBroker.getVmExecList().size());
        final List<Cloudlet> finishedCloudlets = myBroker.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();


        System.out.println("Processed: "+myBroker.getCloudletFinishedList().size());
        System.out.println("Waiting: "+myBroker.getCloudletWaitingList().size());
        System.out.println("Makespan: "+myBroker.getCloudletFinishedList().get(myBroker.getCloudletFinishedList().size()-1).getFinishTime());






    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList); //Uses a VmAllocationPolicySimple by default to allocate VMs
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) { //List of Host's CPUs (Processing Elements, PEs)
            peList.add(new PeSimple(1000)); //Uses a PeProvisionerSimple by default to provision PEs for VMs
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());// ResourceProvisionerSimple by default for RAM and BW provisioning, VmSchedulerSpaceShared for VM scheduling.
        return h;
    }


    private List<Cloudlet> createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        return cloudletList;
    }


    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPS + i * 256;
            VM_RAM = VM_RAM + i * 256;
            VM_BW = VM_BW + i * 256;
            VM_SIZE = VM_SIZE + i * 256;
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());  //Uses a CloudletSchedulerTimeShared
            list.add(vm);
        }
        return list;
    }



    private void showCloudletsDetails(){
        for (Cloudlet c: cloudletList
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
            //c.setLength(2000);
            //c.setNumberOfPes(1);
        }
    }



}
