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

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();
        System.out.println("noOfVms: " + noOfVms);
        System.out.println("noOfCloudlets: " + noOfCloudlets);

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


        System.out.println("*********************************");

        for(int c=0; c< clist.size(); c++) {

            System.out.println("*********************************");
            System.out.println(Arrays.deepToString(executionTime));

            // Getting the maximum execution time value from the execution time matrix
            double MaximumExecutionTime = getMaxValue(executionTime);
            System.out.println("Maximum Execution Time: " + MaximumExecutionTime);

            // Getting the respective indices (cloudlet,VM) of the maximum execution time value
            int[] Indices = getIndices(executionTime, MaximumExecutionTime);

            // Getting the cloudlet-VM  with maximum execution time from the execution time matrix
            maxCloudlet = Indices[0];
            vm = Indices[1];
            System.out.println("Max Cloudlet : " + maxCloudlet);
            System.out.println("VM : " + vm);

            // Initializing MinExecTime value as the highest possible integer value
            double minCompTime = Integer.MAX_VALUE;

            // Finding the VM which gives minimum execution time for this selected cloudlet
            for (int j = 0; j < vlist.size(); j++) {
                if (completionTime[maxCloudlet][j] < minCompTime) {
                    minCompTime = completionTime[maxCloudlet][j];
                    vm = j;
                }
            }

            // The corresponding completion time for the selected cloudlet and VM
            double finalExecutionTime = completionTime[maxCloudlet][vm];

            // Binding the cloudlet to the respective VM
            bindCloudletToVm(clist.get(maxCloudlet), vlist.get(vm));
            System.out.println(clist.get(maxCloudlet) + " is bound to " + vlist.get(vm) + " at MET: " + minCompTime);

            // Updating the completion time values for the selected VM and other remaining cloudlets
            for (int i = 0; i < clist.size(); i++) {
                if (executionTime[i][vm] != -1) {
                    executionTime[i][vm] = executionTime[i][vm] + finalExecutionTime;
                    executionTime[i][vm] = Math.round(executionTime[i][vm] * 100.0) / 100.0;
                }
            }

            // Replacing the completion times of the selected cloudlet across all the VMs with -1
            for (int i = 0; i < vlist.size(); i++) {
                executionTime[maxCloudlet][i] = -1.0;
            }


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
