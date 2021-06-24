package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FirstComeFirstServeHeuristic {


    private final CloudSim simulation;
    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;


    FirstComeFirstServeHeuristic(HeuristicBroker brokerh, List<Vm> vmList, CloudSim simulation){
        this.brokerh = brokerh;
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;
        this.simulation = simulation;

    }

    public void firstComeFirstServeScheduling(){

        //final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        //new CloudletsTableBuilder(finishedCloudlets).build();

        cloudletList.removeAll(brokerh.getCloudletFinishedList());

        System.out.println("No. of Cloudlets: "+cloudletList.size());
        System.out.println("First Cloudlet: "+cloudletList.get(0).getId());

        // Rearranging the remainning cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
            c.setVm(Vm.NULL);}
        }

        // Remaining cloudlets
        System.out.println(cloudletList);

        for(int i=0;i<cloudletList.size();i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            brokerh.bindCloudletToVm(cl,vm);
        }

    }


}
