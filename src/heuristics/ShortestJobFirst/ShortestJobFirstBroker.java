package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class ShortestJobFirstBroker extends DatacenterBrokerSimple {

    public ShortestJobFirstBroker(final CloudSim simulation) {
        super(simulation);
    }

    public List<Cloudlet> OrderShortestJobFirst(List<Cloudlet> cloudletList){

        System.out.println("\n\tShortest Job First Broker Schedules\n");

        cloudletList.sort((Cloudlet c1,Cloudlet c2) -> {
            if (c1.getLength() > c2.getLength())
                return 1;
            if (c1.getLength() < c2.getLength())
                return -1;
            return 0;
        });
        System.out.println("Sorting the cloudlets....");
        for(int i=0;i<cloudletList.size();i++)
        {
            System.out.println("Inside broker");
            System.out.println("Index "+i+" holds cloudlet "+cloudletList.get(i).getId()+" having length "+cloudletList.get(i).getLength());
        }

        return cloudletList;

    }

}
