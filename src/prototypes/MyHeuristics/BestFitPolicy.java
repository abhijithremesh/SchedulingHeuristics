package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;

public class BestFitPolicy {

    MyBroker myBroker;

    BestFitPolicy (MyBroker myBroker){

        this.myBroker = myBroker;

    }

    public void schedule() {

        for (Cloudlet cloudlet: myBroker.getCloudletSubmittedList()
             ) {

            Vm mappedVm = myBroker.getVmCreatedList()
                .stream()
                .filter(vm -> vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes())
                .min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))
                .orElse(Vm.NULL);

            myBroker.bindCloudletToVm(cloudlet,mappedVm);

        }

    }




}
