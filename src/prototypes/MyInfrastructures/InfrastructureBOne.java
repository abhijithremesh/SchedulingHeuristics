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
public class InfrastructureBOne {

    private static final int HOSTS_DUALCORE = 2;
    private static final int HOSTS_QUADCORE = 2;
    private static final int HOST_PES = 2;
    private static final int HOST_RAM = 20000;
    private static final int HOST_SIZE = 1000000;
    private static final int HOST_BW = 10000;

    private static final int VMS = 10;
    private static final int VM_PES = 1;
    private static int VM_RAM = 512;
    private static int VM_BW = 1000;
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 1000;

    private static final int CLOUDLETS = 10000;  // limit:3000
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 2000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new InfrastructureBOne();
    }

    private InfrastructureBOne() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        //cloudletList = createCloudletsFromWorkloadFile();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        vmList.forEach(v-> System.out.println(v.getId()+": "+v.getRam()+" "+v.getBw()+" "+v.getMips()));

        System.out.println(finishedCloudlets.size());





    }

    /**
     * Creates a Datacenter and its Hosts.
     */
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
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHostDualCore() {
        final List<Pe> peList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            peList.add(new PeSimple(10000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private Host createHostQuadCore() {
        final List<Pe> peList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            peList.add(new PeSimple(10000));
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
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */

    private List<Cloudlet> createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, VM_MIPS);
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

    private void statusOfCloudletsVMs(){
        System.out.println("VMs");
        System.out.println("Created: "+broker0.getVmCreatedList().size());
        System.out.println("Executing: "+broker0.getVmExecList().size());
        System.out.println("Waiting: "+broker0.getVmWaitingList().size());
        System.out.println(vmList.get(0).getId());

        System.out.printf("%n************************************%n");

        System.out.println("Cloudlets");
        System.out.println("Created: "+broker0.getCloudletCreatedList().size());
        System.out.println("Submitted: "+broker0.getCloudletSubmittedList().size());
        System.out.println("Waiting: "+broker0.getCloudletWaitingList().size());
        System.out.println("Finished: "+broker0.getCloudletFinishedList().size());
        System.out.println(cloudletList.get(0).getId());


    }

    private void limitCloudlets(int n){

        List<Cloudlet> list = new ArrayList<>();

        for (int i=0; i<n; i++){
            list.add(cloudletList.get(i));
        }

        cloudletList = list;
        System.out.println("Limited to "+cloudletList.size());

    }

    private void modifySubmissionTimes() {

        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
        }
    }


}

