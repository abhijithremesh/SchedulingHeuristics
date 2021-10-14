package org.cloudsimplus.examples.HybridModel;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;


import java.util.List;

public class MinimumExecutionTimePolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    MinimumExecutionTimePolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule(){

        System.out.println("Scheduling with MET Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        /*
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

         */

        double executionTime[][] = new double[cloudletList.size()][vmList.size()];

        double time =0.0;

        // Generating Execution Time matrix for cloudlet and VM
        for(int i=0; i < cloudletList.size(); i++){
            for(int j=0;j < vmList.size(); j++){
                time=getExecutionTime(cloudletList.get(i),vmList.get(j));
                time = Math.round(time*100.0)/100.0;
                executionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
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

            //cloudletList.get(cl).setLength(cloudletList.get(cl).getLength()* (long) vmList.get(vm).getMips());
            myBroker.bindCloudletToVm(cloudletList.get(cl), vmList.get(vm));
            //System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm)+" at MET: "+minExecTime);
        }


    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

}
