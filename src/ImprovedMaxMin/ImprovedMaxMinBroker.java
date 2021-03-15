package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImprovedMaxMinBroker extends DatacenterBrokerSimple {

    public ImprovedMaxMinBroker(final CloudSim simulation) {
        super(simulation);
    }

    ArrayList<cloudletVmMin> cloudletVmMinimumList = new ArrayList<cloudletVmMin>();

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

    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ) {

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();


        System.out.println("noOfVms: " + noOfVms);
        System.out.println("noOfCloudlets: " + noOfCloudlets);

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

        double completionTime[][] = new double[noOfCloudlets][noOfVms];
        double executionTime[][] = new double[noOfCloudlets][noOfVms];

        double time =0.0;

        for(int i=0;i<noOfCloudlets;i++){
            for(int j=0;j<noOfVms;j++){
                time=getCompletionTime(clist.get(i),vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                completionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
            }
        }

        System.out.println("*********************************");

        for(int c=0; c< clist.size(); c++) {

            System.out.println(Arrays.deepToString(completionTime));

            for (int i = 0; i < clist.size(); i++) {
                getMinCompletionTimePerCloudlet(completionTime, i);
            }

            int[] Indices = getMaxCompletionTimeAllCloudlet(cloudletVmMinimumList);
            int maxCloudlet = Indices[0];
            int minVm = Indices[1];
            double maximumCompletionTime = completionTime[maxCloudlet][minVm];

            System.out.println("maxCloudlet: " + maxCloudlet);
            System.out.println("minVm: " + minVm);

            Cloudlet maximumCloudlet = clist.get(maxCloudlet);
            Vm minimumVm = vlist.get(minVm);
            System.out.println("Maximum Cloudlet: " + maximumCloudlet);
            System.out.println("Minimum VM: " + minimumVm);

            bindCloudletToVm(maximumCloudlet, minimumVm);

            for (int i = 0; i < clist.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + maximumCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }


            for (int i = 0; i < vlist.size(); i++) {
                completionTime[maxCloudlet][i] = -1.0;
            }


            cloudletVmMinimumList.clear();

            System.out.println("*********************************");

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
        System.out.println("Minimum Completion Time for Cloudlet "+c+" : " + minValue+" on VM "+vm);
        cloudletVmMinimumList.add(new cloudletVmMin(c,vm,minValue));

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
        System.out.println("maximumCompletionTimeAllCloudlets: "+maximumCompletionTimeAllCloudlets);
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
