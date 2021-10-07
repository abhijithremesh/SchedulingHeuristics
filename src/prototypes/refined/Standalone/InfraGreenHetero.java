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
import java.util.List;

public class InfraGreenHetero {

    private static final double INTERVAL = 3600;

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

    private static int VM_MIPS = 1000;

    private static final int CLOUDLETS = 500;  // limit:1200
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 5000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = 80;
    //private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

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
        new InfraGreenHetero();
    }

    private InfraGreenHetero() {

        Log.setLevel(Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenterOne();
        datacenter1 = createDatacenterTwo();

        broker0 = new MyBroker(simulation);

        //vmList = createVmsSpaceShared();
        vmList = createVmsTimeShared();

        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile();
        modifySubmissionTimes();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        //broker0.Random(vmList);
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

        System.out.println(datacenter0.getHostList());
        System.out.println(datacenter1.getHostList());

        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.println("finished cloudlets: "+finishedCloudlets.size());

        System.out.println("vms created: "+broker0.getVmCreatedList().size());









    }

    private Datacenter createDatacenterOne() {
        final List<Host> hostListOne = new ArrayList<>(HOSTS_DUALCORE+HOSTS_QUADCORE);
        for(int i = 0; i < HOSTS_DUALCORE; i++) {
            Host host = createHostDualCore();
            host.setId(1);
            hostListOne.add(host);
        }
        for(int i = 0; i < HOSTS_QUADCORE; i++) {
            Host host = createHostQuadCore();
            host.setId(2);
            hostListOne.add(host);
        }
        return new DatacenterSimple(simulation, hostListOne, new VmAllocationPolicySimple());
    }

    private Datacenter createDatacenterTwo() {
        final List<Host> hostListTwo = new ArrayList<>(HOSTS_DUALCORE+HOSTS_QUADCORE);
        for(int i = 0; i < HOSTS_DUALCORE; i++) {
            Host host = createHostDualCore();
            host.setId(3);
            hostListTwo.add(host);
        }
        for(int i = 0; i < HOSTS_QUADCORE; i++) {
            Host host = createHostQuadCore();
            hostListTwo.add(host);
            host.setId(4);
        }
        return new DatacenterSimple(simulation, hostListTwo, new VmAllocationPolicySimple());
    }

    private Host createHostDualCore() {
        final List<Pe> peList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            peList.add(new PeSimple(500000)); //10000
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private Host createHostQuadCore() {
        final List<Pe> peList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            peList.add(new PeSimple(250000)); // 10000
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

    private void modifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
        }
    }





}
