package org.cloudsimplus.examples.SchedulingHeuristicsCheck;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SchedullingHeuristics.HeuristicBroker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MinMinCheck {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    CheckBroker brokercheck;

    MinMinCheck (List<Cloudlet> cloudletList, List<Vm> vmList, CheckBroker brokercheck){

        this.brokercheck = brokercheck;
        this.cloudletList = cloudletList;
        this.vmList = vmList;

    }



    public void MinMinChecking(){

        brokercheck.submitCloudletList(cloudletList);
        brokercheck.submitVmList(vmList);

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();
        //System.out.println("noOfVms: "+noOfVms);
        //System.out.println("noOfCloudlets: "+noOfCloudlets);

        // Storing the cloudlets and VMs
        ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();
        ArrayList<Vm> vlist = new ArrayList<Vm>();

        for (int i=0;i<noOfCloudlets;i++){
            Cloudlet cl = cloudletList.get(i);
            clist.add(cl);
        }

        for (int i=0;i<noOfVms;i++){
            Vm vm = vmList.get(i);
            vlist.add(vm);
        }

        // Completion time matrix and execution time matrix for cloudlets-VM
        double completionTime[][] = new double[noOfCloudlets][noOfVms];
        double executionTime[][] = new double[noOfCloudlets][noOfVms];

        // Init some variables
        double time =0.0;
        int minCloudlet = 0;
        int minVm = 0;


        // Computing the completion time matrix for cloudlet-VM
        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getCompletionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        //System.out.println("*********************************");

        for(int c=0; c< clist.size(); c++) {

            //System.out.println("*********************************");

            //System.out.println("clist: "+clist);

            //System.out.println(Arrays.deepToString(completionTime));

            // Getting the minimum completion time from the completion time matrix
            double MinimumCompletionTime = getMinValue(completionTime);
            //System.out.println("Minimum Completion Time: " + getMinValue(completionTime));

            // Getting the respective indices (cloudlet,VM) of the minimum completion time value
            int[] Indices = getIndices(completionTime, MinimumCompletionTime);

            // Getting the cloudlet-VM  with minimum completion time from the completion time matrix
            minCloudlet = Indices[0];
            minVm = Indices[1];

            Cloudlet minimumCloudlet = clist.get(minCloudlet);
            Vm minimumVm = vlist.get(minVm);
            //System.out.println("Minimum Cloudlet: " + minimumCloudlet);
            //System.out.println("Minimum VM: " + minimumVm);

            // Binding the cloudlet to the respective VM
            brokercheck.bindCloudletToVm(minimumCloudlet, minimumVm);

            //System.out.println(minimumCloudlet+" gets mapped to "+minimumVm+" with completion time, "+MinimumCompletionTime);


            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < clist.size(); i++) {
                if(completionTime[i][minVm] != -1 ){
                    completionTime[i][minVm] = completionTime[i][minVm] + MinimumCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vlist.size(); i++) {
                completionTime[minCloudlet][i] = -1.0;
            }

            //System.out.println(Arrays.deepToString(completionTime));


        }


    }

    private double getCompletionTime(Cloudlet cloudlet, Vm vm){
        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());

        double completionTime = execTime + waitingTime;

        return completionTime;
    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

    private double getMinValue(double[][] numbers) {
        double minValue = 0;

        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if(numbers[j][i]==-1){
                    continue;
                }
                else{
                    minValue = numbers[j][i];
                    break;
                }

            }

        }


        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] < minValue && numbers[j][i] > 0.0) {
                    minValue = numbers[j][i];
                }
            }
        }
        return minValue ;
    }

    private int[] getIndices(double[][] numbers,double value) {
        int[] Indices = new int[2];
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] == value && numbers[i][j] > 0.0) {
                    Indices[0]=i;
                    Indices[1]=j;
                }
            }
        }
        return Indices;
    }


}
