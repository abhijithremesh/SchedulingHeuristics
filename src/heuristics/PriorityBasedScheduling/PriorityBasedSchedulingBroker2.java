package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PriorityBasedSchedulingBroker2 extends DatacenterBrokerSimple {

    public PriorityBasedSchedulingBroker2(final CloudSim simulation) {
        super(simulation);
    }

    public void schedule(List<Cloudlet> cloudletList, List<Vm> vmList ) {

        Random random = new Random();

        // Assigning Random Priority to cloudlets
        for (Cloudlet c : cloudletList)
        {
            int randomPriority = random.nextInt(cloudletList.size());
            c.setPriority(randomPriority);
        }

        System.out.println("******* Before sorting based on Priority ***********");

        // Printing Cloudlets before sorting based on priority
        for (Cloudlet c : cloudletList) {
            System.out.println(" Cloudlet "+ c.getId()+ " has priority "+c.getPriority());
        }

        Collections.sort(cloudletList, (Cloudlet c1, Cloudlet c2 ) -> Float.compare(c2.getPriority(), c1.getPriority()));

        System.out.println("******* After sorting based on Priority ***********");

        // Printing Cloudlets after sorting based on priority
        for (Cloudlet c : cloudletList) {
            System.out.println(" Cloudlet "+ c.getId()+ " has priority "+c.getPriority());
        }

        // Submitting the Cloudletlist to the broker
        submitCloudletList(cloudletList);

        // Submitting the VMlist to the broker
        submitVmList(vmList);


    }

}
