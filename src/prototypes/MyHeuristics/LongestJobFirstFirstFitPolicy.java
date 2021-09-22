package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

public class LongestJobFirstFirstFitPolicy {

    MyBroker myBroker;
    int lastVmIndex;

    LongestJobFirstFirstFitPolicy(MyBroker myBroker){

        this.myBroker = myBroker;

    }

    public void schedule() {

        myBroker.getCloudletWaitingList().sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s2.getLength()-s1.getLength()));

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
