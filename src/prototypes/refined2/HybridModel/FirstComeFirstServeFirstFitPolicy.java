package org.cloudsimplus.examples.HybridModel;

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

        System.out.println("Scheduling with FCFS-FirstFit Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList= myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }



        for (Cloudlet cloudlet:cloudletList
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
