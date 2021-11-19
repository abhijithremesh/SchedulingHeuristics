package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class MinimumCompletionTimeHeuristic {

    MyHeuristicBroker myBroker;
    List<Vm> vmList;

    MinimumCompletionTimeHeuristic (MyHeuristicBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule(){

        System.out.println("Scheduling with MCT Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

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

            cloudletList.get(cl).setLength(cloudletList.get(cl).getLength()* (long) vmList.get(vm).getMips());
            myBroker.bindCloudletToVm(cloudletList.get(cl), vmList.get(vm));
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
