package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class ShortestCloudletFastestPEPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    ShortestCloudletFastestPEPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        System.out.println("Scheduling with SCFP Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletCreatedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletCreatedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletCreatedList();

        cloudletList.sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s1.getLength()-s2.getLength()));

        vmList.sort((Vm v1, Vm v2)-> Math.toIntExact((long)(v2.getMips()-v1.getMips())));

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                //Vm v = c.getVm();
                //c.setLength((long)(c.getLength()/v.getMips()));
                c.setVm(Vm.NULL);}
        }

        for(int i=0;i<cloudletList.size();i++){

            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            //cl.setLength(cl.getLength()* (long) vm.getMips());
            myBroker.bindCloudletToVm(cl,vm);

        }


    }

}
