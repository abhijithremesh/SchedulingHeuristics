package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

public class LongestCloudletFastestPEHeuristic {

    MyHeuristicBroker myBroker;
    List<Vm> vmList;

    LongestCloudletFastestPEHeuristic (MyHeuristicBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        List<Cloudlet> cloudletList;

        System.out.println("Scheduling with SCFP Policy");

        if (myBroker.getCloudletWaitingList().isEmpty()) {
            cloudletList  = myBroker.getCloudletCreatedList();
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
        } else {
            cloudletList = myBroker.getCloudletWaitingList();
            System.out.println("Cloudlets waiting: "+cloudletList.size());
        }

        System.out.println("Cloudlets remaining: "+cloudletList.size());

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                //Vm v = c.getVm();
                //c.setLength((long)(c.getLength()/v.getMips()));
                c.setVm(Vm.NULL);}
        }

        final Comparator<Cloudlet> sortByLength = comparingLong(cl -> cl.getLength());
        final Comparator<Vm> sortByMIPS = comparingDouble(v -> v.getMips());
        cloudletList.sort(sortByLength.reversed());
        vmList.sort(sortByMIPS.reversed());

        //cloudletList.sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s2.getLength()-s1.getLength()));
        //vmList.sort((Vm v1, Vm v2)-> Math.toIntExact((long)(v2.getMips()-v1.getMips())));


        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            cl.setLength(cl.getLength()* (long) vm.getMips());
            myBroker.bindCloudletToVm(cl,vm);

        }


    }

}
