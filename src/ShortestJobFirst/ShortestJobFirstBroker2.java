package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class ShortestJobFirstBroker2 extends DatacenterBrokerSimple {

    public ShortestJobFirstBroker2(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        // Printing cloudlets before sorting
        for (Cloudlet c : cloudletList)
        {
            System.out.println(" Cloudlet "+c.getId()+" length: "+c.getLength());
        }

        // Sorting cloudlets in ascending order with respect to its length
        cloudletList.sort((Cloudlet c1,Cloudlet c2) -> {
            if (c1.getLength() > c2.getLength())
                return 1;
            if (c1.getLength() < c2.getLength())
                return -1;
            return 0;
        });

        System.out.println("******************************************* ");

        // Printing cloudlets after sorting
        for (Cloudlet c : cloudletList)
        {
            System.out.println(" Cloudlet "+c.getId()+" length: "+c.getLength());
        }

        System.out.println("******************************************* ");

        // Binding the sorted cloudlets to the VMs as per the First Come First Serve Policy
        for(int i=0;i<cloudletList.size();i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            bindCloudletToVm(cl,vm);
            System.out.println("Cloudlet "+cl.getId()+" is bound with VM "+vm.getId());
        }

    }


}
