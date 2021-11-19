package org.cloudsimplus.examples.HybridStrategy;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SufferageHeuristic {

    MyHeuristicBroker myBroker;
    List<Vm> vmList;

    SufferageHeuristic (MyHeuristicBroker myBroker, List<Vm> vmList){

        this.myBroker = myBroker;
        this.vmList = vmList;

    }

    ArrayList<cloudletSufferage> cloudletSufferageList = new ArrayList<cloudletSufferage>();

    public class cloudletSufferage{
        private int cloudlet;
        private double sufferage;

        public  cloudletSufferage(int cloudlet, double sufferage){
            this.cloudlet = cloudlet;
            this.sufferage = sufferage;
        }

    }

    public void schedule() {

        System.out.println("Scheduling with Sufferage Policy");

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

        // Completion time and execution matrix for cloudlet-VM
        double completionTime[][] = new double[noOfCloudlets][noOfVms];
        double time = 0.0;

        // Computing the completion time matrix for cloudlet-VM
        for (int i = 0; i < noOfCloudlets; i++) {
            for (int j = 0; j < noOfVms; j++) {
                time = getCompletionTime(cloudletList.get(i), vmList.get(j));
                time = Math.round(time * 100.0) / 100.0;
                completionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet" + i + "-VM" + j + " : " + completionTime[i][j]);
            }
        }

        for(int c=0; c< cloudletList.size(); c++) {

            int maxsufferageCloudlet = 0;
            int minVm = 0;
            double minCompTime=Integer.MAX_VALUE;

            //System.out.println(Arrays.deepToString(completionTime));

            // Getting the sufferage for each cloudlet
            cloudletSufferageList = getSufferage(noOfCloudlets, noOfVms, completionTime);

            // Getting the cloudlet with maximum sufferage
            maxsufferageCloudlet = getCloudletMaxSufferage(cloudletSufferageList);

            //System.out.println("maxsufferageCloudlet = " + maxsufferageCloudlet);

            // Getting the VM which execute the above cloudlet in minimum completion time
            for (int j = 0; j < noOfVms; j++) {
                if (completionTime[maxsufferageCloudlet][j] < minCompTime) {
                    minCompTime = completionTime[maxsufferageCloudlet][j];
                    minVm = j;
                }
            }

            //System.out.println("minimumVM = " + minVm);

            // Computing the respective completion time for the selected cloudlet-VM combo.
            double respectiveCompletionTime = completionTime[maxsufferageCloudlet][minVm];
            //System.out.println("respectiveCompletionTime = " + respectiveCompletionTime);

            // Getting the respective cloudlet and VM
            Cloudlet maximumsufferageCloudlet = cloudletList.get(maxsufferageCloudlet);
            Vm minimumVm = vmList.get(minVm);




            // Binding the respective cloudlet to the respective VM
            myBroker.bindCloudletToVm(maximumsufferageCloudlet, minimumVm);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < cloudletList.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + respectiveCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vmList.size(); i++) {
                completionTime[maxsufferageCloudlet][i] = -1.0;
            }

            // clearing the cloudletSufferageList
            cloudletSufferageList.clear();

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

    // get sufferage of all cloudlets
    private ArrayList getSufferage(int noOfCloudlets, int noOfVms, double[][] completionTime ){

        for(int i=0; i < noOfCloudlets; i++) {
            double minCompTime=Integer.MAX_VALUE;
            double minCompTime2=Integer.MAX_VALUE;
            double sufferage = 0;
            for (int j = 0; j < noOfVms; j++) {
                if (completionTime[i][j] < minCompTime && completionTime[i][j] != -1) {
                    minCompTime2 = minCompTime;
                    minCompTime = completionTime[i][j];

                } else if (completionTime[i][j] < minCompTime2 && completionTime[i][j] != minCompTime && completionTime[i][j] != -1) {
                    minCompTime2 = completionTime[i][j];
                }
            }

            sufferage = Math.abs(minCompTime2-minCompTime);
            sufferage = Math.round(sufferage * 100.0) / 100.0;;

            //System.out.println("Cloudlet "+i+" - minimum : "+minCompTime);
            //System.out.println("Cloudlet "+i+" - minimum2 : "+minCompTime2);
            //System.out.println("Cloudlet "+i+" - sufferage : "+sufferage);

            cloudletSufferageList.add(new cloudletSufferage(i,sufferage));

        }
        return cloudletSufferageList;
    }

    //get cloudlet with maximu sufferage
    private Integer getCloudletMaxSufferage(ArrayList<cloudletSufferage> List){

        int maxsufferageCloudlet =0;
        double maximumSufferage = cloudletSufferageList.get(0).sufferage;
        for (int i=0;i<cloudletSufferageList.size();i++){
            if (cloudletSufferageList.get(i).sufferage > maximumSufferage ){
                maximumSufferage = cloudletSufferageList.get(i).sufferage;
                maxsufferageCloudlet = i;
            }
        }
        //System.out.println("maximumSufferage: "+maximumSufferage);
        return maxsufferageCloudlet;

    }




}
