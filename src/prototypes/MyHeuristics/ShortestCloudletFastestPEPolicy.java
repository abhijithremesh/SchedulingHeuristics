package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class ShortestCloudletFastestPEPolicy {

    MyBroker myBroker;

    ShortestCloudletFastestPEPolicy (MyBroker myBroker){

        this.myBroker = myBroker;

    }

    public void schedule() {

        myBroker.getCloudletWaitingList().sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s1.getLength()-s2.getLength()));

        myBroker.getVmWaitingList().sort((Vm v1, Vm v2)-> Math.toIntExact(v2.getNumberOfPes()-v1.getNumberOfPes()));

        List<Cloudlet> cloudletList =  myBroker.getCloudletWaitingList();
        List<Vm> vmList =  myBroker.getVmWaitingList();

        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            myBroker.bindCloudletToVm(cl,vm);

        }


    }

}
