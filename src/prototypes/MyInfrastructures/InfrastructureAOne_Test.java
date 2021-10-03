package org.cloudsimplus.examples.MyInfrastructures;


import ch.qos.logback.classic.Level;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
public class InfrastructureAOne_Test {

    private static final double INTERVAL = 3600;

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;
    private static final int HOST_RAM = 20000;
    private static final int HOST_SIZE = 1000000;
    private static final int HOST_BW = 100000;

    private static final int VMS = 25;
    private static final int VM_PES = 2;
    private static int VM_RAM = 1000;  // 128 - 15360
    private static int VM_BW = 1000; // 128 - 15360
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 256;

    private static final int CLOUDLETS = 500;   // 100-1000
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 2000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    int heuristicIndex = 1;

    List<Integer> VM_MIPS_list = new ArrayList<Integer>(){{
        add(500);
        add(1000);
        add(2500);
        add(5000);
        add(7500);
        add(10000);
        add(20000);
        add(30000);
    }};

    public static void main(String[] args) {
        new InfrastructureAOne_Test();
    }

    private InfrastructureAOne_Test() {

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        datacenter0.setSchedulingInterval(0.5);

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();

        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile(50);
        //nullifySubmissionTimes();
        modifySubmissionTimes();
        //modifyLength();  // sets length = length * npe
        //modifyReqPes();  // sets the reqPE as 1

        broker0.submitCloudletList(cloudletList);
        broker0.submitVmList(vmList);

        simulation.addOnClockTickListener(this::pauseSimulation);
        simulation.addOnSimulationPauseListener(this::resumeSimulation);

        simulation.start();

        System.out.println(broker0.getCloudletFinishedList().size());
        System.out.println(simulation.getLastCloudletProcessingUpdate());

        System.out.println(broker0.getVmCreatedList().size());

    }

    public void resumeSimulation(EventInfo pauseInfo) {
        simulation.resume();
        heuristicIndex++;
        System.out.println("Heuristics switched....");
        System.out.println("simulation resumed...");
    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex ){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
        }
    }


    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(75000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPS_list.get(i%7);
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        Random r = new Random();
        for (int i = 0; i < CLOUDLETS; i++) {
            int l = r.nextInt(2000-1000) + 1000;
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + l , CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setSubmissionDelay(0);
            list.add(cloudlet);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile(int count) {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1);
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

    private void nullifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(0);
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

}

