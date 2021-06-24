package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.MaxMinBroker2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MaxMinHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    MaxMinHeuristic (HeuristicBroker brokerh, List<Vm> vmList){
        this.brokerh = brokerh;
        this.cloudletList = brokerh.getCloudletSubmittedList();
        this.vmList = vmList;


    }

    ArrayList<cloudletVmMin> cloudletVmMinList = new ArrayList<cloudletVmMin>();

    //Class which stores the cloudlet,VM and their respective completion time
    public class cloudletVmMin{
        private int cloudlet;
        private int vm;
        private double completionTime;

        public  cloudletVmMin(int cloudlet, int vm, double completionTime){
            this.cloudlet = cloudlet;
            this.vm = vm;
            this.completionTime = completionTime;
        }

    }

    public void maxMinScheduling(){

        cloudletList.removeAll(brokerh.getCloudletFinishedList());

        System.out.println("No. of Cloudlets: "+cloudletList.size());
        System.out.println("First Cloudlet: "+cloudletList.get(0).getId());

        // Rearranging the remaining cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                c.setVm(Vm.NULL);}
        }

        // Remaining cloudlets
        System.out.println("Cloudlets: "+cloudletList);

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

            //System.out.println(Arrays.deepToString(completionTime));

            // Getting the minimum cloudlet-VM combinations for each cloudlet
            for (int i = 0; i < clist.size(); i++) {
                getMinCompletionTimePerCloudlet(completionTime, i);
            }

            // Getting the maximum cloudlet-VM combo from the above combinations
            int[] Indices = getMaxCompletionTimeAllCloudlet(cloudletVmMinList);
            int maxCloudlet = Indices[0];
            int minVm = Indices[1];

            // Computing the respective completion time for the selected cloudlet-VM combo.
            double maximumCompletionTime = completionTime[maxCloudlet][minVm];

            //System.out.println("maxCloudlet: " + maxCloudlet);
            //System.out.println("minVm: " + minVm);

            Cloudlet maximumCloudlet = clist.get(maxCloudlet);
            Vm minimumVm = vlist.get(minVm);
            //System.out.println("Maximum Cloudlet: " + maximumCloudlet);
            //System.out.println("Minimum VM: " + minimumVm);

            // Binding the respetcive cloudlet to the respective VM
            brokerh.bindCloudletToVm(maximumCloudlet, minimumVm);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < clist.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + maximumCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vlist.size(); i++) {
                completionTime[maxCloudlet][i] = -1.0;
            }


            cloudletVmMinList.clear();

            //System.out.println("*********************************");

        }

    }

    // get completion time of a specific cloudlet and a specific vm
    private double getCompletionTime(Cloudlet cloudlet, Vm vm){
        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
        double completionTime = execTime + waitingTime;
        return completionTime;
    }

    // get execution time of a specific cloudlet and a specific vm
    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }

    // get the minimum completion time per cloudlet
    private void getMinCompletionTimePerCloudlet(double[][] numbers,int c) {
        double minValue = 0;
        int vm=0;
        for (int i = 0; i < numbers[c].length; i++) {
            if(numbers[c][i]==-1){
                continue;
            }
            else{
                minValue = numbers[c][i];
                break;
            }
        }

        for (int i = 0; i < numbers[c].length; i++) {
            if (numbers[c][i] < minValue && numbers[c][i] > 0.0) {
                minValue = numbers[c][i];
                vm = i;
            }
        }
        //System.out.println("Minimum Completion Time for Cloudlet "+c+" : " + minValue+" on VM "+vm);
        cloudletVmMinList.add(new cloudletVmMin(c,vm,minValue));
    }

    private int[] getMaxCompletionTimeAllCloudlet(ArrayList<cloudletVmMin> List) {
        int[] Indices = new int[2];
        double maximumCompletionTimeAllCloudlets = List.get(0).completionTime;
        for (int i=0;i<List.size();i++){
            if (List.get(i).completionTime > maximumCompletionTimeAllCloudlets ){
                maximumCompletionTimeAllCloudlets = List.get(i).completionTime;
                Indices[0]=List.get(i).cloudlet;
                Indices[1]=List.get(i).vm;
            }
        }
        //System.out.println("maximumCompletionTimeAllCloudlets: "+maximumCompletionTimeAllCloudlets);
        return Indices;
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
