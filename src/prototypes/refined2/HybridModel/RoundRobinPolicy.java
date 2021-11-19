package org.cloudsimplus.examples.HybridModel;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;

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

        System.out.println("Scheduling with Round-Robin Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        for (Vm v: vmList
        ) {
            v.setCloudletScheduler(new CloudletSchedulerTimeShared());
        }


        for (Cloudlet cloudlet : cloudletList
        ) {
            lastSelectedVmIndex = ++lastSelectedVmIndex % vmList.size();
            Vm vm =  myBroker.getWaitingVm(lastSelectedVmIndex);
        }


    }


}
