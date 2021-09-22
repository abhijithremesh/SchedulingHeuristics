package org.cloudsimplus.examples.HybridApproach;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HybridHeuristicBroker brokerh;

    RandomHeuristic(HybridHeuristicBroker brokerh, List<Vm> vmList, List<Cloudlet> cloudletList){
        this.brokerh = brokerh;
        //this.cloudletList = brokerh.getCloudletSubmittedList();
        this.cloudletList = cloudletList;
        this.vmList = vmList;

    }

    public void randomScheduling() {


        Random random = new Random();

        // Rearranging the remainning cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
            c.setVm(Vm.NULL);}
        }

        // Remaining cloudlets
        //System.out.println("Cloudlets: "+cloudletList);

        // Binding Cloudlets to random VMs
        for (int i = 0; i <cloudletList.size(); i++){
            Cloudlet cl = cloudletList.get(i);
            int v = random.nextInt(vmList.size());
            Vm vm = vmList.get(v);
            brokerh.bindCloudletToVm(cl, vm);
            //System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm));
        }

        System.out.printf("Performed RandomScheduling.%n%n");


    }





}
