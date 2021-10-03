package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerFirstFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SchedulingHeuristicsCheck.FirstComeFirstServeCheck;

import java.util.List;

public class MyBroker extends DatacenterBrokerSimple {

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
            case 3:
                System.out.println("Random");
                this.Random(vmList);
                break;
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
            case 4:
                System.out.println("LCFP");
                this.LongestCloudletFastestPE(vmList);
                break;
            case 5:
                System.out.println("SCFP");
                this.ShortestCloudletFastestPE(vmList);
                break;
            case 6:
                System.out.println("MAX-MIN");
                this.MaxMin(vmList);
                break;
            case 7:
                System.out.println("MIN-MIN");
                this.MinMin(vmList);
                break;
            case 8:
                System.out.println("Sufferage");
                this.Sufferage(vmList);
                break;
            case 9:
                System.out.println("MET");
                this.MinimumExecutionTime(vmList);
                break;
            case 10:
                System.out.println("MCT");
                this.MinimumCompletionTime(vmList);
                break;
            case 11:
                System.out.println("FCFS-FirstFit");
                this.FirstComeFirstServeFirstFit(vmList);
                break;
            case 12:
                System.out.println("SJF-FirstFit");
                this.ShortestJobFirstFirstFit(vmList);
                break;
            case 13:
                System.out.println("LJF-FirstFit");
                this.LongestJobFirstFirstFit(vmList);
                break;

        }
    }






}
