package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class MinimumExecutionTimeBroker2 extends DatacenterBrokerSimple {

    double minExecTime=Integer.MAX_VALUE;
    int vm=0;

    public MinimumExecutionTimeBroker2(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();

        System.out.println("minExecTime: " + minExecTime);

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

        double executionTime[][] = new double[noOfCloudlets][noOfVms];

        double time =0.0;

        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getExecutionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                executionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
            }
        }

        for (int i = 0; i <clist.size();i++){
            int cl = i;
            minExecTime=Integer.MAX_VALUE;
            System.out.println("Cloudlet waiting time: "+clist.get(cl).getWaitingTime());
            for (int j = 0; j < vlist.size(); j++) {
                if (executionTime[i][j] < minExecTime) {
                    minExecTime = executionTime[i][j];
                    vm = j;
                }
            }
            bindCloudletToVm(clist.get(cl), vlist.get(vm));
            System.out.println(clist.get(cl)+" is bound to "+vlist.get(vm)+" at MET: "+minExecTime);
        }
    }


    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }


}
