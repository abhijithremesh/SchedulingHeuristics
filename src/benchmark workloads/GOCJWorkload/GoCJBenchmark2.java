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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoCJBenchmark2 {
    
    private static final int HOSTS = 1;
    private static final int HOST_PES = 5;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 4;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) throws IOException {
        new GoCJBenchmark2();
    }

    public GoCJBenchmark2() throws IOException {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createGoCJCloudlets();

        //Configuring cloudlets wrt GoCJ Jobs
        //cloudletList = cloudletsGoCJ(cloudletList);


        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);


        for(int i =0;i<cloudletList.size();i++){
            broker0.bindCloudletToVm(cloudletList.get(i),vmList.get(i));
        }

        for(int i =0;i<cloudletList.size();i++){
            Cloudlet c = cloudletList.get(i);
            System.out.println("ID: "+c.getId()+" Time: "+c.getSubmissionDelay()+" Length: "+c.getLength());
        }


        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

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
            //Random random = new Random();
            //int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    private List<Cloudlet> cloudletsGoCJ(List<Cloudlet> cloudletList) throws IOException {

        GoCJ g = new GoCJ();
        final long[] cloudlets = g.createGoCJ(cloudletList.size());
        long[] cloudletsTime = new long[cloudletList.size()];

        int low = 0;
        int high = 2;

        for(int i =0;i<cloudletList.size();i++){
            java.util.Random r = new Random();
            cloudletsTime[i] = r.nextInt(high-low)+low;
            low=low+5;
            high = high+5;

            Cloudlet c = cloudletList.get(i);
            c.setLength(cloudlets[i]);
            c.setSubmissionDelay(cloudletsTime[i]);

            System.out.println("ID: "+c.getId()+" Time: "+c.getSubmissionDelay()+" Length: "+c.getLength());
        }

        return cloudletList;
    }

    private List<Cloudlet> createGoCJCloudlets() throws IOException {

        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        GoCJ g = new GoCJ();
        final long[] cloudlets = g.createGoCJ(CLOUDLETS);
        long[] cloudletsTime = new long[CLOUDLETS];

        int low = 0;
        int high = 2;

        for(int i =0; i<CLOUDLETS; i++){

            java.util.Random r = new Random();
            cloudletsTime[i] = r.nextInt(high-low)+low;
            low=low+5;
            high = high+5;

            final Cloudlet cloudlet = new CloudletSimple(cloudlets[i], CLOUDLET_PES, utilizationModel);
            cloudlet.setSubmissionDelay(cloudletsTime[i]);
            cloudlet.setSizes(1024);
            list.add(cloudlet);

            System.out.println("ID: "+cloudlet.getId()+" Time: "+cloudlet.getSubmissionDelay()+" Length: "+cloudlet.getLength());
        }

        return list;
    }

}
