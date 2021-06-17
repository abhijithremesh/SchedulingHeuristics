package org.cloudsimplus.examples.KTHWorkload;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.BasicFirstExample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedDirectoryStreamException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KTHBenchmark {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 8;

    private static final int VMS = 2;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 3;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) throws IOException, ParseException {
        new KTHBenchmark();
    }

    private KTHBenchmark() throws ParseException, IOException {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);
        ArrayList<String[]> workloadEntries = getWorkloadEntries();
        cloudletList = createKTHWorloadCloudlets(workloadEntries,50);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        /*

        ArrayList<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        BufferedReader TSVReader = new BufferedReader(new FileReader("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/KTHWorkload/KTH-SP2-1996-0"));
        String line = null;
        while ((line = TSVReader.readLine()) != null) {
            String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
            Data.add(lineItems); //adding the splitted line array to the ArrayList
        }

        //SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");


        ArrayList<String[]> entry = new ArrayList<String[]>();
        String [] st;
        String sp = "";
        for (String[] s: Data) {
            sp = Arrays.toString(s);
            sp = sp.substring(1, sp.length() - 1);
            sp = sp.replaceAll("\\s+",",");
            if (sp.length()>5){
                sp = sp.substring(1, sp.length() - 1);
                st = sp.split(",");
                st[4] = st[4] + " " + st[5];  // combining date and time
                st[6] = st[6] + " " + st[7];  // combining date and time
                if (st[2].length() == 16){
                    st[3] = st[2].substring(8,st[2].length()-2);
                    st[3] = st[3].replaceAll("..(?!$)", "$0:");
                    entry.add(st);
                }
            }
        }

         */



        //System.out.println(workloadEntries.size());

        vmList = createVms();
        //cloudletList = createCloudlets();
        //cloudletList = createKTHWorloadCloudlets(workloadEntries,50);

        System.out.println(cloudletList.size());

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        /*
        System.out.println(entry.get(1)[2]);
        System.out.println(entry.get(1)[3]);
        System.out.println(entry.get(1)[4]);
        System.out.println(entry.get(1)[5]);
        System.out.println(entry.get(1)[6]);
        System.out.println(entry.get(1)[7]);
        System.out.println(entry.get(1)[8]);

         */



        /*
        Date date1 = datetimeFormatter.parse(entry.get(1)[4]);
        Date date2 = datetimeFormatter.parse(entry.get(1)[6]);
        System.out.println(date1);
        System.out.println(date2);
        long difference = (date2.getTime()-date1.getTime())/1000;
        long len = difference/Integer.parseInt(entry.get(1)[8]);
        System.out.println(difference);
        System.out.println(len);
         */

        /*
        long len = jobLength(entry.get(1)[6],entry.get(1)[4],entry.get(1)[8]);
        System.out.println(len);

        long submitTime = submitTime(entry.get(1)[3]);
        System.out.println(submitTime);
         */



        /*
        Date time = timeFormatter.parse(entry.get(1)[3]);
        System.out.println((time.getTime())/1000);
         */

        for (Cloudlet c : cloudletList){
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" SubmitTime: "+c.getSubmissionDelay());
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
            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

   private long jobLength(String startTime, String endTime, String pe) throws ParseException {

       SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       Date start = datetimeFormatter.parse(startTime);
       Date end = datetimeFormatter.parse(endTime);
       long difference =  ((start.getTime()-end.getTime())/1000);
       long len =  (difference/Integer.parseInt(pe));
       return len;

   }

    private long submitTime (String submitTime) throws ParseException {

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        Date time = timeFormatter.parse(submitTime);
        return time.getTime()/1000;

    }


    private List<Cloudlet> createKTHWorloadCloudlets(ArrayList<String[]> workloadEntries, int n) throws ParseException {

        final List<Cloudlet> list = new ArrayList<>(n);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < n; i++) {
            long cloudletLength = jobLength(workloadEntries.get(i)[6],workloadEntries.get(i)[4],workloadEntries.get(i)[8]);
            long submitTime = submitTime(workloadEntries.get(i)[3]);
            if (cloudletLength > 0){
                final Cloudlet cloudlet = new CloudletSimple(cloudletLength, CLOUDLET_PES, utilizationModel);
                cloudlet.setSizes(1024);
                cloudlet.setSubmissionDelay(submitTime);
                list.add(cloudlet);
            }
        }

        return list;
    }

    private ArrayList<String[]> getWorkloadEntries() throws IOException {

        ArrayList<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        BufferedReader TSVReader = new BufferedReader(new FileReader("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/KTHWorkload/KTH-SP2-1996-0"));
        String line = null;
        while ((line = TSVReader.readLine()) != null) {
            String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
            Data.add(lineItems); //adding the splitted line array to the ArrayList
        }

        ArrayList<String[]> entry = new ArrayList<String[]>();
        String [] st;
        String sp = "";
        for (String[] s: Data) {
            sp = Arrays.toString(s);
            sp = sp.substring(1, sp.length() - 1);
            sp = sp.replaceAll("\\s+",",");
            if (sp.length()>5){
                sp = sp.substring(1, sp.length() - 1);
                st = sp.split(",");
                st[4] = st[4] + " " + st[5];  // combining date and time
                st[6] = st[6] + " " + st[7];  // combining date and time
                if (st[2].length() == 16){
                    st[3] = st[2].substring(8,st[2].length()-2);
                    st[3] = st[3].replaceAll("..(?!$)", "$0:");
                    entry.add(st);
                }
            }
        }

        return entry;

    }





}
