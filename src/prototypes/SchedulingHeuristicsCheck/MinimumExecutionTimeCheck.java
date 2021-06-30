package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SchedullingHeuristics.HeuristicBroker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinimumExecutionTimeCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    CheckBroker brokercheck;

    MinimumExecutionTimeCheck(List<Cloudlet> cloudletList, List<Vm> vmList, CheckBroker brokercheck) {

        this.brokercheck = brokercheck;
        this.cloudletList = cloudletList;
        this.vmList = vmList;

    }

    public void METChecking( ) {

        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        double executionTime[][] = new double[cloudletList.size()][vmList.size()];

        double time =0.0;

        // Generating Execution Time matrix for cloudlet and VM
        for(int i=0; i < cloudletList.size(); i++){
            for(int j=0;j < vmList.size(); j++){
                time=getExecutionTime(cloudletList.get(i),vmList.get(j));
                time = Math.round(time*100.0)/100.0;
                executionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
            }
        }

        int vm = 0;

        // Assigning each cloudlet to that VM which gives the minimum execution time
        for (int i = 0; i < cloudletList.size();i++){
            int cl = i;
            double minExecTime=Integer.MAX_VALUE;
            for (int j = 0; j < vmList.size(); j++) {
                if (executionTime[i][j] < minExecTime) {
                    minExecTime = executionTime[i][j];
                    vm = j;
                }
            }
            brokercheck.bindCloudletToVm(cloudletList.get(cl), vmList.get(vm));
            System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm)+" at MET: "+minExecTime);
        }
    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

}
