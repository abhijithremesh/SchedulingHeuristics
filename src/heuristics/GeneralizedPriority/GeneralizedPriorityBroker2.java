package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class GeneralizedPriorityBroker2 extends DatacenterBrokerSimple {

    public GeneralizedPriorityBroker2(final CloudSim simulation) {
        super(simulation);
    }

    public void schedule(List<Cloudlet> cloudletList, List<Vm> vmList ) {

        // sort the VMs in descending order based on the VM MIPS
        for (int a = 0; a < vmList.size(); a++) {
            for (int b = a + 1; b < vmList.size(); b++) {
                if (vmList.get(b).getMips() > vmList.get(a).getMips()) {
                    Vm temp = vmList.get(a);
                    vmList.set(a, vmList.get(b));
                    vmList.set(b, temp);
                }
            }
        }

        // sort the Cloudlets in descending order based on the Cloudlet length
        for (int a = 0; a < cloudletList.size(); a++) {
            for (int b = a + 1; b < cloudletList.size(); b++) {
                if (cloudletList.get(b).getLength() > cloudletList.get(a).getLength()) {
                    Cloudlet temp = cloudletList.get(a);
                    cloudletList.set(a, cloudletList.get(b));
                    cloudletList.set(b, temp);
                }
            }
        }

        System.out.println("******************* After Scheduling ********************");

        for (Cloudlet c :cloudletList) {
            System.out.println("Cloudlet "+c.getId()+" has length: "+c.getLength());
        }

        for (Vm v :vmList) {
            System.out.println("VM "+v.getId()+" has VM MIPS: "+v.getMips());
        }

        submitVmList(vmList);

        submitCloudletList(cloudletList);

    }

}
