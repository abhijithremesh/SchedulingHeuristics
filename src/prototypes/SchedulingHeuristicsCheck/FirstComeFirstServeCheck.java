package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.SchedulingHeuristicsCheck.CheckBroker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FirstComeFirstServeCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    CheckBroker brokercheck;


    FirstComeFirstServeCheck (List<Cloudlet> cloudletList,List<Vm> vmList, CheckBroker brokercheck){

        this.brokercheck = brokercheck;
        this.cloudletList = cloudletList;
        this.vmList = vmList;

    }

    public void FCFSChecking(){

        // Remaining cloudlets
        //System.out.println("Cloudlets: "+cloudletList);
        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        for(int i=0;i<cloudletList.size();i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            brokercheck.bindCloudletToVm(cl,vm);
        }

    }


}

