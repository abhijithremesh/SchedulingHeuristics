package org.cloudsimplus.examples.HybridApproach;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

public class FirstComeFirstServeHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HybridHeuristicBroker brokerh;


    FirstComeFirstServeHeuristic(HybridHeuristicBroker brokerh, List<Vm> vmList, List<Cloudlet> cloudletList){
        this.brokerh = brokerh;
        this.cloudletList = cloudletList;
        //this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;


    }

    public void firstComeFirstServeScheduling(){


        // Rearranging the remainning cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
            c.setVm(Vm.NULL);}
        }

        // Remaining cloudlets
        //System.out.println("Cloudlets: "+cloudletList);

        for(int i=0;i<cloudletList.size();i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            brokerh.bindCloudletToVm(cl,vm);
        }

        System.out.printf("Performed FCFS.%n%n");


    }


}
