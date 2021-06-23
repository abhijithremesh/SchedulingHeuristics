package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    RandomHeuristic(HeuristicBroker brokerh, List<Vm> vmList){
        this.brokerh = brokerh;
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;

    }

    public void randomScheduling() {

        final List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        //System.out.println(cloudletList.size());
        cloudletList.removeAll(brokerh.getCloudletFinishedList());
        System.out.println(cloudletList.size());

        Random random = new Random();

        //broker0.submitCloudletList(cloudletList);
        //broker0.submitVmList(vmList);

        System.out.println(cloudletList.get(0).getId());

        for (Cloudlet c : cloudletList) {
            c.setVm(Vm.NULL);
        }

        // Binding Cloudlets to random VMs
        for (int i = 0; i <cloudletList.size(); i++){
            Cloudlet cl = cloudletList.get(i);
            int v = random.nextInt(vmList.size());
            Vm vm = vmList.get(v);
            brokerh.bindCloudletToVm(cl, vm);
            //System.out.println(cloudletList.get(cl)+" is bound to "+vmList.get(vm));
        }

    }

    private List<Cloudlet> getRemainingCloudlets(List<Cloudlet> cloudletList){

        List <Cloudlet> cloudletListRemaining = new ArrayList<Cloudlet>(cloudletList);
        List <Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();

        cloudletListRemaining.removeAll(finishedCloudlets);

        return cloudletListRemaining;
    }

}
