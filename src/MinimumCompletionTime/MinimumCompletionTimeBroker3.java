package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinimumCompletionTimeBroker3 extends DatacenterBrokerSimple {

    double minCompTime=Integer.MAX_VALUE;
    int vm=0;

    public MinimumCompletionTimeBroker3(final CloudSim simulation) {
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

        double time = 0.0;
        int minCloudlet = 0;
        int minVm = 0;

        // Completion Time matrix: Cloudlet-VM
        for (int i = 0; i < noOfCloudlets; i++) {
            for (int j = 0; j < noOfVms; j++) {
                time = getCompletionTime(clist.get(i), vlist.get(j));
                time = Math.round(time * 100.0) / 100.0;
                completionTime[i][j] = time;
                System.out.println("Completion Time Cloudlet" + i + "-VM" + j + " : " + completionTime[i][j]);
            }
        }

        for(int c=0; c< clist.size(); c++) {

            System.out.println("*********************************");

            System.out.println("clist: " + clist);

            System.out.println(Arrays.deepToString(completionTime));

            double MinimumCompletionTime = getMinValue(completionTime);
            System.out.println("Minimum Completion Time: " + getMinValue(completionTime));

            int[] Indices = getIndices(completionTime, MinimumCompletionTime);
            minCloudlet = Indices[0];
            minVm = Indices[1];
            Cloudlet minimumCloudlet = clist.get(minCloudlet);
            Vm minimumVm = vlist.get(minVm);
            System.out.println("Minimum Cloudlet: " + minimumCloudlet);
            System.out.println("Minimum VM: " + minimumVm);

            for (int i = 0; i < clist.size(); i++) {
                if (completionTime[i][minVm] != -1) {
                    completionTime[i][minVm] = completionTime[i][minVm] + MinimumCompletionTime;
                    completionTime[i][minVm] = Math.round(completionTime[i][minVm] * 100.0) / 100.0;
                }
            }


            for (int i = 0; i < vlist.size(); i++) {
                completionTime[minCloudlet][i] = -1.0;
            }

            System.out.println(Arrays.deepToString(completionTime));

        }

    }



    private double getCompletionTime(Cloudlet cloudlet, Vm vm){

        double waitingTime = cloudlet.getWaitingTime();
        double execTime = cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
        double completionTime = execTime + waitingTime;
        return completionTime;

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
