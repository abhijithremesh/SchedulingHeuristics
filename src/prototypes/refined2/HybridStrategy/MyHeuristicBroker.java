package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerFirstFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;


import java.util.List;

public class MyHeuristicBroker extends DatacenterBrokerSimple {

    public MyHeuristicBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void RoundRobin(List<Vm> vmList){

        RoundRobinHeuristic rr = new RoundRobinHeuristic(this, vmList);
        rr.schedule();

    }

    public void FirstComeFirstServe(List<Vm> vmList){

        FirstComeFirstServeHeuristic fcfs = new FirstComeFirstServeHeuristic(this, vmList);
        fcfs.schedule();

    }

    public void LongestJobFirst(List<Vm> vmList){

        LongestJobFirstHeuristic ljf = new LongestJobFirstHeuristic(this, vmList);
        ljf.schedule();

    }

    public void ShortestJobFirst(List<Vm> vmList){

        ShortestJobFirstHeuristic sjf = new ShortestJobFirstHeuristic(this, vmList);
        sjf.schedule();

    }

    public void FirstComeFirstServeFirstFit(List<Vm> vmList){

        FirstComeFirstServeFirstFitHeuristic ff = new FirstComeFirstServeFirstFitHeuristic(this, vmList);
        ff.schedule();

    }

    public void BestFit(List<Vm> vmList){

        BestFitHeuristic bf = new BestFitHeuristic(this, vmList);
        bf.schedule();

    }

    public void ShortestJobFirstFirstFit(List<Vm> vmList){

        ShortestJobFirstFirstFitHeuristic sjfff = new ShortestJobFirstFirstFitHeuristic(this, vmList);
        sjfff.schedule();

    }

    public void LongestJobFirstFirstFit(List<Vm> vmList){

        LongestJobFirstFirstFitHeuristic ljfff = new LongestJobFirstFirstFitHeuristic(this, vmList);
        ljfff.schedule();

    }

    public void Random(List<Vm> vmList){

        RandomHeuristic  r = new RandomHeuristic(this, vmList);
        r.schedule();

    }

    public void ShortestCloudletFastestPE(List<Vm> vmList){

        ShortestCloudletFastestPEHeuristic scfp = new ShortestCloudletFastestPEHeuristic(this, vmList);
        scfp.schedule();

    }

    public void LongestCloudletFastestPE(List<Vm> vmList){

        LongestCloudletFastestPEHeuristic lcfp = new LongestCloudletFastestPEHeuristic(this, vmList);
        lcfp.schedule();

    }

    public void MinimumExecutionTime(List<Vm> vmList){

        MinimumExecutionTimeHeuristic met = new MinimumExecutionTimeHeuristic(this, vmList);
        met.schedule();

    }

    public void MinimumCompletionTime(List<Vm> vmList){

        MinimumCompletionTimeHeuristic mct = new MinimumCompletionTimeHeuristic(this, vmList);
        mct.schedule();

    }

    public void MinMin(List<Vm> vmList){

        MinMinHeuristic min = new MinMinHeuristic(this, vmList);
        min.schedule();

    }

    public void MaxMin(List<Vm> vmList){

        MaxMinHeuristic max = new MaxMinHeuristic(this, vmList);
        max.schedule();

    }

    public void Sufferage(List<Vm> vmList){

        SufferageHeuristic s = new SufferageHeuristic(this, vmList);
        s.schedule();

    }

    public void selectSchedulingPolicy(int schedulingHeuristic, List<Vm> vmList){
        switch(schedulingHeuristic){
            case 0:
                System.out.println("FCFS");
                this.FirstComeFirstServe(vmList);
                break;
            case 1:
                System.out.println("SJF");
                this.ShortestJobFirst(vmList);
                break;
            case 2:
                System.out.println("LJF");
                this.LongestJobFirst(vmList);
                break;
            case 3:
                System.out.println("LCFP");
                this.LongestCloudletFastestPE(vmList);
                break;
            case 4:
                System.out.println("SCFP");
                this.ShortestCloudletFastestPE(vmList);
                break;
            case 5:
                System.out.println("MAX-MIN");
                this.MaxMin(vmList);
                break;
            case 6:
                System.out.println("MIN-MIN");
                this.MinMin(vmList);
                break;
            case 7:
                System.out.println("Sufferage");
                this.Sufferage(vmList);
                break;
            case 8:
                System.out.println("MET");
                this.MinimumExecutionTime(vmList);
                break;
            case 9:
                System.out.println("MCT");
                this.MinimumCompletionTime(vmList);
                break;
            case 10:
                System.out.println("SJF-FirstFit");
                this.ShortestJobFirstFirstFit(vmList);
                break;
            case 11:
                System.out.println("LJF-FirstFit");
                this.LongestJobFirstFirstFit(vmList);
                break;
            case 12:
                System.out.println("FCFS-FirstFit");
                this.FirstComeFirstServeFirstFit(vmList);
                break;

        }
    }






}
