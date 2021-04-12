package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;


public class OpportunisticLoadBalancerBroker extends DatacenterBrokerSimple {

    double min=Integer.MAX_VALUE;

    public OpportunisticLoadBalancerBroker(final CloudSim simulation) {
        super(simulation);
    }

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();
        int index=0;

        double executionTime[][] = new double[noOfCloudlets][noOfVms];
        double time =0.0;

        ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();
        ArrayList<Vm> vlist = new ArrayList<Vm>();
       // List<Vm> vmWaitingList = new List<Vm>();


        for (int i = 0; i < noOfCloudlets; i++) {
            Cloudlet cl = cloudletList.get(i);
            clist.add(cl);
        }

        for (int i = 0; i < noOfVms; i++) {
            Vm vm = vmList.get(i);
            vlist.add(vm);

        }


        for (int i =0; i<clist.size(); i++){

            // Get the first cloudlet
            Cloudlet cl = clist.get(i);
            System.out.println("Current cloudlet: "+cl);

            // Get the next waiting VM
            List<Vm> vmWaitingList  = vlist.get(0).getBroker().getVmWaitingList();
            Vm vm = vmWaitingList.get(0);
            System.out.println("Current VM: "+vm);

            // Binding the cloudlet to the next waiting VM
            bindCloudletToVm(cl, vm);

            System.out.println("Cloudlet "+cl.getId()+" is bound to "+cl.getVm());

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



        /**
        // Binding Cloudlets to the next available VMs
        for (int i = 0; i <clist.size();i++){
            //int cl = i;
            //min =  Integer.MAX_VALUE;
            for (int j = 0; j < vlist.size(); j++) {
                   System.out.println("VM "+vlist.get(j).getId()+" is "+vlist.get(j).getLastBusyTime());
               // if (vlist.get(j).getLastBusyTime() < min) {
               //     min = vlist.get(j).getLastBusyTime();
               //     index = j;
                }
            }
            //bindCloudletToVm(clist.get(cl), vlist.get(index));
            //System.out.println(clist.get(cl)+" is bound to "+vlist.get(index));
        }
         **/

/*

    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }


*/

    }

}
