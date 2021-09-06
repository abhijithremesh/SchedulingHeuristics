package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicBroker extends DatacenterBrokerSimple {

    public HeuristicBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void selectSchedulingHeuristics(int schedulingHeuristic, List<Vm> vmList){

        //int heuristic = candidate.get(heuristicIndex);

        switch(schedulingHeuristic){
            case 0:
                System.out.println("0: Performing First Come First Serve Scheduling Policy");
                this.performFirstComeFirstServeScheduling(vmList);
                break;
            case 1:
                System.out.println("1: Performing Random Scheduling");
                this.performRandomScheduling(vmList);
                break;
            case 2:
                System.out.println("2: Performing Longest Cloudlet Fastest Processing Scheduling");
                this.performLongestCloudletFastestProcessingScheduling(vmList);
                break;
            case 3:
                System.out.println("3: Performing Shortest Cloudlet Fastest Processing Scheduling");
                this.performShortestCloudletFastestProcessingScheduling(vmList);
                break;
            case 4:
                System.out.println("4: Performing Min Min Scheduling");
                this.performMinMinScheduling(vmList);
                break;
            case 5:
                System.out.println("5: Performing Max Min Scheduling");
                this.performMaxMinScheduling(vmList);
                break;
            case 6:
                System.out.println("6: Performing Sufferage Scheduling");
                this.performSufferageScheduling(vmList);
                break;
            case 7:
                System.out.println("7: Performing Minimum Completion Time Scheduling");
                this.performMinimumCompletionTimeScheduling(vmList);
                break;
            case 8:
                System.out.println("8: Performing Minimum Execution Time Scheduling");
                this.performMinimumExecutionTimeScheduling(vmList);
                break;
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

    public void performFirstComeFirstServeScheduling(List<Vm> vmList){

        FirstComeFirstServeHeuristic fcfs = new FirstComeFirstServeHeuristic(this, vmList);
        fcfs.firstComeFirstServeScheduling();

    }

    public void performRandomScheduling(List<Vm> vmList){

        RandomHeuristic rand = new RandomHeuristic(this, vmList);
        rand.randomScheduling();

    }

    public void performLongestCloudletFastestProcessingScheduling(List<Vm> vmList){

        LongestCloudletFastestProcessingHeuristic lcfp = new LongestCloudletFastestProcessingHeuristic(this, vmList);
        lcfp.longestCloudletFastestProcessingScheduling1();

    }

    public void performShortestCloudletFastestProcessingScheduling(List<Vm> vmList){

        ShortestCloudletFastestProcessingHeuristic scfp = new ShortestCloudletFastestProcessingHeuristic(this, vmList);
        scfp.shortestCloudletFastestProcessingScheduling();

    }

    public void performMinimumCompletionTimeScheduling(List<Vm>vmlist){

        MinimumCompletionTimeHeuristic mct = new MinimumCompletionTimeHeuristic(this,vmlist);
        mct.minimumCompletionTimeScheduling();

    }

    public void performMinimumExecutionTimeScheduling(List<Vm>vmlist){

        MinimumExecutionTimeHeuristic met = new MinimumExecutionTimeHeuristic(this,vmlist);
        met.minimumExecutionTimeScheduling();

    }

    public void performOpportunisticLoadBalancingHeuristic(List<Cloudlet> cloudletList, List<Vm> vmList){

        OpportunisticLoadBalancingHeuristic olb = new OpportunisticLoadBalancingHeuristic(this,cloudletList,vmList);
        olb.opportunisticLoadBalancingScheduling();

    }

    public void performMinMinScheduling(List<Vm>vmlist){

        MinMinHeuristic minmin  = new MinMinHeuristic(this,vmlist);
        minmin.minMinScheduling();

    }

    public void performMaxMinScheduling(List<Vm>vmlist){

        MaxMinHeuristic maxmin  = new MaxMinHeuristic(this,vmlist);
        maxmin.maxMinScheduling();

    }

    public void performSufferageScheduling(List<Vm> vmlist){

        SufferageHeuristic sufferage = new SufferageHeuristic(this, vmlist);
        sufferage.sufferageScheduling();

    }

    public void performShortestJobFirstScheduling(List<Cloudlet> cloudletList, List<Vm> vmList){

        ShortestJobFirstSchedulingHeuristic sjf = new ShortestJobFirstSchedulingHeuristic(this,cloudletList,vmList);
        sjf.shortestJobFirstScheduling1();

    }

    public void performPriorityBasedScheduling(List<Cloudlet> cloudletList, List<Vm> vmList){

        PriorityBasedSchedulingHeuristic pbs = new PriorityBasedSchedulingHeuristic(this,cloudletList,vmList);
        pbs.PriorityBasedScheduling();

    }

    public void performGeneralizedPriority(List<Cloudlet> cloudletList,List<Vm> vmList){

        GeneralizedPriorityHeuristic genprior = new GeneralizedPriorityHeuristic(this, cloudletList, vmList);
        genprior.generalizedPriorityScheduling();

    }









}
