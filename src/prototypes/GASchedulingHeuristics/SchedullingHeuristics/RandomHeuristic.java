package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    RandomHeuristic(HeuristicBroker brokerh, List<Vm> vmList){
        this.brokerh = brokerh;
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;

    }

    public void randomScheduling() {

        //final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        //new CloudletsTableBuilder(finishedCloudlets).build();

        cloudletList.removeAll(brokerh.getCloudletFinishedList());

        System.out.println("No. of Cloudlets: "+cloudletList.size());
        System.out.println("First Cloudlet: "+cloudletList.get(0).getId());

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



    }





}
