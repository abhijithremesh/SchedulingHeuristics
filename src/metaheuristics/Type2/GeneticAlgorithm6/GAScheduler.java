package org.cloudsimplus.examples.GeneticAlgorithm6;

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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.RandomBroker;

import javax.swing.plaf.synth.SynthUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GAScheduler {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 3;

    private static final int VMS = 3;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 5;
    private static final int CLOUDLET_PES = 1;

    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private Datacenter datacenter0;
    private DatacenterBroker broker0;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    public static void main(String[] args) {
        new GAScheduler();
    }

    private GAScheduler(){

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        //cloudletList = sortCloudletAscendingLength((cloudletList));
        //vmList = sortVmDescendingMIPS(vmList);

        //cloudletList.forEach(c -> System.out.println(c.getLength()));
        //vmList.forEach(v -> System.out.println(v.getMips()));

        //ArrayList<Chromosomes> initialPopulation = generateInitialPopulation1();
        ArrayList<Chromosomes> initialPopulation = generateInitialPopulation2();

        initialPopulation.forEach(i -> i.printChromosome());
        System.out.println("");

        int populationSize = initialPopulation.size();

        System.out.println("populationSize: "+populationSize);

        Random random = new Random();

        System.out.println("Starting Generations.....");

        for(int generations=0; generations<10; generations++) {

            System.out.println("Generation: "+generations);

            // Printing the initial population of chromosomes
            initialPopulation.forEach(i -> i.printChromosome());
            System.out.println("");

            int index1 = random.nextInt(populationSize);
            int index2 = random.nextInt(populationSize);
            System.out.println("index1: " + index1);
            System.out.println("index2: " + index2);

            // l1: A random chromosome from the population
            ArrayList<Gene> l1 = new ArrayList<Gene>();
            l1 = initialPopulation.get(index1).getChromsome();

            // chromsome1: A copy of l1
            Chromosomes chromosome1 = new Chromosomes(l1);
            System.out.println("chromosome1: ");
            chromosome1.printChromosome();
            System.out.println("");

            // l2: A random chromosome from the chromosome list
            ArrayList<Gene> l2 = new ArrayList<Gene>();
            l2 = initialPopulation.get(index2).getChromsome();

            // chromsome2: A copy of l2
            Chromosomes chromosome2 = new Chromosomes(l2);
            System.out.println("chromosome2: ");
            chromosome2.printChromosome();
            System.out.println("");

            // Computing mutation Probability and Crossover probability
            double rangeMin = 0.0f;
            double rangeMax = 1.0f;
            double crossProb = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
            double mutProb = rangeMin + (rangeMax - rangeMin) * random.nextDouble();

            //System.out.println("rangeMin: " + rangeMin);
            //System.out.println("rangeMax: " + rangeMax);
            System.out.println("crossProb: " + crossProb);
            System.out.println("mutProb: " + mutProb);

            if (crossProb < 1) {

                // Getting two random indices: i,j
                int i, j;
                i = random.nextInt(cloudletList.size());
                j = random.nextInt(cloudletList.size());
                System.out.println("i: " + i +", j: "+ j);

                // Getting vm1 from the ith gene of l1 chromosome
                Vm vm1 = l1.get(i).getVmFromGene();
                // Getting vm2 from the jth gene of l2 chromosome
                Vm vm2 = l2.get(j).getVmFromGene();
                System.out.println("vm1: " + vm1 +", vm2: "+ vm2);

                // Updating the ith gene of chromsome1 with vm2
                chromosome1.updateGene(i, vm2);
                System.out.println("chromsome1: ");
                chromosome1.printChromosome();
                System.out.println("");

                // Updating the jth gene of chromsome2 with vm1
                chromosome2.updateGene(j, vm1);
                System.out.println("chromosome2: ");
                chromosome2.printChromosome();
                System.out.println("");

                // Updating the initial population
                initialPopulation.set(index1, chromosome1);
                initialPopulation.set(index2, chromosome2);

            }

            if (mutProb < 0.5) {

                int i;
                int j;

                i = random.nextInt(populationSize);

                // l: A random chromosome from the population
                ArrayList<Gene> l = new ArrayList<Gene>();
                l = initialPopulation.get(i).getChromsome();

                // chromosomeMutated: A copy of l
                Chromosomes chromosomeMutated = new Chromosomes(l);
                chromosomeMutated.printChromosome();
                System.out.println("");

                // Updating a random gene of chromosomeMutated with a VM
                j = random.nextInt(cloudletList.size());
                Vm vm1 = vmList.get(0);
                chromosomeMutated.updateGene(j, vm1);

                //Updating the initial population
                initialPopulation.set(i, chromosomeMutated);

            }

            initialPopulation.forEach(i -> i.printChromosome());
            System.out.println("");

            System.out.println("*******************************************");

        }

        System.out.println("Computing fitness of chromosome..........");

        int fittestIndex = 0;
        double time = 100000000;
        ArrayList<Double> fitnessList = new ArrayList<Double>();

        for(int i =0; i<populationSize; i++){

            double sum =0.0;

            ArrayList<Gene> l = new ArrayList<Gene>();
            l = initialPopulation.get(i).getChromsome();

            Chromosomes chromo = new Chromosomes(l);
            chromo.printChromosome();
            System.out.println("");

            for(int j=0; j<cloudletList.size();j++){
                Gene g = l.get(j);
                Cloudlet c = g.getCloudletFromGene();
                Vm v = g.getVmFromGene();
                double temp = c.getLength()/(v.getMips()*v.getNumberOfPes());
                sum += temp;
            }

            fitnessList.add(sum);

            if(sum<time){
                time = sum;
                fittestIndex = i;
            }

            System.out.println("fitnessList: "+fitnessList);
            System.out.println("fittestIndex: "+fittestIndex);

        }

        ArrayList<Gene> result = new ArrayList<Gene>();
        result = initialPopulation.get(fittestIndex).getChromsome();

        Chromosomes fittestChromosome = new Chromosomes(result);
        System.out.println("Fittest Chromosome: ");
        fittestChromosome.printChromosome();
        System.out.println("");

        List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
        List<Vm> finalvmlist = new ArrayList<Vm>();

        for(int i=0;i<result.size();i++) {

            finalcloudletList.add(result.get(i).getCloudletFromGene());
            finalvmlist.add(result.get(i).getVmFromGene());

        }

        System.out.println("Final Cloudlet List: "+finalcloudletList);
        System.out.println("Final VM List: "+finalvmlist);

        for (int i=0; i< finalcloudletList.size(); i++){
            Cloudlet c = finalcloudletList.get(i);
            Vm v =finalvmlist.get(i);
            broker0.bindCloudletToVm(c,v);
        }


        simulation.start();

        //for(Cloudlet c : broker0.getCloudletSubmittedList()){
        //    System.out.println("Cloudlet "+c.getId()+" of length "+c.getLength() +" is mapped to VM "+c.getVm().getId());
        //}

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

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            java.util.Random random = new java.util.Random();
            int randomMips = random.nextInt(500);
            final Vm vm = new VmSimple(randomMips, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            vm.setBroker(broker0);
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            java.util.Random random = new java.util.Random();
            int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH+randomLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlet.setBroker(broker0);
            list.add(cloudlet);
        }
        return list;
    }

    private List<Cloudlet> sortCloudletAscendingLength(List<Cloudlet> cloudletList) {

        // Sorting the list of cloudlets in ascending order of their length.
        for (int a = 0; a < cloudletList.size(); a++) {
            for (int b = a + 1; b < cloudletList.size(); b++) {
                if (cloudletList.get(b).getLength() < cloudletList.get(a).getLength()) {
                    Cloudlet temp = cloudletList.get(a);
                    cloudletList.set(a, cloudletList.get(b));
                    cloudletList.set(b, temp);
                }
            }
        }
        return cloudletList;
    }

    private List<Vm> sortVmDescendingMIPS (List<Vm> vmList) {

        // Sorting the list of Vms in descending order of their MIPS.
        for (int a = 0; a < vmList.size(); a++) {
            for (int b = a + 1; b < vmList.size(); b++) {
                if (vmList.get(b).getMips() > vmList.get(a).getMips()) {
                    Vm temp = vmList.get(a);
                    vmList.set(a, vmList.get(b));
                    vmList.set(b, temp);
                }
            }
        }
        return vmList;
    }

    private ArrayList<Chromosomes> generateInitialPopulation1(){

        ArrayList<Chromosomes> initialPopulation = new ArrayList<Chromosomes>();

        for (int j =0; j< cloudletList.size(); j++){

            ArrayList<Gene> firstChromosome = new ArrayList<Gene>();

            for (int i =0; i < cloudletList.size(); i++){
                int k = (i+j)%vmList.size();
                k = (k + cloudletList.size())%cloudletList.size();
                //System.out.println("j: "+j+" ,i: "+i+" ,k: "+k);
                Gene g = new Gene(cloudletList.get(i),vmList.get(k));
                firstChromosome.add(g);
            }

            //firstChromosome.forEach(g -> g.printGene());
            //System.out.println("");

            Chromosomes chromosome = new Chromosomes(firstChromosome);
            initialPopulation.add(chromosome);

        }

        return initialPopulation;

    }


    private ArrayList<Chromosomes> generateInitialPopulation2(){

        ArrayList<Chromosomes> initialPopulation = new ArrayList<Chromosomes>();

        for (int j =0; j< cloudletList.size(); j++){

            ArrayList<Gene> firstChromosome = new ArrayList<Gene>();

            for (int i =0; i < cloudletList.size(); i++){
                Random random = new Random();
                int k = random.nextInt(vmList.size());
                //System.out.println("j: "+j+" ,i: "+i+" ,k: "+k);
                Gene g = new Gene(cloudletList.get(i),vmList.get(k));
                firstChromosome.add(g);
            }

            firstChromosome.forEach(g -> g.printGene());
            System.out.println("");

            Chromosomes chromosome = new Chromosomes(firstChromosome);
            initialPopulation.add(chromosome);

        }

        return initialPopulation;

    }




}
