package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerFirstFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SchedulingHeuristicsCheck.FirstComeFirstServeCheck;

import java.util.List;

public class MyBroker extends DatacenterBrokerFirstFit {

    public MyBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void RoundRobin(List<Vm> vmList){

        RoundRobinPolicy rr = new RoundRobinPolicy(this, vmList);
        rr.schedule();

    }

    public void FirstComeFirstServe(List<Vm> vmList){

        FirstComeFirstServePolicy fcfs = new FirstComeFirstServePolicy(this, vmList);
        fcfs.schedule();

    }

    public void LongestJobFirst(List<Vm> vmList){

        LongestJobFirstPolicy ljf = new LongestJobFirstPolicy(this, vmList);
        ljf.schedule();

    }

    public void ShortestJobFirst(List<Vm> vmList){

        ShortestJobFirstPolicy sjf = new ShortestJobFirstPolicy(this, vmList);
        sjf.schedule();

    }

    public void FirstComeFirstServeFirstFit(List<Vm> vmList){

        FirstComeFirstServeFirstFitPolicy ff = new FirstComeFirstServeFirstFitPolicy(this, vmList);
        ff.schedule();

    }

    public void BestFit(List<Vm> vmList){

        BestFitPolicy bf = new BestFitPolicy(this, vmList);
        bf.schedule();

    }

    public void ShortestJobFirstFirstFit(List<Vm> vmList){

        ShortestJobFirstFirstFitPolicy sjfff = new ShortestJobFirstFirstFitPolicy(this, vmList);
        sjfff.schedule();

    }

    public void LongestJobFirstFirstFit(List<Vm> vmList){

        LongestJobFirstFirstFitPolicy ljfff = new LongestJobFirstFirstFitPolicy(this, vmList);
        ljfff.schedule();

    }

    public void Random(List<Vm> vmList){

        RandomPolicy r = new RandomPolicy(this, vmList);
        r.schedule();

    }

    public void ShortestCloudletFastestPE(List<Vm> vmList){

        ShortestCloudletFastestPEPolicy scfp = new ShortestCloudletFastestPEPolicy(this, vmList);
        scfp.schedule();

    }

    public void LongestCloudletFastestPE(List<Vm> vmList){

        LongestCloudletFastestPEPolicy lcfp = new LongestCloudletFastestPEPolicy(this, vmList);
        lcfp.schedule();

    }

    public void MinimumExecutionTime(List<Vm> vmList){

        MinimumExecutionTimePolicy met = new MinimumExecutionTimePolicy(this, vmList);
        met.schedule();

    }

    public void MinimumCompletionTime(List<Vm> vmList){

        MinimumCompletionTimePolicy mct = new MinimumCompletionTimePolicy(this, vmList);
        mct.schedule();

    }

    public void MinMin(List<Vm> vmList){

        MinMinPolicy min = new MinMinPolicy(this, vmList);
        min.schedule();

    }

    public void MaxMin(List<Vm> vmList){

        MaxMinPolicy max = new MaxMinPolicy(this, vmList);
        max.schedule();

    }

    public void Sufferage(List<Vm> vmList){

        SufferagePolicy s = new SufferagePolicy(this, vmList);
        s.schedule();

    }

    public void selectSchedulingPolicy(int schedulingHeuristic, List<Vm> vmList){
        switch(schedulingHeuristic){
            case 0:
                this.Random(vmList);
                System.out.println("Random");
                break;
            case 1:
                this.FirstComeFirstServe(vmList);
                System.out.println("FCFS");
                break;
            case 2:
                this.ShortestJobFirst(vmList);
                System.out.println("SJF");
                break;
            case 3:
                this.LongestJobFirst(vmList);
                System.out.println("LJF");
                break;
            case 4:
                this.LongestCloudletFastestPE(vmList);
                System.out.println("LCFP");
                break;
            case 5:
                this.ShortestCloudletFastestPE(vmList);
                System.out.println("SCFP");
                break;
            case 6:
                this.MaxMin(vmList);
                System.out.println("MAX-MIN");
                break;
            case 7:
                this.MinMin(vmList);
                System.out.println("MIN-MIN");
                break;
            case 8:
                this.Sufferage(vmList);
                System.out.println("Sufferage");
                break;
            case 9:
                this.MinimumExecutionTime(vmList);
                System.out.println("MET");
                break;
            case 10:
                this.MinimumCompletionTime(vmList);
                System.out.println("MCT");
                break;
            case 11:
                this.FirstComeFirstServeFirstFit(vmList);
                System.out.println("FCFS-FirstFit");
                break;
            case 12:
                this.ShortestJobFirstFirstFit(vmList);
                System.out.println("SJF-FirstFit");
                break;
            case 13:
                this.LongestJobFirstFirstFit(vmList);
                System.out.println("LJF-FirstFit");
                break;

        }
    }






}
