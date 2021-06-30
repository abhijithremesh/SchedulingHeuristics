package org.cloudsimplus.examples.GeneticAlgorithm8;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Simulate {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 85 ;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private CloudSim simulation;

    private DatacenterBroker broker;

    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private Datacenter datacenter;

    public static void main(String[] args) {
        new Simulate();
    }

    private Simulate() {

        Log.setLevel(Level.OFF);

        GA ga = new GA();

        ArrayList<ArrayList> chromosomeList = ga.createInitialPopulation(CLOUDLETS, VMS);

        ArrayList<Double> generationFitness = new ArrayList<Double>();

        System.out.println(chromosomeList);

        for (int generations = 0; generations < 25; generations++) {

            System.out.println("Generation: "+generations);

            ArrayList<Double> fitnessList = new ArrayList<Double>();

            for (int i = 0; i < chromosomeList.size(); i++) {

                ArrayList<Integer> chromosome = chromosomeList.get(i);

                simulation = new CloudSim();
                datacenter = createDatacenter();

                broker = new DatacenterBrokerSimple(simulation);

                vmList = createVms();
                cloudletList = createCloudlets();

                broker.submitVmList(vmList);
                broker.submitCloudletList(cloudletList);

                //vmList.forEach(v -> System.out.println(v.getMips()+" "));
                //cloudletList.forEach(c -> System.out.println(c.getLength()+" "));


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
            System.out.println("chromosomeList size: "+chromosomeList.size());
            System.out.println("fitnessList size: "+fitnessList.size());


            double bestFitness = ga.generationFitness(fitnessList,"min");
            generationFitness.add(bestFitness);

            System.out.println("Generation Fitness: "+generationFitness);

            // If using Elite method for passing elite individuals to the next generation.
            ArrayList<ArrayList> eliteChromosomes = ga.fittestEliteChromosome(fitnessList, chromosomeList,3,"min");

            ga.removeWeakChromosomes(fitnessList,chromosomeList,"max",1);

            ArrayList<ArrayList> offspringsList = new ArrayList<ArrayList>();

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
            customLength += 500;
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + customLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }
        return list;
    }


}
