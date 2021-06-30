package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.SchedullingHeuristics.HeuristicBroker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    CheckBroker brokercheck;

    RandomCheck(List<Cloudlet> cloudletList, List<Vm> vmList, CheckBroker brokercheck){
        this.brokercheck = brokercheck;
        this.cloudletList = cloudletList;
        this.vmList = vmList;

    }

    public void RandomChecking() {

        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        Random random = new Random();

        // Binding Cloudlets to random VMs
        for (int i = 0; i <cloudletList.size(); i++){
            Cloudlet cl = cloudletList.get(i);
            int v = random.nextInt(vmList.size());
            Vm vm = vmList.get(v);
            brokercheck.bindCloudletToVm(cl, vm);
            //System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm));
        }



    }





}
