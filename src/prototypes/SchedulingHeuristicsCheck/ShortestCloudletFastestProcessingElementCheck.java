package org.cloudsimplus.examples.SchedulingHeuristicsCheck;


import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

public class ShortestCloudletFastestProcessingElementCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    CheckBroker brokercheck;

    ShortestCloudletFastestProcessingElementCheck (List<Cloudlet> cloudletList,List<Vm> vmList, CheckBroker brokercheck){
        this.cloudletList = cloudletList;
        this.vmList = vmList;
        this.brokercheck = brokercheck;

    }


    public void SCFPChecking1(){

        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        // Sorting the list of cloudlets in ascending order of their length.
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

        // Binding the cloudlets from the sorted cloudlet list to the VMs from the sorted vm list.
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            //System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
            brokercheck.bindCloudletToVm(cl,vm);
        }

    }


    public void SCFPChecking2(){

        // Sorting the list of cloudlets in ascending order of their length.
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

        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        // Binding the cloudlets from the sorted cloudlet list to the VMs from the sorted vm list.
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            //System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
            brokercheck.bindCloudletToVm(cl,vm);
        }

    }

}

