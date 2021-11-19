package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

public class FirstComeFirstServeHeuristic {

    MyHeuristicBroker myBroker;
    List<Vm> vmList;

    FirstComeFirstServeHeuristic(MyHeuristicBroker myBroker, List<Vm> vmList) {

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    public void schedule() {

        System.out.println("Scheduling with FCFS Policy");

        System.out.println("Cloudlets waiting: " + myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: " + myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true) {
                Vm v = c.getVm();
                c.setVm(Vm.NULL);}
            }

        final Comparator<Cloudlet> sortById = comparingLong(cl -> cl.getId());
        cloudletList.sort(sortById);

        for (int i = 0; i < cloudletList.size(); i++) {
                Cloudlet cl = cloudletList.get(i);
                Vm vm = vmList.get((i % vmList.size()));
                cl.setLength(cl.getLength()* (long) vm.getMips());
                myBroker.bindCloudletToVm(cl, vm);
        }


        }

    }

