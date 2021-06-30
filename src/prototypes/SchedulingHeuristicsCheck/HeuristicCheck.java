package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

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

import java.util.*;
import java.util.stream.Collectors;

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
public class HeuristicCheck {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 10 ;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 3000;

    private CloudSim simulation;
    //private DatacenterBroker broker0;
    CheckBroker brokercheck;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new HeuristicCheck();
    }

    private HeuristicCheck() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();

        datacenter0 = createDatacenter();

        vmList = createVms();
        cloudletList = createCloudlets();

        brokercheck = new CheckBroker(simulation);

        //brokercheck.FirstComeFirstServeScheduleCheck(cloudletList,vmList);
        //brokercheck.RandomScheduleCheck(cloudletList,vmList);
        brokercheck.SJFScheduleCheck(cloudletList,vmList);

        //brokercheck.LCFPScheduleCheck(cloudletList,vmList);
        //brokercheck.SCFPScheduleCheck(cloudletList,vmList);

        //brokercheck.METScheduleCheck(cloudletList,vmList);
        //brokercheck.MCTScheduleCheck(cloudletList,vmList);

        //brokercheck.MinMinScheduleCheck(cloudletList,vmList);
        //brokercheck.MaxMinScheduleCheck(cloudletList,vmList);
        //brokercheck.SufferageScheduleCheck(cloudletList,vmList);

        /* Not sure Scheduling heuristics */

        //brokercheck.OLBCheck(cloudletList,vmList);
        //brokercheck.GeneralizedPriorityCheck(cloudletList,vmList);
        //brokercheck.PrioritySchedulingCheck(cloudletList, vmList);



        simulation.start();

        final List<Cloudlet> finishedCloudlets = brokercheck.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();



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
        int customLength = 0;
        for (int i = 0; i < CLOUDLETS; i++) {
            //Random random = new Random();
            //int randomLength = random.nextInt(500);
            customLength += 2;
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + customLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    /*
    public void selectSchedulingHeuristics(int heuristicIndex, HeuristicBroker broker0){

        switch(heuristicIndex){
            case 0:
                System.out.println("0: Performing First Come First Serve Scheduling Policy");
                broker0.performFirstComeFirstServeScheduling(vmList);
                break;
            case 1:
                System.out.println("1: Performing Random Scheduling");
                broker0.performRandomScheduling(vmList);
                break;
            case 2:
                System.out.println("2: Performing Longest Cloudlet Fastest Processing Scheduling");
                broker0.performLongestCloudletFastestProcessingScheduling(vmList);
                break;
            case 3:
                System.out.println("4: Performing Shortest Cloudlet Fastest Processing Scheduling");
                broker0.performShortestCloudletFastestProcessingScheduling(vmList);
                break;
            case 4:
                System.out.println("5: Performing Minimum Completion Time Scheduling");
                broker0.performMinimumCompletionTimeScheduling(vmList);
                break;
            case 5:
                System.out.println("6: Performing Minimum Execution Time Scheduling");
                broker0.performMinimumExecutionTimeScheduling(vmList);
                break;
            case 6:
                System.out.println("7: Performing Min Min Scheduling");
                broker0.performMinMinScheduling(vmList);
                break;
            case 7:
                System.out.println("8: Performing Max Min Scheduling");
                broker0.performMaxMinScheduling(vmList);
                break;
            case 8:
                System.out.println("9: Performing Sufferage Scheduling");
                broker0.performSufferageScheduling(vmList);
                break;
            case 9:
                System.out.println("10: Performing Shortest Job First Scheduling");
                broker0.performShortestJobFirstScheduling(cloudletList,vmList);
                break;
            case 10:
                System.out.println("11: Performing Priority Based Scheduling");
                broker0.performPriorityBasedScheduling(cloudletList,vmList);
                break;
            case 11:
                System.out.println("12: Performing Opportunistic Load Balance Scheduling");
                broker0.performOpportunisticLoadBalancingHeuristic(cloudletList,vmList);;
                break;
            case 12:
                System.out.println("2: Performing Generalized Priority Scheduling");
                broker0.performGeneralizedPriority(cloudletList, vmList);
                break;


        }

    }

     */

}
