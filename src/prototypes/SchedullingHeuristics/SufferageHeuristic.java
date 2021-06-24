package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.examples.SufferageBroker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SufferageHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker brokerh;

    SufferageHeuristic(HeuristicBroker brokerh,  List<Vm> vmList) {
        this.brokerh = brokerh;
        this.vmList = vmList;
        this.cloudletList = brokerh.getCloudletSubmittedList();

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

    public void sufferageScheduling() {

        cloudletList.removeAll(brokerh.getCloudletFinishedList());

        System.out.println("No. of Cloudlets: "+cloudletList.size());
        System.out.println("First Cloudlet: "+cloudletList.get(0).getId());

        // Rearranging the remaining cloudlets and deassigning their respective VM.
        Collections.sort(cloudletList);
        //for (Cloudlet c : cloudletList) {
        //    if (c.isBoundToVm() == true){
        //        c.setVm(Vm.NULL);}
        //}

        // Remaining cloudlets
        System.out.println("Cloudlets: "+cloudletList);

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();

        // Completion time and execution matrix for cloudlet-VM
        double completionTime[][] = new double[noOfCloudlets][noOfVms];
        double time = 0.0;

        // Storing the cloudlets and VMs
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

        // Computing the completion time matrix for cloudlet-VM
        for (int i = 0; i < noOfCloudlets; i++) {
            for (int j = 0; j < noOfVms; j++) {
                time = getCompletionTime(clist.get(i), vlist.get(j));
                time = Math.round(time * 100.0) / 100.0;
                completionTime[i][j] = time;
                //System.out.println("Completion Time Cloudlet" + i + "-VM" + j + " : " + completionTime[i][j]);
            }
        }

        for(int c=0; c< clist.size(); c++) {

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
            Cloudlet maximumsufferageCloudlet = clist.get(maxsufferageCloudlet);
            Vm minimumVm = vlist.get(minVm);

            // Binding the respective cloudlet to the respective VM
            brokerh.bindCloudletToVm(maximumsufferageCloudlet, minimumVm);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < clist.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + respectiveCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vlist.size(); i++) {
                completionTime[maxsufferageCloudlet][i] = -1.0;
            }

            // clearing the cloudletSufferageList
            cloudletSufferageList.clear();

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
