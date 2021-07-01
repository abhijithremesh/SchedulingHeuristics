package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class OpportunisticLoadBalancingHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker broker0;

    OpportunisticLoadBalancingHeuristic(HeuristicBroker broker0, List<Cloudlet> cloudletList, List<Vm> vmList) {
        this.cloudletList = cloudletList;
        this.vmList = vmList;
        this.broker0 = broker0;

    }

    public void opportunisticLoadBalancingScheduling() {

        broker0.submitCloudletList(cloudletList);
        broker0.submitVmList(vmList);

        for (int i =0; i< cloudletList.size(); i++){

            // Get the first cloudlet
            Cloudlet cl = cloudletList.get(i);
            //System.out.println("Current cloudlet: "+cl);

            // Get the next waiting VM
            List<Vm> vmWaitingList  = broker0.getVmWaitingList();
            Vm vm = vmWaitingList.get(0);
            //System.out.println("Current VM: "+vm);

            // Binding the cloudlet to the next waiting VM
            broker0.bindCloudletToVm(cl, vm);

            //System.out.println("Cloudlet "+cl.getId()+" is bound to "+cl.getVm());

        }

    }


}
