package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class FirstComeFirstServeBroker extends DatacenterBrokerSimple {

    public FirstComeFirstServeBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTaskstoVms(List<Cloudlet> cloudletList,List<Vm> vmList){

        int numberOfCloudlets = cloudletList.size();
        int numberOfVMs = vmList.size();
        System.out.println("\n\tFCFS Broker Schedules\n");

        for(int i=0;i<numberOfCloudlets;i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i%numberOfVMs));
            bindCloudletToVm(cl,vm);
            //System.out.println("Task"+cloudletList.get(i).getCloudletId()+" is bound with VM"+vmList.get(i%reqVms).getId());
        }

    }

}
