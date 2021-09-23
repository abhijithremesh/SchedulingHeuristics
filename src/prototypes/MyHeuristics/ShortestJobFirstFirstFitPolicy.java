package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class ShortestJobFirstFirstFitPolicy {

    MyBroker myBroker;
    int lastVmIndex;
    List<Vm> vmList;

    ShortestJobFirstFirstFitPolicy(MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        myBroker.getCloudletWaitingList().sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s1.getLength()-s2.getLength()));

        for (Cloudlet cloudlet:myBroker.getCloudletWaitingList()
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
