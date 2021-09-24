package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    RandomPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        Random random = new Random();

        System.out.println("Scheduling with Random Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList= myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        for (int i = 0; i <cloudletList.size(); i++){

            Cloudlet cl = cloudletList.get(i);
            int v = random.nextInt(vmList.size());
            Vm vm = vmList.get(v);
            myBroker.bindCloudletToVm(cl, vm);


        }

        }
    }





