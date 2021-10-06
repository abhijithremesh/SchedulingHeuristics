/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.SchedullingHeuristics;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



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
public class BasicExample2 {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 100;

    private static final int VMS = 1;
    private static final int VM_PES = 100;

    private static final int CLOUDLETS = 28485;
    private static final int CLOUDLET_PES = 1;

    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int j = 1;

    ArrayList<Integer> cloudletLengths = new ArrayList<>();
    ArrayList<Integer> cloudletPEs = new ArrayList<>();
    ArrayList<Double> cloudletSubmissionTimes = new ArrayList<>();
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args) throws Exception {
        new BasicExample2();
    }

    private BasicExample2() throws Exception  {

        Log.setLevel(Level.OFF);

/*

        // Reading Benchmark
        File file1 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/benchmark_lengths");
        File file2 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/benchmark_reqPEs");
        File file3 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/benchmark_submit_times");

        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        String st1;
        while ((st1 = br1.readLine()) != null)
            cloudletLengths.add(Integer.parseInt(st1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        String st2;
        while ((st2 = br2.readLine()) != null)
            cloudletPEs.add(Integer.parseInt(st2));
        BufferedReader br3 = new BufferedReader(new FileReader(file3));
        String st3;
        while ((st3 = br3.readLine()) != null)
            cloudletSubmissionTimes.add(Double.parseDouble(st3));

        System.out.println("Maximum PE: "+Collections.max(cloudletPEs));
        System.out.println("Minimum PE: "+Collections.min(cloudletPEs));



 */
        // Reading Sample
        File file4 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/sample_lengths");
        File file5 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/sample_reqPEs");
        File file6 = new File("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/SchedullingHeuristics/sample_submit_times");

        BufferedReader br4 = new BufferedReader(new FileReader(file4));
        String st4;
        while ((st4 = br4.readLine()) != null)
            cloudletLengths.add(Integer.parseInt(st4));
        BufferedReader br5 = new BufferedReader(new FileReader(file5));
        String st5;
        while ((st5 = br5.readLine()) != null)
            cloudletPEs.add(Integer.parseInt(st5));
        BufferedReader br6 = new BufferedReader(new FileReader(file6));
        String st6;
        while ((st6 = br6.readLine()) != null)
            cloudletSubmissionTimes.add(Double.parseDouble(st6));



        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        datacenter0.setSchedulingInterval(2);

        broker0 = new DatacenterBrokerSimple(simulation);

        //ArrayList<String[]> workloadEntries = getWorkloadEntries();
        //cloudletList = createKTHWorloadCloudlets(workloadEntries, 2000);

        vmList = createVms();
        broker0.submitVmList(vmList);


        cloudletList = createCloudlets();

        broker0.submitCloudletList(cloudletList);


        /*
        for (Cloudlet c : cloudletList) {
            System.out.println(" | "+c.getId()+" | "+c.getLength() + "|" +c.getNumberOfPes()+" | "+c.getSubmissionDelay()+" | "+c.getWaitingTime()+" | "+c.getExecStartTime());
        }
         */


        System.out.println("********************** SIMULATION STARTS ***************************************************");


        //simulation.addOnClockTickListener(this::pauseSimulationAtSpecificTime);
        simulation.addOnEventProcessingListener(this::pauseSimulationAtSpecificTime);
        simulation.addOnSimulationPauseListener(this::sayHi);


        simulation.start();

        //System.out.println(simulation.getLastCloudletProcessingUpdate());

        //System.out.printf("%n"+broker0.getCloudletFinishedList().get(broker0.getCloudletFinishedList().size()-1).getFinishTime());

        //final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        //new CloudletsTableBuilder(finishedCloudlets)
        //    .build();

        ArrayList<Long> PeList = new ArrayList<>();

        System.out.println("cloudletListSize: "+cloudletList.size());
        System.out.println("No. of Cloudlets processed: "+broker0.getCloudletFinishedList().size());
        System.out.println("Last Cloudlet processed time: "+simulation.getLastCloudletProcessingUpdate());
        System.out.println("Last Cloudlet Finished time: "+broker0.getCloudletFinishedList().get(broker0.getCloudletFinishedList().size()-1).getFinishTime());

        for (Cloudlet c:broker0.getCloudletFinishedList()) {
            PeList.add(c.getNumberOfPes());
        }

        System.out.println("Maximum PE: "+Collections.max(PeList));
        System.out.println("Minimum PE: "+Collections.min(PeList));

        List<Cloudlet> finishedList = broker0.getCloudletFinishedList();
        List<Cloudlet> submittedList = broker0.getCloudletSubmittedList();

        System.out.println("FinishedCloudlets: "+broker0.getCloudletFinishedList().size());
        System.out.println("SubmittedCloudlets: "+broker0.getCloudletSubmittedList().size());

        List<Cloudlet> differences = new ArrayList<Cloudlet>(submittedList);
        differences.removeAll(finishedList);

        System.out.println(differences.size());

        //for (Cloudlet c: differences
        //     ) {
        //    System.out.println(c.getId()+" "+c.getNumberOfPes()+" "+c.getLength()+" "+c.getSubmissionDelay());
        //}

        /*
        for (Cloudlet c : broker0.getCloudletFinishedList()) {
            System.out.println(" | "+c.getId()+" | "+c.getLength() + "|" +c.getNumberOfPes()+" | "+c.getSubmissionDelay()+" | "+c.getWaitingTime()+" | "+c.getExecStartTime());
        }

         */

    }

    /**
     * Creates a Datacenter and its Hosts.
     */
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

    /**
     * Creates a list of VMs.
     */
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

    /**
     * Creates a list of Cloudlets.
     */


    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            int len = cloudletLengths.get(i);
            //int pe =1;
            int pe = cloudletPEs.get(i);
            double submission_delays = cloudletSubmissionTimes.get(i);
            if (len > 0) {
                //final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, pe, utilizationModel);
                final Cloudlet cloudlet = new CloudletSimple(len, pe, utilizationModel);
                cloudlet.setSubmissionDelay(submission_delays);
                cloudlet.setSizes(1024);
                list.add(cloudlet);
            }

        }

        return list;
    }



    /*
    private static ArrayList<Integer> getCloudletLength(){

        ArrayList<Integer> seed = new ArrayList<Integer>();

        System.out.println(System.getProperty("user.dir")+"/sample");
        File fobj = new File(System.getProperty("user.dir")+"/sample");


        return
    }

     */


    public void sayHi(EventInfo pauseInfo) {
        System.out.printf("%n# Simulation paused at %.2f second%n", pauseInfo.getTime());
        System.out.println(broker0.getCloudletFinishedList().size());
        //postSimulationHeuristicSpecificFinishedCloudlets(broker0);

        if (j< 24){
            System.out.println("Heuristic switched!!!!!");

        }

        j++;
        simulation.resume();
        System.out.println("Simulation resumed");

    }

    public void pauseSimulationAtSpecificTime(EventInfo eInfo) {

        if (Math.floor(simulation.clock()) == 3600 * j) {

            simulation.pause();

            //System.out.println("# Simulation paused at %.2f second%n"+eInfo.getTime());
        }
    }

    public void postSimulationHeuristicSpecificFinishedCloudlets(DatacenterBroker brokerh){

        List<Cloudlet> allFinishedCloudlets = brokerh.getCloudletFinishedList();
        heuristicSpecificFinishedCloudletsList.add(allFinishedCloudlets);
        int items = heuristicSpecificFinishedCloudletsList.size();
        List<Cloudlet> heuristicSpecificFinishedCloudlets = new ArrayList<Cloudlet>();
        //if (brokerh.getCloudletSubmittedList().size() > brokerh.getCloudletFinishedList().size()) {
        if (items == 1) {
            heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(0);
        } else if (items > 1) {
            List<Cloudlet> lastItem = heuristicSpecificFinishedCloudletsList.get(items - 1);
            List<Cloudlet> secondLastItem = heuristicSpecificFinishedCloudletsList.get(items - 2);
            List<Cloudlet> differences = new ArrayList<>(lastItem);
            differences.removeAll(secondLastItem);
            //heuristicSpecificFinishedCloudletsList.get(items - 1).removeAll(heuristicSpecificFinishedCloudletsList.get(items - 2));
            //heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(items - 1);
            heuristicSpecificFinishedCloudlets = differences;
        }
        //}
        System.out.println("Heuristic Cloudlets processed: "+heuristicSpecificFinishedCloudlets.size());
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }


    /*

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
            int reqPEs = Integer.parseInt(workloadEntries.get(i)[8]);
            long submitTime = submitTime(workloadEntries.get(i)[3]);

            if (cloudletLength > 0){
                final Cloudlet cloudlet = new CloudletSimple(cloudletLength, reqPEs, utilizationModel);
                cloudlet.setSizes(1024);
                //cloudlet.setExecStartTime();
                //cloudlet.setSubmissionDelay(submitTime);
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

     */

}
