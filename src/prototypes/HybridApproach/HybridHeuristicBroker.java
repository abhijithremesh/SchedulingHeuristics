package org.cloudsimplus.examples.HybridApproach;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HybridHeuristicBroker extends DatacenterBrokerSimple {

    public HybridHeuristicBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void selectSchedulingHeuristics(int schedulingHeuristic, List<Vm> vmList, List<Cloudlet> cloudletList){

        //int heuristic = candidate.get(heuristicIndex);

        switch(schedulingHeuristic){
            case 0:
                System.out.println("0: Performing First Come First Serve Scheduling Policy");
                this.performFirstComeFirstServeScheduling(vmList,cloudletList);
                break;
            case 1:
                System.out.println("1: Performing Random Scheduling");
                this.performRandomScheduling(vmList,cloudletList);
                break;
            /*
            case 2:
                System.out.println("2: Performing Longest Cloudlet Fastest Processing Scheduling");
                this.performLongestCloudletFastestProcessingScheduling(vmList,cloudletList);
                break;
            case 3:
                System.out.println("3: Performing Shortest Cloudlet Fastest Processing Scheduling");
                this.performShortestCloudletFastestProcessingScheduling(vmList,cloudletList);
                break;
            case 4:
                System.out.println("4: Performing Min Min Scheduling");
                this.performMinMinScheduling(vmList,cloudletList);
                break;
            case 5:
                System.out.println("5: Performing Max Min Scheduling");
                this.performMaxMinScheduling(vmList,cloudletList);
                break;
            case 6:
                System.out.println("6: Performing Sufferage Scheduling");
                this.performSufferageScheduling(vmList, cloudletList);
                break;
            case 7:
                System.out.println("7: Performing Minimum Completion Time Scheduling");
                this.performMinimumCompletionTimeScheduling(vmList, cloudletList);
                break;
            case 8:
                System.out.println("8: Performing Minimum Execution Time Scheduling");
                this.performMinimumExecutionTimeScheduling(vmList,cloudletList);
                break;

             */
            //case 9:
            //    System.out.println("9: Performing Shortest Job First Scheduling");
            //    this.performShortestJobFirstScheduling(cloudletList,vmList);
            //    break;
            //case 10:
            //    System.out.println("10: Performing Priority Based Scheduling");
            //    this.performPriorityBasedScheduling(cloudletList,vmList);
            //    break;
            //case 11:
            //    System.out.println("11: Performing Opportunistic Load Balance Scheduling");
            //    this.performOpportunisticLoadBalancingHeuristic(cloudletList,vmList);;
            //    break;
            //case 12:
            //    System.out.println("12: Performing Generalized Priority Scheduling");
            //    this.performGeneralizedPriority(cloudletList, vmList);
            //    break;


        }

    }

    public void performFirstComeFirstServeScheduling(List<Vm> vmList,List<Cloudlet> cloudletList){

        FirstComeFirstServeHeuristic fcfs = new FirstComeFirstServeHeuristic(this, vmList, cloudletList);
        fcfs.firstComeFirstServeScheduling();

    }

    public void performRandomScheduling(List<Vm> vmList,List<Cloudlet> cloudletList){

        RandomHeuristic rand = new RandomHeuristic(this, vmList, cloudletList);
        rand.randomScheduling();

    }

    /*
    public void performLongestCloudletFastestProcessingScheduling(List<Vm> vmList, List<Cloudlet> cloudletList){

        LongestCloudletFastestProcessingHeuristic lcfp = new LongestCloudletFastestProcessingHeuristic(this, vmList, cloudletList);
        lcfp.longestCloudletFastestProcessingScheduling1();

    }

    public void performShortestCloudletFastestProcessingScheduling(List<Vm> vmList, List<Cloudlet> cloudletList){

        ShortestCloudletFastestProcessingHeuristic scfp = new ShortestCloudletFastestProcessingHeuristic(this, vmList, cloudletList);
        scfp.shortestCloudletFastestProcessingScheduling();

    }

    public void performMinimumCompletionTimeScheduling(List<Vm>vmlist, List<Cloudlet> cloudletList){

        MinimumCompletionTimeHeuristic mct = new MinimumCompletionTimeHeuristic(this,vmlist);
        mct.minimumCompletionTimeScheduling();

    }

    public void performMinimumExecutionTimeScheduling(List<Vm>vmlist, List<Cloudlet> cloudletList){

        MinimumExecutionTimeHeuristic met = new MinimumExecutionTimeHeuristic(this,vmlist,cloudletList);
        met.minimumExecutionTimeScheduling();

    }



    public void performMinMinScheduling(List<Vm>vmlist, List<Cloudlet> cloudletList){

        MinMinHeuristic minmin  = new MinMinHeuristic(this,vmlist,cloudletList);
        minmin.minMinScheduling();

    }

    public void performMaxMinScheduling(List<Vm>vmlist, List<Cloudlet> cloudletList){

        MaxMinHeuristic maxmin  = new MaxMinHeuristic(this,vmlist, cloudletList);
        maxmin.maxMinScheduling();

    }

    public void performSufferageScheduling(List<Vm> vmlist, List<Cloudlet> cloudletList){

        SufferageHeuristic sufferage = new SufferageHeuristic(this, vmlist, cloudletList);
        sufferage.sufferageScheduling();

    }

    public void performShortestJobFirstScheduling(List<Cloudlet> cloudletList, List<Vm> vmList){

        ShortestJobFirstSchedulingHeuristic sjf = new ShortestJobFirstSchedulingHeuristic(this,cloudletList,vmList);
        sjf.shortestJobFirstScheduling1();

    }



     */









}
