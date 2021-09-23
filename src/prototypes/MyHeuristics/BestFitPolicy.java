package org.cloudsimplus.examples.MyHeuristics;

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
