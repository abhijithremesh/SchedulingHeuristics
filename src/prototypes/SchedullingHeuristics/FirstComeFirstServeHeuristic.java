package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirstComeFirstServeHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    FirstComeFirstServeHeuristic(HeuristicBroker brokerh, List<Vm> vmList){
        this.brokerh = brokerh;
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;

    }

    public void firstComeFirstServeScheduling(){

        //final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        //new CloudletsTableBuilder(finishedCloudlets).build();

        //System.out.println(cloudletList.size());
        cloudletList.removeAll(brokerh.getCloudletFinishedList());
        System.out.println(cloudletList.size());
        System.out.println(vmList.size());

        System.out.println(cloudletList.get(0).getId());

        for (Cloudlet c : cloudletList) {
            c.setVm(Vm.NULL);
        }


        for(int i=0;i<cloudletList.size();i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            brokerh.bindCloudletToVm(cl,vm);
            //System.out.println(cl.getId()+" is bound to "+vm.getId());
        }



    }


}
