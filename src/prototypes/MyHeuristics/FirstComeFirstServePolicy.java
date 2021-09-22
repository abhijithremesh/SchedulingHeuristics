package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class FirstComeFirstServePolicy {

    MyBroker myBroker;

    FirstComeFirstServePolicy (MyBroker myBroker){

        this.myBroker = myBroker;

    }

    public void schedule(){

        List<Cloudlet> cloudletList = myBroker.getCloudletWaitingList();
        List<Vm> vmList = myBroker.getVmWaitingList();


        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            myBroker.bindCloudletToVm(cl,vm);

        }





    }

}
