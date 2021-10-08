package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class ShortestJobFirstFirstFitHeuristic {

    MyHeuristicBroker myBroker;
    int lastVmIndex;
    List<Vm> vmList;

    ShortestJobFirstFirstFitHeuristic(MyHeuristicBroker myBroker, List<Vm> vmList) {

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        List<Cloudlet> cloudletList;

        System.out.println("Scheduling with SJF-FirstFit Policy");

        if (myBroker.getCloudletWaitingList().isEmpty()) {
            cloudletList = myBroker.getCloudletCreatedList();
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
        } else {
            cloudletList = myBroker.getCloudletWaitingList();
            System.out.println("Cloudlets waiting: " + cloudletList.size());
        }

        System.out.println("Cloudlets remaining: " + cloudletList.size());

        cloudletList.sort((Cloudlet s1, Cloudlet s2) -> Math.toIntExact(s1.getLength() - s2.getLength()));

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true) {
                c.setVm(Vm.NULL);
            }
        }
        for (Cloudlet cloudlet : cloudletList
        ) {

            final int maxTries = vmList.size();
            for (int i = 0; i < maxTries; i++) {
                final Vm vm = vmList.get(lastVmIndex);
                if (vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes()) {
                    myBroker.bindCloudletToVm(cloudlet, vm);
                }
                lastVmIndex = ++lastVmIndex % vmList.size();
            }
            myBroker.bindCloudletToVm(cloudlet, Vm.NULL);
        }

/*
        for (Cloudlet cloudlet: cloudletList
             ) {
            if (cloudlet.isBoundToVm()){
                Vm v = cloudlet.getVm();
                cloudlet.setLength(cloudlet.getLength()*(long)v.getMips());
            }
        }
 */


    }

}

