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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SWIMBenchmark {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 10;

    private static final int VMS = 10;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 3;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) throws IOException {
        new SWIMBenchmark();
    }

    public SWIMBenchmark() throws IOException {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();

        ArrayList<List> SWIMWorload = getSWIMWorkload();
        cloudletList = createSWIMCloudlets(SWIMWorload);

        for (Cloudlet c: cloudletList) {
            System.out.println("Cloudlet ID: "+c.getId()+", Length: "+c.getLength()+", SubmitTime: "+c.getSubmissionDelay());
        }

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        for (Cloudlet c: cloudletList) {
            System.out.println("Cloudlet ID: "+c.getId()+", Length: "+c.getLength()+", SubmitTime: "+c.getSubmissionDelay());
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



        for (int i = 0; i <CLOUDLETS; i++) {
            //Random random = new Random();
            //int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    private ArrayList<List> getSWIMWorkload() throws IOException {
        ArrayList<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        BufferedReader TSVReader = new BufferedReader(new FileReader("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/FB-2009_samples_24_times_1hr_0.tsv"));
        String line = null;
        while ((line = TSVReader.readLine()) != null) {
            String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
            Data.add(lineItems); //adding the splitted line array to the ArrayList
        }

        ArrayList<List> entry = new ArrayList<List>();
        String sp = "";
        for (String[] s: Data) {
            sp = Arrays.toString(s);
            sp = sp.substring(1, sp.length() - 1);
            entry.add(Arrays.asList(sp.split(",")));
        }

        return entry;
    }

    private List<Cloudlet> createSWIMCloudlets(ArrayList<List> entry) {
        final List<Cloudlet> list = new ArrayList<>(entry.size());
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i=0; i < entry.size();i++ ){
            long cloudletLength =  Long.parseLong(entry.get(i).get(3).toString().substring(1))+
                Long.parseLong(entry.get(i).get(4).toString().substring(1))+
                Long.parseLong(entry.get(i).get(5).toString().substring(1))     ;
            final Cloudlet cloudlet = new CloudletSimple(cloudletLength+1, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setSubmissionDelay(Double.parseDouble(entry.get(i).get(1).toString().substring(1)));
            list.add(cloudlet);

        }
        return list;
    }


}
