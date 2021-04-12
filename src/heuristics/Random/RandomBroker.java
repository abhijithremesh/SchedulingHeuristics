package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBroker extends DatacenterBrokerSimple {

    public RandomBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();
        Random random = new Random();
        double executionTime[][] = new double[noOfCloudlets][noOfVms];
        double time =0.0;

        ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();
        ArrayList<Vm> vlist = new ArrayList<Vm>();


        for (int i = 0; i < noOfCloudlets; i++) {
            Cloudlet cl = cloudletList.get(i);
            clist.add(cl);
        }

        for (int i = 0; i < noOfVms; i++) {
            Vm vm = vmList.get(i);
            vlist.add(vm);
        }
/*
        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getExecutionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                executionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
            }
        }

 */
        // Binding Cloudlets to random VMs
        for (int i = 0; i <clist.size();i++){
            int cl = i;
            int vm = random.nextInt(vlist.size());
            bindCloudletToVm(clist.get(cl), vlist.get(vm));
            System.out.println(clist.get(cl)+" is bound to "+vlist.get(vm));
        }
    }


    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

}
