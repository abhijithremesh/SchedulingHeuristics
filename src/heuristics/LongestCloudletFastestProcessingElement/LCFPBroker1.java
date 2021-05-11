package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class LCFPBroker1 extends DatacenterBrokerSimple {

    public LCFPBroker1(final CloudSim simulation) {
        super(simulation);
    }

    // Function to sort the VMs in descending order based on the VM MIPS
    public List<Vm> scheduleVms(List<Vm> vmList ) {

        for (int a = 0; a < vmList.size(); a++) {
            for (int b = a + 1; b < vmList.size(); b++) {
                if (vmList.get(b).getMips() > vmList.get(a).getMips()) {
                    Vm temp = vmList.get(a);
                    vmList.set(a, vmList.get(b));
                    vmList.set(b, temp);
                }
            }
        }

        return vmList;

    }

    // Function to sort the Cloudlets in descending order based on the Cloudlet length
    public List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudletList ) {

        for (int a = 0; a < cloudletList.size(); a++) {
            for (int b = a + 1; b < cloudletList.size(); b++) {
                if (cloudletList.get(b).getLength() > cloudletList.get(a).getLength()) {
                    Cloudlet temp = cloudletList.get(a);
                    cloudletList.set(a, cloudletList.get(b));
                    cloudletList.set(b, temp);
                }
            }
        }
        return cloudletList;
    }

    public void mapCloudletsToVM(List<Cloudlet> cloudletList,List<Vm> vmList) {

        System.out.println("******* Inside Broker Cloudlet ***********");

        for (Cloudlet c : cloudletList) {
            System.out.println(" Cloudlet length: " + c.getLength());
        }

        System.out.println("******* Inside Broker VM ***********");

        for (Vm v : vmList) {
            System.out.println(" VM MIPS: "+v.getMips());
        }


        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            bindCloudletToVm(cl,vm);
            System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
        }

    }

}
