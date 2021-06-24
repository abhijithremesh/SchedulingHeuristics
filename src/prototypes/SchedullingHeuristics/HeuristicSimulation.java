package org.cloudsimplus.examples.SchedullingHeuristics;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
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
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
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
public class HeuristicSimulation {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 200 ;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private CloudSim simulation;
    private DatacenterBroker broker0;
    HeuristicBroker brokerh;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int heuristicIndex;
    int heuristicSwitch;

    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();
    ArrayList<Integer> candidate = new ArrayList<>();

    public static void main(String[] args)  {
        new HeuristicSimulation();
    }

    private HeuristicSimulation() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        Log.setLevel(Level.OFF);

        GAMetaHeuristic mh_ga = new GAMetaHeuristic();

        ArrayList<Double> generationFitness = new ArrayList<Double>();

        ArrayList<ArrayList> candidateList = mh_ga.createInitialPopulation(10, 8);

        System.out.println("initialPopulation: " + candidateList);

        for (int generations = 0; generations < 1; generations++) {

            ArrayList<Double> fitnessList = new ArrayList<Double>();

            //simulation.addOnClockTickListener(this::pauseSimulation);
            //simulation.addOnEventProcessingListener(this::pauseSimulationAtSpecificTime);
            //simulation.addOnSimulationStartListener(this::startSchedulingHeuristics);
            //simulation.addOnSimulationPauseListener(this::changeSchedulingHeuristics);

            System.out.println("Generation: "+generations);


            for (int i = 0; i < candidateList.size(); i++) {

                simulation = new CloudSim();

                datacenter0 = createDatacenter();

                vmList = createVms();
                cloudletList = createCloudlets();

                brokerh = new HeuristicBroker(simulation);

                brokerh.submitCloudletList(cloudletList);
                brokerh.submitVmList(vmList);

                simulation.addOnClockTickListener(this::pauseSimulation);

                simulation.addOnSimulationPauseListener(this::changeSchedulingHeuristics);

                heuristicIndex = 0;
                heuristicSwitch = 0;

                candidate = candidateList.get(i);

                System.out.println("Candidate: " + candidate);

                System.out.println("****************************************************************************************************************************************");

                selectSchedulingHeuristics(heuristicIndex, brokerh, candidate);

                simulation.start();

                postSimulationHeuristicSpecificFinishedloudlets(brokerh);

                double makespan = mh_ga.calculateFitness(brokerh);

                fitnessList.add(makespan);

                System.out.println("Candidate Fitness: " + fitnessList);

                System.out.println("################################################# SIMULATION END ##############################################################################");

            }

            System.out.println("Candidate List: " + candidateList);
            System.out.println("Fitness List: " + fitnessList);
            System.out.println("Candidate List Size: " + candidateList.size());
            System.out.println("Fitness List Size: " + fitnessList.size());

            double bestFitness = mh_ga.generationFitness(fitnessList, "min");
            generationFitness.add(bestFitness);

            System.out.println("Generation Fitness: " + generationFitness);

            // If using Elite method for passing elite individuals to the next generation.
            ArrayList<ArrayList> eliteChromosomes = mh_ga.fittestEliteChromosome(fitnessList, candidateList,3,"min");

            mh_ga.removeWeakChromosomes(fitnessList, candidateList, "max", 3);

            ArrayList<ArrayList> offspringsList = new ArrayList<ArrayList>();

            mh_ga.generationEvolve(3, "min", candidateList, fitnessList, offspringsList);

            offspringsList.addAll(eliteChromosomes);

            candidateList.addAll(offspringsList);

            System.out.println("Candidate List after Evolution: "+candidateList.size());

            System.out.println("################################################# GENERATION END ##############################################################################");

        }

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

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        int customMIPS = 0;
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            //Random random = new Random();
            //int randomMips = random.nextInt(500);
            customMIPS = customMIPS + 100;
            final Vm vm = new VmSimple(1000 + customMIPS, VM_PES);
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
            Random random = new Random();
            int randomLength = random.nextInt(500);
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + randomLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    public void selectSchedulingHeuristics(int heuristicIndex, HeuristicBroker brokerh, ArrayList<Integer> candidate){

        int heuristic = candidate.get(heuristicIndex);

        switch(heuristic){
            case 0:
                System.out.println("0: Performing First Come First Serve Scheduling Policy");
                brokerh.performFirstComeFirstServeScheduling(vmList);
                break;
            case 1:
                System.out.println("1: Performing Random Scheduling");
                brokerh.performRandomScheduling(vmList);
                break;
            case 2:
                System.out.println("2: Performing Longest Cloudlet Fastest Processing Scheduling");
                brokerh.performLongestCloudletFastestProcessingScheduling(vmList);
                break;
            case 3:
                System.out.println("3: Performing Shortest Cloudlet Fastest Processing Scheduling");
                brokerh.performShortestCloudletFastestProcessingScheduling(vmList);
                break;
            case 4:
                System.out.println("4: Performing Min Min Scheduling");
                brokerh.performMinMinScheduling(vmList);
                break;
            case 5:
                System.out.println("5: Performing Max Min Scheduling");
                brokerh.performMaxMinScheduling(vmList);
                break;
            case 6:
                System.out.println("6: Performing Sufferage Scheduling");
                brokerh.performSufferageScheduling(vmList);
                break;
            case 7:
                System.out.println("7: Performing Minimum Completion Time Scheduling");
                brokerh.performMinimumCompletionTimeScheduling(vmList);
                break;
            case 8:
                System.out.println("8: Performing Minimum Execution Time Scheduling");
                brokerh.performMinimumExecutionTimeScheduling(vmList);
                break;
            case 9:
                System.out.println("9: Performing Shortest Job First Scheduling");
                brokerh.performShortestJobFirstScheduling(cloudletList,vmList);
                break;
            case 10:
                System.out.println("10: Performing Priority Based Scheduling");
                brokerh.performPriorityBasedScheduling(cloudletList,vmList);
                break;
            case 11:
                System.out.println("11: Performing Opportunistic Load Balance Scheduling");
                brokerh.performOpportunisticLoadBalancingHeuristic(cloudletList,vmList);;
                break;
            case 12:
                System.out.println("12: Performing Generalized Priority Scheduling");
                brokerh.performGeneralizedPriority(cloudletList, vmList);
                break;


        }

    }


    // pauses simulation every 30 seconds...
    public void pauseSimulation(EventInfo eInfo) {
        if (Math.floor(simulation.clock()) == 20 * (heuristicSwitch + 1)) {
            simulation.pause();
            //System.out.println("# Simulation paused at %.2f second%n"+eInfo.getTime());
        }
    }

    public void pauseSimulationAtSpecificTime(SimEvent simEvent) {
        if(Math.floor(simEvent.getTime()) == 3000){
            simulation.pause();
            System.out.println("passed "+simEvent.getTime());
        }

    }

    public void startSchedulingHeuristics(EventInfo startInfo) {
        System.out.printf("%n# Simulation started at %.2f second%n", startInfo.getTime());
        //selectSchedulingHeuristics(heuristicIndex,brokerh);
        //heuristicIndex++;
        simulation.pause();
    }

    public  void changeSchedulingHeuristics(EventInfo pauseInfo) {
        System.out.println("# Pausing Simulation at "+ pauseInfo.getTime()+" seconds");
        postSimulationAllFinishedloudlets(brokerh);
        postSimulationHeuristicSpecificFinishedloudlets(brokerh);
        System.out.println("************************************************************************************************************************");
        if (heuristicIndex < candidate.size()-1){
            heuristicIndex ++;
            heuristicSwitch ++;
            selectSchedulingHeuristics(heuristicIndex,brokerh, candidate);
        }
        else if (heuristicIndex >= candidate.size()-1){
            heuristicIndex = 0;
            heuristicSwitch ++;
            selectSchedulingHeuristics(heuristicIndex,brokerh, candidate);
        }
        System.out.println("Resuming Simulation....");
        simulation.resume();
    }


    public void postSimulationAllFinishedloudlets(HeuristicBroker brokerh){

        List<Cloudlet> allFinishedCloudlets = new ArrayList<Cloudlet>();
        allFinishedCloudlets = brokerh.getCloudletFinishedList();
        System.out.println("Total No. of Cloudlets processed: "+allFinishedCloudlets.size());
        System.out.println("Total Cloudlets processed: "+allFinishedCloudlets);
        //new CloudletsTableBuilder(allFinishedCloudlets).build();

    }

    public void postSimulationHeuristicSpecificFinishedloudlets(HeuristicBroker brokerh){

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
        System.out.println("No. of Cloudlets Heuristic processed: "+heuristicSpecificFinishedCloudlets.size());
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }


}

