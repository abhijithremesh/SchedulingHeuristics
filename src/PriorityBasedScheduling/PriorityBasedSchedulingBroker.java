package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

public class PriorityBasedSchedulingBroker extends DatacenterBrokerSimple {

    public PriorityBasedSchedulingBroker(final CloudSim simulation) {
        super(simulation);
    }

    // Function which handles the scheduling of cloudlets based on priority
    public List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudletList ) {

        Collections.sort(cloudletList, (Cloudlet c1, Cloudlet c2 ) -> Float.compare(c2.getPriority(), c1.getPriority()));

        return cloudletList;

    }



}
