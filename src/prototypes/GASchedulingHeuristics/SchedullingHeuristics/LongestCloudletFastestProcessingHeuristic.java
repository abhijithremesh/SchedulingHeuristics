package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public void longestCloudletFastestProcessingScheduling1(){

        //final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        //new CloudletsTableBuilder(finishedCloudlets).build();

        cloudletList.removeAll(brokerh.getCloudletFinishedList());

        System.out.println("Remaining Cloudlets: "+cloudletList.size());
        System.out.println("First Cloudlet: "+cloudletList.get(0).getId());

        // Rearranging the remainning cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
            c.setVm(Vm.NULL);}
        }

        // Remaining cloudlets
        //System.out.println("Cloudlets: "+cloudletList);

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


        // Binding the cloudlets from the sorted cloudlet list to the VMs from the sorted vm list.
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            //System.out.println("Binding Cloudlet "+cl.getId()+" of length "+cl.getLength()+" to VM "+vm.getId()+" with VM MIPS "+vm.getMips());
            brokerh.bindCloudletToVm(cl,vm);
        }


    }


    public void longestCloudletFastestProcessingScheduling2(){

        final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        cloudletList.removeAll(brokerh.getCloudletFinishedList());
        System.out.println(cloudletList.size());

        //cloudletList.forEach(c-> System.out.println(c.getId()+" "+c.getLength()));
        //vmList.forEach(c-> System.out.println(c.getId()+" "+c.getMips()));

        //System.out.println(cloudletList.get(0).getId());

        for (Cloudlet c : cloudletList) {
            c.setVm(Vm.NULL);
        }

        Comparator<Cloudlet> cmpCloudlet = new Comparator<Cloudlet>() {
            public int compare(Cloudlet c1, Cloudlet c2) {
                return Double.compare(c1.getLength(), c2.getLength());
            }
        };

        //System.out.println("*******************************************");

        Comparator<Vm> cmpVm = new Comparator<Vm>() {
            public int compare(Vm v1, Vm v2) {
                return Double.compare(v1.getMips(), v2.getMips());
            }
        };

        int i = 0;

        while (cloudletList.isEmpty() == false) {
            Cloudlet c = Collections.max(cloudletList, cmpCloudlet);
            //brokerh.getVmWaitingList().forEach(p-> System.out.println(p.getId()+" "+p.getMips()));
            Vm v = Collections.max(brokerh.getVmWaitingList(), cmpVm);
            //System.out.println(c.getId()+" "+c.getLength()+" "+v.getId()+" "+v.getMips());
            brokerh.bindCloudletToVm(c,v);
            cloudletList.remove(c);
        }


    }


}
