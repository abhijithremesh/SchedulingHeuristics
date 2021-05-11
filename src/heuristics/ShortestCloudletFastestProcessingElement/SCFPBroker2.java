package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class SCFPBroker2 extends DatacenterBrokerSimple {

    public SCFPBroker2(final CloudSim simulation) {
        super(simulation);
    }


    public void schedule(List<Cloudlet> cloudletList, List<Vm> vmList) {

        // Sorting the list of cloudlets in descending order of their length.
        for (int a = 0; a < cloudletList.size(); a++) {
            for (int b = a + 1; b < cloudletList.size(); b++) {
                if (cloudletList.get(b).getLength() < cloudletList.get(a).getLength()) {
                    Cloudlet temp = cloudletList.get(a);
                    cloudletList.set(a, cloudletList.get(b));
                    cloudletList.set(b, temp);
                }
            }
        }

        // Sorting the list of Vms in descending order of their MIPS.
        for (int a = 0; a < vmList.size(); a++) {
            for (int b = a + 1; b < vmList.size(); b++) {
                if (vmList.get(b).getMips() > vmList.get(a).getMips()) {
                    Vm temp = vmList.get(a);
                    vmList.set(a, vmList.get(b));
                    vmList.set(b, temp);
                }
            }
        }

         System.out.println("******* Inside Broker Cloudlet ***********");

        for (Cloudlet c : cloudletList) {
            System.out.println(" Cloudlet length: " + c.getLength());
        }

        System.out.println("******* Inside Broker VM ***********");

        for (Vm v : vmList) {
            System.out.println(" VM MIPS: "+v.getMips());
        }

        // Binding the cloudlets from the sorted cloudlet list to the VMs from the sorted vm list.
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
            bindCloudletToVm(cl,vm);
        }

    }

}
