package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class MinimumCompletionTimeBroker2 extends DatacenterBrokerSimple {

    double minCompTime=Integer.MAX_VALUE;
    int vm=0;

    public MinimumCompletionTimeBroker2(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();

        System.out.println("minExecTime: " + minCompTime);

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

        double completionTime[][] = new double[noOfCloudlets][noOfVms];

        double time =0.0;

        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getCompletionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        for (int i = 0; i <clist.size();i++){
            int cl = i;
            minCompTime=Integer.MAX_VALUE;
            for (int j = 0; j < vlist.size(); j++) {
                if (completionTime[i][j] < minCompTime) {
                    minCompTime = completionTime[i][j];
                    vm = j;
                }
            }
            bindCloudletToVm(clist.get(cl), vlist.get(vm));
            System.out.println(clist.get(cl)+" is bound to "+vlist.get(vm)+" at MET: "+minCompTime);
        }

        /*
        for (int i=0;i < clist.size();i++){
            getDetails(clist.get(i));
        }

         */
    }

    private double getCompletionTime(Cloudlet cloudlet, Vm vm){

        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
        double completionTime = execTime + waitingTime;
        return completionTime;

    }

    private void getDetails(Cloudlet cloudlet){
        double responseTime = cloudlet.getFinishTime()-cloudlet.getExecStartTime();
        System.out.println("***************************************************");
        System.out.println("PEs of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getNumberOfPes());
        System.out.println("VM of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getVm());
        System.out.println("Waiting Time of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getWaitingTime());
        System.out.println("Exec Start Time of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getExecStartTime());
        System.out.println("Finish Time of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getFinishTime());
        System.out.println("Priority of Cloudlet "+cloudlet.getId()+" = "+cloudlet.getPriority());
        System.out.println("Response Time of Cloudlet "+cloudlet.getId()+" = "+responseTime);

    }

}
