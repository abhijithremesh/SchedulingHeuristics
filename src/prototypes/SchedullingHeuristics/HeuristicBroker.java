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
        lcfp.longestCloudletFastestProcessingScheduling();

    }

    public void performShortestCloudletFastestProcessingScheduling(List<Cloudlet> cloudletList,List<Vm> vmList){

        ShortestCloudletFastestProcessingHeuristic scfp = new ShortestCloudletFastestProcessingHeuristic(this, cloudletList, vmList);
        scfp.shortestCloudletFastestProcessingScheduling();

    }

    public void performMinimumCompletionTimeScheduling(List<Cloudlet>cloudletList, List<Vm>vmlist){

        MinimumCompletionTimeHeuristic mct = new MinimumCompletionTimeHeuristic(this,cloudletList,vmlist);
        mct.minimumCompletionTimeScheduling();

    }

    public void performMinimumExecutionTimeScheduling(List<Cloudlet>cloudletList, List<Vm>vmlist){

        MinimumExecutionTimeHeuristic met = new MinimumExecutionTimeHeuristic(this,cloudletList,vmlist);
        met.minimumExecutionTimeScheduling();

    }

    public void performOpportunisticLoadBalancingHeuristic(List<Cloudlet> cloudletList, List<Vm> vmList){

        OpportunisticLoadBalancingHeuristic olb = new OpportunisticLoadBalancingHeuristic(this,cloudletList,vmList);
        olb.opportunisticLoadBalancingScheduling();

    }

    public void performMinMinScheduling(List<Cloudlet>cloudletList, List<Vm>vmlist){

        MinMinHeuristic minmin  = new MinMinHeuristic(this,cloudletList,vmlist);
        minmin.minMinScheduling();

    }

    public void performMaxMinScheduling(List<Cloudlet>cloudletList, List<Vm>vmlist){

        MaxMinHeuristic maxmin  = new MaxMinHeuristic(this,cloudletList,vmlist);
        maxmin.maxMinScheduling();

    }

    public void performSufferageScheduling(List<Cloudlet> cloudletList, List<Vm> vmlist){

        SufferageHeuristic sufferage = new SufferageHeuristic(this,cloudletList, vmlist);
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
