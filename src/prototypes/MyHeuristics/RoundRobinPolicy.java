package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SchedulingHeuristicsCheck.CheckBroker;

import java.util.List;

public class RoundRobinPolicy {

    MyBroker myBroker;
    int lastSelectedVmIndex = -1;
    List<Vm> vmList;

    RoundRobinPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        for (Vm v: vmList
        ) {
            v.setCloudletScheduler(new CloudletSchedulerTimeShared());
        }


        for (Cloudlet cloudlet : myBroker.getCloudletSubmittedList()
             ) {
            lastSelectedVmIndex = ++lastSelectedVmIndex % vmList.size();
            Vm vm =  myBroker.getWaitingVm(lastSelectedVmIndex);
        }


    }


}
