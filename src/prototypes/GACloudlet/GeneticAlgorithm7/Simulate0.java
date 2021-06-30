package org.cloudsimplus.examples.GeneticAlgorithm7;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Simulate0 {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 5;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private CloudSim simulation;

    private DatacenterBroker broker;

    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private Datacenter datacenter;

    public static void main(String[] args) throws IOException, ParseException {
        new Simulate0();
    }

    private Simulate0() throws IOException, ParseException {

        Log.setLevel(Level.OFF);

        GA0 ga = new GA0();

        ArrayList<String[]> workloadEntries = getWorkloadEntries();
        cloudletList = createKTHWorloadCloudlets(workloadEntries, 100);

        displayCloudletLength(cloudletList);
        System.out.println("cloudlet size: "+cloudletList.size());

        //ArrayList<ArrayList> chromosomeList = ga.createInitialPopulation(CLOUDLETS, VMS);
        ArrayList<ArrayList> chromosomeList = ga.createInitialPopulation(cloudletList.size(), VMS);

        ArrayList<Double> generationFitness = new ArrayList<Double>();

        System.out.println("chromosomeList: "+chromosomeList);
        System.out.println("chromosomeList size: "+chromosomeList.size());
        System.out.println("Single chromosome size: "+chromosomeList.get(0).size());

        for (int generations = 0; generations < 25; generations++) {

            System.out.println("Generation: " + generations);

            ArrayList<Double> fitnessList = new ArrayList<Double>();

            for (int i = 0; i < chromosomeList.size(); i++) {

                ArrayList<Integer> chromosome = chromosomeList.get(i);

                simulation = new CloudSim();
                datacenter = createDatacenter();

                broker = new DatacenterBrokerSimple(simulation);

                vmList = createVms();
                //cloudletList = createCloudlets();
                cloudletList = createKTHWorloadCloudlets(workloadEntries, 100);

                broker.submitVmList(vmList);
                broker.submitCloudletList(cloudletList);

                for (int j = 0; j < chromosome.size(); j++) {

                    Cloudlet c = cloudletList.get(j);
                    Vm v = vmList.get(chromosome.get(j));
                    broker.bindCloudletToVm(c, v);

                }

                simulation.start();

                double tft = ga.calculateFitnessFunctionFour(broker);

                fitnessList.add(tft);

            }

            System.out.println("Chromosome List: " + chromosomeList);
            System.out.println("Fitness List: " + fitnessList);
            System.out.println("chromosomeList size: " + chromosomeList.size());
            System.out.println("fitnessList size: " + fitnessList.size());

            double bestFitness = ga.generationFitness(fitnessList,"min");
            generationFitness.add(bestFitness);

            System.out.println("Generation Fitness: " + generationFitness);

            ArrayList<ArrayList> offspringsList = new ArrayList<ArrayList>();

            // If using Elite method for passing elite individuals to the next generation.
            ArrayList<ArrayList> eliteChromosomes = ga.fittestEliteChromosome(fitnessList, chromosomeList, 3, "min");

            ga.removeWeakChromosomes(fitnessList, chromosomeList, "max", 1);

            ga.generationEvolve(3,"min",chromosomeList,fitnessList,offspringsList);

            offspringsList.addAll(eliteChromosomes);

            chromosomeList.addAll(offspringsList);

        }

    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
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
            peList.add(new PeSimple(1500));
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
        int customMIPS = 0;
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            customMIPS = customMIPS + 100;
            final Vm vm = new VmSimple(1000 + customMIPS, VM_PES);
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
        int customLength = 0;
        for (int i = 0; i < CLOUDLETS; i++) {
            //Random random = new Random();
            //int randomLength = random.nextInt(500);
            customLength += 100;
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + customLength, CLOUDLET_PES, utilizationModel);
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

    private void displayCloudletLength (List<Cloudlet> cloudletList) {

        ArrayList<Long> cLengths = new ArrayList<Long>();
        ArrayList<Double> cSubmitTimes = new ArrayList<Double>();

        for (Cloudlet c : cloudletList) {
            cLengths.add(c.getLength());
            cSubmitTimes.add(c.getSubmissionDelay());
        }

        System.out.println("cloudlet Lengths: "+cLengths);
        System.out.println("cloudlet Submit Times: "+cSubmitTimes);
        System.out.println("cloudlet max submit time: "+Collections.max(cSubmitTimes));

    }

}



