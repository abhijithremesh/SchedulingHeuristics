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

    RoundRobinPolicy (MyBroker myBroker){

        this.myBroker = myBroker;

    }

    public void schedule() {

        for (Vm v: myBroker.getVmWaitingList()
        ) {
            v.setCloudletScheduler(new CloudletSchedulerTimeShared());
        }


        for (Cloudlet cloudlet : myBroker.getCloudletWaitingList()
             ) {
            lastSelectedVmIndex = ++lastSelectedVmIndex % myBroker.getVmWaitingList().size();
            Vm vm =  myBroker.getWaitingVm(lastSelectedVmIndex);
        }


    }


}
