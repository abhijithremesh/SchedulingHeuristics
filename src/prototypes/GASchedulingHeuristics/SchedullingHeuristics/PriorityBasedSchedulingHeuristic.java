package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PriorityBasedSchedulingHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker broker0;

    PriorityBasedSchedulingHeuristic (HeuristicBroker broker0, List<Cloudlet> cloudletList, List<Vm> vmList) {
        this.cloudletList = cloudletList;
        this.vmList = vmList;
        this.broker0 = broker0;

    }

    public void PriorityBasedScheduling() {

        Random random = new Random();

        // Assigning Random Priority to cloudlets
        for (Cloudlet c : cloudletList)
        {
            int randomPriority = random.nextInt(cloudletList.size());
            c.setPriority(randomPriority);
        }

        //System.out.println("******* Before sorting based on Priority ***********");

        // Printing Cloudlets before sorting based on priority
        //for (Cloudlet c : cloudletList) {
        //    System.out.println(" Cloudlet "+ c.getId()+ " has priority "+c.getPriority());
        //}

        Collections.sort(cloudletList, (Cloudlet c1, Cloudlet c2 ) -> Float.compare(c2.getPriority(), c1.getPriority()));

        //System.out.println("******* After sorting based on Priority ***********");

        // Printing Cloudlets after sorting based on priority
        //for (Cloudlet c : cloudletList) {
        //    System.out.println(" Cloudlet "+ c.getId()+ " has priority "+c.getPriority());
        //}

        // Submitting the Cloudletlist to the broker
        broker0.submitCloudletList(cloudletList);

        // Submitting the VMlist to the broker
        broker0.submitVmList(vmList);




    }


}
