package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class LongestJobFirstPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    LongestJobFirstPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        myBroker.getCloudletWaitingList().sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s2.getLength()-s1.getLength()));

        List<Cloudlet> cloudletList = myBroker.getCloudletWaitingList();


        for(int i=0; i < cloudletList.size(); i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            myBroker.bindCloudletToVm(cl,vm);


        }





    }


}
