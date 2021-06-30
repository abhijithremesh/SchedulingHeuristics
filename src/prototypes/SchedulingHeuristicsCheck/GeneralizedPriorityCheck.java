package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class GeneralizedPriorityCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    GeneralizedPriorityCheck (HeuristicBroker brokerh, List<Cloudlet> cloudletList, List<Vm> vmList){
        this.cloudletList = brokerh.getCloudletSubmittedList();;
        this.vmList = vmList;
        this.brokerh = brokerh;

    }

    public void generalizedPriorityScheduling(){

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


    }


}
