package org.cloudsimplus.examples.HybridModel;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.List;

public class BestFitPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    BestFitPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        System.out.println("Scheduling with Best-Fit Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList= myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        for (Cloudlet cloudlet: myBroker.getCloudletSubmittedList()
        ) {

            Vm mappedVm = vmList
                .stream()
                .filter(vm -> vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes())
                .min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))
                .orElse(Vm.NULL);

            myBroker.bindCloudletToVm(cloudlet,mappedVm);

        }

    }




}
