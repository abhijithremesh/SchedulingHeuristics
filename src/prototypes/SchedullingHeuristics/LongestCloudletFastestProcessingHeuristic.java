package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.List;

public class LongestCloudletFastestProcessingHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    LongestCloudletFastestProcessingHeuristic (HeuristicBroker brokerh, List<Vm> vmList){
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;
        this.brokerh = brokerh;

    }

    public void longestCloudletFastestProcessingScheduling(){

        final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        //System.out.println(cloudletList.size());
        cloudletList.removeAll(brokerh.getCloudletFinishedList());
        System.out.println(cloudletList.size());

        cloudletList.forEach(c-> System.out.println(c.getId()+" "+c.getLength()));
        vmList.forEach(c-> System.out.println(c.getId()+" "+c.getMips()));

        //System.out.println(cloudletList.get(0).getId());

        for (Cloudlet c : cloudletList) {
            c.setVm(Vm.NULL);
        }

        // Sorting the list of cloudlets in descending order of their length.
        for (int a = 0; a < cloudletList.size(); a++) {
            for (int b = a + 1; b < cloudletList.size(); b++) {
                if (cloudletList.get(b).getLength() > cloudletList.get(a).getLength()) {
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

        cloudletList.forEach(c-> System.out.println(c.getId()+" "+c.getLength()));
        vmList.forEach(c-> System.out.println(c.getId()+" "+c.getMips()));

        //brokerh.submitCloudletList(cloudletList);
        //brokerh.submitVmList(vmList);

        // Binding the cloudlets from the sorted cloudlet list to the VMs from the sorted vm list.
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
            brokerh.bindCloudletToVm(cl,vm);
        }


    }


}
