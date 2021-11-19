package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class MaxMinHeuristic {

    MyHeuristicBroker myBroker;
    List<Vm> vmList;

    MaxMinHeuristic (MyHeuristicBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
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

    public void schedule(){

        System.out.println("Scheduling with MAX_MIN Policy");

        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

        myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());

        System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());

        List<Cloudlet> cloudletList = myBroker.getCloudletSubmittedList();

        /*
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm() == true){
                //Vm v = c.getVm();
                //c.setLength((long)(c.getLength()/v.getMips()));
                c.setVm(Vm.NULL);}
        }
         */

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();

        // Completion time matrix and execution time matrix for cloudlets-VM
        double completionTime[][] = new double[noOfCloudlets][noOfVms];
        double executionTime[][] = new double[noOfCloudlets][noOfVms];

        // Init some variables
        double time =0.0;

        // Computing the completion time matrix for cloudlet-VM
        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getCompletionTime(cloudletList.get(i),vmList.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        for(int c=0; c< cloudletList.size(); c++) {

            //System.out.println(Arrays.deepToString(completionTime));

            // Getting the minimum cloudlet-VM combinations for each cloudlet
            for (int i = 0; i < cloudletList.size(); i++) {
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

            Cloudlet maximumCloudlet = cloudletList.get(maxCloudlet);
            Vm minimumVm = vmList.get(minVm);
            //System.out.println("Maximum Cloudlet: " + maximumCloudlet);
            //System.out.println("Minimum VM: " + minimumVm);

            //maximumCloudlet.setLength(maximumCloudlet.getLength()* (long) minimumVm.getMips());

            // Binding the respetcive cloudlet to the respective VM
            myBroker.bindCloudletToVm(maximumCloudlet, minimumVm);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < cloudletList.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + maximumCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vmList.size(); i++) {
                completionTime[maxCloudlet][i] = -1.0;
            }


            cloudletVmMinList.clear();

            //System.out.println("*********************************");

        }

        for (Cloudlet c : cloudletList
        ) {
            if(c.isBoundToVm()){
                Vm v = c.getVm();
                c.setLength(c.getLength()* (long) v.getMips());
            }
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
