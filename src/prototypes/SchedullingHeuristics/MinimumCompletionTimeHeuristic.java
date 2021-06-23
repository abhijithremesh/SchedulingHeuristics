package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinimumCompletionTimeHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker broker0;

    MinimumCompletionTimeHeuristic(HeuristicBroker broker0, List<Cloudlet> cloudletList, List<Vm> vmList) {
        this.cloudletList = cloudletList;
        this.vmList = vmList;
        this.broker0 = broker0;

    }

    public void minimumCompletionTimeScheduling() {

        //broker0.submitCloudletList(cloudletList);
        //broker0.submitVmList(vmList);

        System.out.println(cloudletList.get(0).getId());

        double completionTime[][] = new double[cloudletList.size()][vmList.size()];

        double time =0.0;

        // Generating Task-VM Completion time matrix
        for(int i=0;i<cloudletList.size();i++){
            for(int j=0;j<vmList.size();j++){
                time=getCompletionTime(cloudletList.get(i),vmList.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        int vm =0;

        // Assigning each cloudlet to that VM which gives the minimum completion time
        for (int i = 0; i <cloudletList.size();i++){
            int cl = i;
            double minCompTime=Integer.MAX_VALUE;
            for (int j = 0; j < vmList.size(); j++) {
                if (completionTime[i][j] < minCompTime) {
                    minCompTime = completionTime[i][j];
                    vm = j;
                }
            }
            broker0.bindCloudletToVm(cloudletList.get(cl), vmList.get(vm));
            //System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm)+" at MET: "+minCompTime);
        }


    }

    private double getCompletionTime(Cloudlet cloudlet, Vm vm){

        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
        double completionTime = execTime + waitingTime;
        return completionTime;

    }
}
