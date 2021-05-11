package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PriorityBasedScheduling1 {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 1;

    private static final int VMS = 1;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 10;
    private static final int CLOUDLET_PES = 1;

    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private Datacenter datacenter0;
    private DatacenterBroker broker0;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    public static void main(String[] args) {
        new PriorityBasedScheduling1();
    }

    private PriorityBasedScheduling1(){

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        PriorityBasedSchedulingBroker1 broker0 = new PriorityBasedSchedulingBroker1(simulation);

        vmList = createVms(broker0);
        cloudletList = createCloudlets(broker0);

        // Submitting the VMlist to the broker
        broker0.submitVmList(vmList);

        Random random = new Random();

        // Assigning Random Priority to cloudlets
        for (Cloudlet c : cloudletList)
        {
            int randomPriority = random.nextInt(cloudletList.size());
            c.setPriority(randomPriority);
        }

        System.out.println("******* Before sorting based on Priority ***********");

        // Printing Cloudlets before sorting based on priority
        for (Cloudlet c : cloudletList) {
            System.out.println(" Priority of Cloudlet: "+c.getPriority());
        }

        // Function which handles the scheduling of cloudlets based on priority
        cloudletList = broker0.scheduleCloudlets(cloudletList);

        System.out.println("******* After sorting based on Priority ***********");

        // Printing Cloudlets after sorting based on priority
        for (Cloudlet c : cloudletList) {
            System.out.println(" Priority of Cloudlet: "+c.getPriority());
        }

        // Submitting the Cloudletlist to the broker
        broker0.submitCloudletList(cloudletList);


        simulation.start();


        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();

        new CloudletsTableBuilder(finishedCloudlets)
            .build();



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
        //Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        //VmSchedulerSpaceShared for VM scheduling.
        Host host = new HostSimple(ram, bw, storage, peList);
        host.setVmScheduler(new VmSchedulerSpaceShared());
        return host;
    }

    private List<Vm> createVms(PriorityBasedSchedulingBroker1 broker0) {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Random random = new Random();
            int randomMips = random.nextInt(500);
            final Vm vm = new VmSimple(randomMips, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            vm.setBroker(broker0);
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets(PriorityBasedSchedulingBroker1 broker0) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            Random random = new Random();
            int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH+randomLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setBroker(broker0);
            list.add(cloudlet);
        }
        return list;
    }


}
