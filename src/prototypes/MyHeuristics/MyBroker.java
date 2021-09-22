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

    public void RoundRobin(){

        RoundRobinPolicy rr = new RoundRobinPolicy(this);
        rr.schedule();

    }

    public void FirstComeFirstServe(){

        FirstComeFirstServePolicy fcfs = new FirstComeFirstServePolicy(this);
        fcfs.schedule();

    }

    public void LongestJobFirst(){

        LongestJobFirstPolicy ljf = new LongestJobFirstPolicy(this);
        ljf.schedule();

    }

    public void ShortestJobFirst(){

        ShortestJobFirstPolicy sjf = new ShortestJobFirstPolicy(this);
        sjf.schedule();

    }

    public void FirstComeFirstServeFirstFit(){

        FirstComeFirstServeFirstFitPolicy ff = new FirstComeFirstServeFirstFitPolicy(this);
        ff.schedule();

    }

    public void BestFit(){

        BestFitPolicy bf = new BestFitPolicy(this);
        bf.schedule();

    }

    public void ShortestJobFirstFirstFit(){

        ShortestJobFirstFirstFitPolicy sjfff = new ShortestJobFirstFirstFitPolicy(this);
        sjfff.schedule();

    }

    public void LongestJobFirstFirstFit(){

        LongestJobFirstFirstFitPolicy ljfff = new LongestJobFirstFirstFitPolicy(this);
        ljfff.schedule();

    }

    public void Random(){

        RandomPolicy r = new RandomPolicy(this);
        r.schedule();

    }

    public void ShortestCloudletFastestPE(){

        ShortestCloudletFastestPEPolicy scfp = new ShortestCloudletFastestPEPolicy(this);
        scfp.schedule();

    }

    public void LongestCloudletFastestPE(){

        LongestCloudletFastestPEPolicy lcfp = new LongestCloudletFastestPEPolicy(this);
        lcfp.schedule();

    }

    public void MinimumExecutionTime(){

        MinimumExecutionTimePolicy met = new MinimumExecutionTimePolicy(this);
        met.schedule();

    }

    public void MinimumCompletionTime(){

        MinimumCompletionTimePolicy mct = new MinimumCompletionTimePolicy(this);
        mct.schedule();

    }

    public void MinMin(){

        MinMinPolicy min = new MinMinPolicy(this);
        min.schedule();

    }

    public void MaxMin(){

        MaxMinPolicy max = new MaxMinPolicy(this);
        max.schedule();

    }

    public void Sufferage(){

        SufferagePolicy s = new SufferagePolicy(this);
        s.schedule();

    }

    public void selectSchedulingPolicy(int schedulingHeuristic){
        switch(schedulingHeuristic){
            case 0:
                this.Random();
                System.out.println("Random");
                break;
            case 1:
                this.FirstComeFirstServe();
                System.out.println("FCFS");
                break;
            case 2:
                this.FirstComeFirstServeFirstFit();
                System.out.println("FCFS-FirstFit");
                break;
            case 3:
                this.ShortestJobFirst();
                System.out.println("SJF");
                break;
            case 4:
                this.ShortestJobFirstFirstFit();
                System.out.println("SJF-FirstFit");
                break;
            case 5:
                this.LongestJobFirst();
                System.out.println("LJF");
                break;
            case 6:
                this.LongestJobFirstFirstFit();
                System.out.println("LJF-FirstFit");
                break;
            case 7:
                this.LongestCloudletFastestPE();
                System.out.println("LCFP");
                break;
            case 8:
                this.ShortestCloudletFastestPE();
                System.out.println("SCFP");
                break;
            case 9:
                this.MaxMin();
                System.out.println("MAX-MIN");
                break;
            case 10:
                this.MinMin();
                System.out.println("MIN-MIN");
                break;
            case 11:
                this.Sufferage();
                System.out.println("Sufferage");
                break;
            case 12:
                this.MinimumExecutionTime();
                System.out.println("MET");
                break;
            case 13:
                this.MinimumCompletionTime();
                System.out.println("MCT");
                break;
        }
    }






}
