package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaxMinBroker3 extends DatacenterBrokerSimple {

    public MaxMinBroker3(final CloudSim simulation) {
        super(simulation);
    }


    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ){

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();
        System.out.println("noOfVms: "+noOfVms);
        System.out.println("noOfCloudlets: "+noOfCloudlets);

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
        int maxCloudlet = 0;
        int vm = 0;

        // Computing the completion time matrix for cloudlet-VM
        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getCompletionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        // Computing the execution time matrix for cloudlet-VM
        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getExecutionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                executionTime[i][j] = time;
                System.out.println("Execution Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
            }
        }

        for(int c=0; c< clist.size(); c++) {

            System.out.println("*********************************");

            System.out.println("clist: "+clist);
            System.out.println(Arrays.deepToString(completionTime));

            // Getting the maximum completion time from the completion time matrix
            double MaximumCompletionTime = getMaxValue(completionTime);
            System.out.println("Maximum Completion Time: " + MaximumCompletionTime);

            // Getting the respective indices (cloudlet,VM) of the maximum completion time value
            int[] Indices = getIndices(completionTime, MaximumCompletionTime);

            // Getting the cloudlet-VM  with maximum completion time from the completion time matrix
            maxCloudlet = Indices[0];
            vm = Indices[1];
            System.out.println("Max Cloudlet : " + maxCloudlet);
            System.out.println("VM : " + vm);

            // Initializing MinExecTime value as the highest possible integer value
            double minExecTime = Integer.MAX_VALUE;

            // Finding the VM which gives minimum execution time for this selected cloudlet
            for (int j = 0; j < vlist.size(); j++) {
                if (executionTime[maxCloudlet][j] < minExecTime) {
                    minExecTime = executionTime[maxCloudlet][j];
                    vm = j;
                }
            }

            // The corresponding completion time for the selected cloudlet and VM
            double finalCompletionTime = completionTime[maxCloudlet][vm];

            // Binding the cloudlet to the respective VM
            bindCloudletToVm(clist.get(maxCloudlet), vlist.get(vm));
            System.out.println(clist.get(maxCloudlet) + " is bound to " + vlist.get(vm) + " at MET: " + minExecTime);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < clist.size(); i++) {
                if (completionTime[i][vm] != -1) {
                    completionTime[i][vm] = completionTime[i][vm] + finalCompletionTime;
                    completionTime[i][vm] = Math.round(completionTime[i][vm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vlist.size(); i++) {
                completionTime[maxCloudlet][i] = -1.0;
            }

        }

    }

    // Get completion time of a specific cloudlet and a specific VM
    private double getCompletionTime(Cloudlet cloudlet, Vm vm){
        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
        double completionTime = execTime + waitingTime;
        return completionTime;
    }

    // Get execution time of a specific cloudlet and a specific VM
    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

    // Get maximum completion time from the maximum completion time matrix
    private double getMaxValue(double[][] numbers) {
        double maxValue = 0;

        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if(numbers[j][i]==-1){
                    continue;
                }
                else{
                    maxValue = numbers[j][i];
                    break;
                }
            }
        }

        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue && numbers[j][i] > 0.0) {
                    maxValue = numbers[j][i];
                }
            }
        }

        return maxValue ;
    }

    // Get the respective indices(x > cloudlet, y > vm) of a value from the completion time matrix
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
