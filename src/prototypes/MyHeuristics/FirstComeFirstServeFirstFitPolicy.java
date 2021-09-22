package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;

public class FirstComeFirstServeFirstFitPolicy {

    MyBroker myBroker;
    int lastVmIndex;

    FirstComeFirstServeFirstFitPolicy(MyBroker myBroker){

        this.myBroker = myBroker;

    }


    public void schedule() {


        for (Cloudlet cloudlet:myBroker.getCloudletWaitingList()
             ) {
            final int maxTries = myBroker.getVmCreatedList().size();
            for (int i = 0; i < maxTries; i++) {
                final Vm vm = myBroker.getVmWaitingList().get(lastVmIndex);
                if (vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes()) {
                        myBroker.bindCloudletToVm(cloudlet,vm);
                }
                lastVmIndex = ++lastVmIndex % myBroker.getVmCreatedList().size();
            }
            myBroker.bindCloudletToVm(cloudlet,Vm.NULL);
        }

    }

}

