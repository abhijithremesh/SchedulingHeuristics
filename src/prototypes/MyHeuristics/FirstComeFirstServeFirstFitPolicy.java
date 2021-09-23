package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class FirstComeFirstServeFirstFitPolicy {

    MyBroker myBroker;
    int lastVmIndex;
    List<Vm> vmList;

    FirstComeFirstServeFirstFitPolicy(MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }


    public void schedule() {


        for (Cloudlet cloudlet:myBroker.getCloudletSubmittedList()
             ) {
            final int maxTries = vmList.size();
            for (int i = 0; i < maxTries; i++) {
                final Vm vm = vmList.get(lastVmIndex);
                if (vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes()) {
                        myBroker.bindCloudletToVm(cloudlet,vm);
                }
                lastVmIndex = ++lastVmIndex % vmList.size();
            }
            myBroker.bindCloudletToVm(cloudlet,Vm.NULL);
        }

    }

}

