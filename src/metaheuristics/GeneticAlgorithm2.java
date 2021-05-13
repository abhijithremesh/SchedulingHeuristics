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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm2 {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 4;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 50;
    private static final int CLOUDLET_PES = 1;

    private static final int CLOUDLET_LENGTH = 1000;

    private final CloudSim simulation;
    private Datacenter datacenter0;
    private DatacenterBroker broker0;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    public static void main(String[] args) {
        new GeneticAlgorithm2();
    }

    private GeneticAlgorithm2() {

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();

        int num_cloudlet = cloudletList.size();
        int num_vm = vmList.size();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        // Creating a container List to store Chromosomes
        ArrayList<ArrayList> chromosomeList = new ArrayList<ArrayList>();

        // Creating a container List to store the fitness
        ArrayList<Double> fitnessList = new ArrayList<Double>();

        // Creating a temporary Variable to store the chromosome
        ArrayList<Integer> chromosome = new ArrayList<Integer>();

        // Creating a container List to store the final best makespan value of each generation
        ArrayList<Double> generationMakespan = new ArrayList<Double>();

        System.out.println("Initializing a population of 100 chromosomes");
        // Creating initial Population of chromosomes
        for(int i=0;i<100;i++) {
            chromosome = createChromosome(num_cloudlet,num_vm);
            chromosomeList.add(chromosome);
        }
        System.out.println("chromosomeList size: "+chromosomeList.size());

        double fitness;
        double length;
        double filesize;
        int indexofFittestChromosome = 0;
        int generation=10;
        Random rand = new Random();

        System.out.println("Running the algorithm for 10 generations");

        // Running the algorithm for 10 generations
        for(int count=1;count<=generation;count++) {

            System.out.println("Generation: "+ count);

            // Calculating fitness of each chromosome and adding the fitness value to the fitness list
            for(int i=0;i<num_cloudlet;i++) {
                chromosome = chromosomeList.get(i);
                fitness = calculateFitness(chromosome,num_vm,cloudletList,300,1,1000,1000);
                fitnessList.add(fitness);
            }

            System.out.println("Maximum Fitness (VM) from each chromosome");
            System.out.println("FitnessList: "+ fitnessList);
            System.out.println("FitnessList Size: "+ fitnessList.size());
            System.out.println("Fittest Chromosome (Minimum) of Generation "+count+" is having makespan of "+ Collections.min(fitnessList));
            generationMakespan.add(Collections.min(fitnessList));

            // Getting the minimum fitness value and its index from the fitness list
            // This index corresponds to the index of the most fittest chromosome
            double minfitness=fitnessList.get(0);
            for(int i = 0;i<fitnessList.size();i++) {
                if(fitnessList.get(i)<minfitness) {
                    minfitness=fitnessList.get(i);
                    indexofFittestChromosome=i;
                }
            }

            System.out.println("Index of Fittest Chromosome of Generation "+count+": "+ indexofFittestChromosome);
            System.out.println("Fittest Chromosome of Generation ");

            // Getting the most fittest chromosome from the chromosome list wrt the index
            for (int i = 0; i < chromosomeList.get(indexofFittestChromosome).size(); i++) {
                System.out.print(chromosomeList.get(indexofFittestChromosome).get(i)+" ");
                if(i==chromosomeList.get(indexofFittestChromosome).size()-1) {
                    System.out.println("");
                }
            }

            System.out.println("FitnessList size before discarding: "+ fitnessList.size());
            System.out.println("ChromosomeList size before discarding: "+ chromosomeList.size());

            // Reducing the fitness list to half, quarter to remove weak chromsomes from the chromosome list
            int fitnessListHalf = (int)fitnessList.size()/2;
            System.out.println("FitnessList size half: "+ fitnessListHalf);

            if(count<generation) {

                // Discarding weak chromosomes (half/quarter) by removing the chromosomes which have higher fitness values
                int index;
                for (int i = 0; i < fitnessListHalf; i++) {
                    index = fitnessList.indexOf(Collections.max(fitnessList));
                    System.out.println("Removing Index: "+index);
                    fitnessList.remove(index);
                    chromosomeList.remove(index);
                }
                System.out.println("ChromosomeList after discarding: "+ chromosomeList.size());

                // Creating offsprings (half/quarter) and a container to store the offsprings
                // Offsprings created through SinglePoint, Uniform or Random cross over operation
                ArrayList<ArrayList> offspringList = new ArrayList<ArrayList>();
                for(int i = 0;i<fitnessListHalf;i++) {
                    offspringList.add(SinglePointCrossover(chromosomeList.get(rand.nextInt(chromosomeList.size())),chromosomeList.get(rand.nextInt(chromosomeList.size())),num_vm));
                    //offspringList.add(SinglePointCrossover(chromosomeList.get(indexofFittestChromosome),chromosomeList.get(rand.nextInt(chromosomeList.size()))));
                }

                System.out.println("No. of Offsprings being added: "+ offspringList.size());

                /*
                // Mutating the offspring chromosome
                				ArrayList<Integer> tempOffspring=new ArrayList<Integer>();
                			for(int i=0;i<offspringList.size();i++)
                				{
                					tempOffspring = Mutation(offspringList.get(i),vmSize);
                					offspringList.set(i,tempOffspring);
                				}
                 */


                // Appending the created offsprings to the chromosome list
                chromosomeList.addAll(offspringList);
                System.out.println("ChromosomeList size @ Gen end: "+ chromosomeList.size());

                offspringList.clear();
                fitnessList.clear();
            }

            System.out.println("********************************************************************************************************************************************************");

            System.out.println("Makespans of all Generations: "+generationMakespan);
        }

        Cloudlet c;
        int vm;
        ArrayList<Integer> solution = new ArrayList<Integer>();
        solution = chromosomeList.get(indexofFittestChromosome);

        System.out.println("Solution: "+solution);


        for(int i=0;i<cloudletList.size();i++) {
            c = cloudletList.get(i);
            vm = solution.get(i);
            //c.setVm(vmList.get(vm));
            broker0.bindCloudletToVm(c,vmList.get(vm));
        }

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
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
        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(100).setSize(10000);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            //vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            Random random = new Random();
            int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH+randomLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }
        return list;
    }

    private static int getNum(ArrayList<Integer> v) {

        // Size of the vector
        int n = v.size();

        // Make sure the number is within the index range
        int index = (int)(Math.random() * n);

        // Get random number from the vector
        int num = v.get(index);

        // Remove the number from the vector
        v.set(index, v.get(n - 1));
        v.remove(n - 1);

        // Return the removed number
        return num;
    }

    // Function to generate n non-repeating random numbers
    private static ArrayList<Integer> generateRandom(int n)
    {
        ArrayList<Integer> v = new ArrayList<Integer>(n);
        ArrayList<Integer> ans = new ArrayList<Integer>(n);

        // Fill the vector with the values 1, 2, 3, ..., n
        for (int i = 0; i < n; i++)
            v.add(i + 1);

        // While vector has elements get a random number from the vector and print it
        while (v.size() > 0) {
            ans.add(getNum(v)-1);
        }

        return ans;
    }

    private static ArrayList<Integer> createChromosome(int length,int range) {
        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        ArrayList<Integer> part = new ArrayList<Integer>();
        Random rand = new Random();
        int c = 0;
        int remainder = length%range;
        int division = (length-remainder)/range;

        for(int i = 0;i<division;i++) {
            part = generateRandom(range);
            for(int j=0;j<part.size();j++) {
                chromosome.add(part.get(j));
                c = c+1;
            }
        }

        int temp;

        for(int i=c;i<length;i++) {
            temp = rand.nextInt(range);
            chromosome.add(temp);
        }
        return chromosome;
    }

    private static double calculateFitness(ArrayList<Integer> chromosome,int vmCount,List<Cloudlet> cloudletList ,double filesize,int pe,int mips,int bw) {

        double fitness=0;
        double[] vm  = new double[vmCount];
        int vmId;
        double max;
        int item=0;


        for(int i=0;i<chromosome.size();i++) {
            vmId = chromosome.get(i);
            fitness = (cloudletList.get(i).getLength()/1000.00)+(300.00/125000000);
            fitness = (double) Math.round(fitness * 100) / 100;
            vm[vmId] = vm[vmId] + fitness;
            vm[vmId] = (double) Math.round( vm[vmId] * 100) / 100;
        }

        // Getting the maximum fitness value (VM) from a chromosome
        max = 0;
        for (int i = 0; i < vmCount; i++) {
            if (vm[i] > max) {
                max = vm[i];
            }
        }

        // Displaying the chromosome and it's respective VM specific fitness values
        for(int i=0;i<vmCount;i++) {
            System.out.print(vm[i]+" ");
            if(i==vmCount-1) {
                System.out.print("--->");
            }
        }
        for(int i=0;i<chromosome.size();i++) {
            System.out.print(chromosome.get(i)+" ");
            if(i==chromosome.size()-1) {
                System.out.println("");
            }
        }
        

        item++;
        System.out.print("");
        return max;
    }

    private static ArrayList<Integer>SinglePointCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo,int vmcount) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int size = chromosomeOne.size();

        for(int i=0;i<size;i++) {
            if(i<vmcount) {
                chromosome.add(chromosomeOne.get(i));
            }
            else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }

    private static ArrayList<Integer>uniformCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int size = chromosomeOne.size();
        for(int i=0;i<size;i++) {
            if(i%2==0) {
                chromosome.add(chromosomeOne.get(i));
            }
            else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }


    private static ArrayList<Integer>RandomCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int size = chromosomeOne.size();
        Random rand = new Random();
        for(int i=0;i<size;i++) {
            if(rand.nextInt(1)==1) {
                chromosome.add(chromosomeOne.get(i));
            }
            else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }

    private double getCompletionTime(Cloudlet cloudlet, Vm vm){
        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());

        double completionTime = execTime + waitingTime;

        return completionTime;
    }

    private double getExecTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }


}
