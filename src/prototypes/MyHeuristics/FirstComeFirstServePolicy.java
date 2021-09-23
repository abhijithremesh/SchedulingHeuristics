package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class FirstComeFirstServePolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    FirstComeFirstServePolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule(){

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            myBroker.bindCloudletToVm(cl,vm);

        }





    }

}
