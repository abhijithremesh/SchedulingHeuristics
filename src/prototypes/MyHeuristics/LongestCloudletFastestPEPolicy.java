package org.cloudsimplus.examples.MyHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class LongestCloudletFastestPEPolicy {

    MyBroker myBroker;
    List<Vm> vmList;

    LongestCloudletFastestPEPolicy (MyBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        System.out.println("Scheduling with LCFP Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList= myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        cloudletList.sort((Cloudlet s1, Cloudlet s2)-> Math.toIntExact(s2.getLength()-s1.getLength()));

        vmList.sort((Vm v1, Vm v2)-> Math.toIntExact(v2.getNumberOfPes()-v1.getNumberOfPes()));


            for(int i=0;i<cloudletList.size();i++){

                Cloudlet cl = cloudletList.get(i);
                Vm vm = vmList.get((i % vmList.size()));
                myBroker.bindCloudletToVm(cl,vm);

            }


        }

    }


