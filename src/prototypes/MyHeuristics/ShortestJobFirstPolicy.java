package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ShortestJobFirstPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    ShortestJobFirstPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        List<Cloudlet> cloudletList;

        System.out.println("Scheduling with SJF Policy");

        if (myBroker.getCloudletWaitingList().isEmpty()) {
            cloudletList  = myBroker.getCloudletCreatedList();
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
        } else {
            cloudletList = myBroker.getCloudletWaitingList();
            System.out.println("Cloudlets waiting: "+cloudletList.size());
        }

        System.out.println("Cloudlets remaining: "+cloudletList.size());

        cloudletList.sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s1.getLength()-s2.getLength()));

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            //cl.setLength(cl.getLength()* (long) vm.getMips());
            myBroker.bindCloudletToVm(cl,vm);

        }
    }

}

