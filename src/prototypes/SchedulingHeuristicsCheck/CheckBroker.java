package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

import org.apache.commons.math3.analysis.function.Max;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.MaxMin;
import org.cloudsimplus.examples.SchedulingHeuristicsCheck.FirstComeFirstServeCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckBroker extends DatacenterBrokerSimple {

    public CheckBroker(final CloudSim simulation) {
        super(simulation);
    }


    public void FirstComeFirstServeScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        FirstComeFirstServeCheck fcfs = new FirstComeFirstServeCheck(cloudletList,vmList,this);
        fcfs.FCFSChecking();

    }

    public void LCFPScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        LongestCloudletFastestProcessingElementCheck lcfp = new LongestCloudletFastestProcessingElementCheck(cloudletList,vmList,this);
        lcfp.LCFPChecking2();

    }

    public void SCFPScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        ShortestCloudletFastestProcessingElementCheck scfp = new ShortestCloudletFastestProcessingElementCheck(cloudletList,vmList,this);
        scfp.SCFPChecking1();

    }

    public void METScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        MinimumExecutionTimeCheck met = new MinimumExecutionTimeCheck(cloudletList,vmList,this);
        met.METChecking();

    }

    public void MCTScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        MinimumCompletionTimeCheck mct = new MinimumCompletionTimeCheck(cloudletList,vmList,this);
        mct.MCTChecking();

    }

    public void RandomScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        RandomCheck r = new RandomCheck(cloudletList,vmList,this);
        r.RandomChecking();

    }

    public void MaxMinScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        MaxMinCheck maxmin = new MaxMinCheck(cloudletList,vmList,this);
        maxmin.MaxMinChecking();

    }

    public void MinMinScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        MinMinCheck maxmin = new MinMinCheck(cloudletList,vmList,this);
        maxmin.MinMinChecking();

    }

    public void SufferageScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        SufferageCheck suff = new SufferageCheck(cloudletList,vmList,this);
        suff.SufferageChecking();

    }

    public void SJFScheduleCheck(List<Cloudlet> cloudletList, List<Vm> vmList){

        ShortestJobFirstCheck sjf = new ShortestJobFirstCheck(cloudletList,vmList, this);
        sjf.SJFChecking4();

    }










}
