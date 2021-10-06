package org.cloudsimplus.examples.MyBasic;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class DummyBroker extends DatacenterBrokerSimple {

    public DummyBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList ){

        List<Cloudlet> cloudletList = this.getCloudletSubmittedList();

        cloudletList.forEach(c->c.setVm(Vm.NULL));

        cloudletList.forEach(c-> System.out.println(c.getVm()));


    }

}
