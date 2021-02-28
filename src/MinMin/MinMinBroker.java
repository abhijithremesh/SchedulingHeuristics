package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinMinBroker extends DatacenterBrokerSimple {

    public MinMinBroker(final CloudSim simulation) {
        super(simulation);
    }


    public void scheduleTasksToVms(List<Vm> vmList, List<Cloudlet> cloudletList ){

        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();


        System.out.println("noOfVms: "+noOfVms);
        System.out.println("noOfCloudlets: "+noOfCloudlets);

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
                //time=getExecutionTime(clist.get(i),vlist.get(j));
                //time = Math.round(time*100)/100;
                //executionTime[i][j]=time;
                //System.out.println("Execution Time "+i+"-"+j+":"+execTime[i][j]);
                System.out.println("Completion Time Cloudlet"+i+"-VM"+j+" : "+completionTime[i][j]);
                //System.out.println("Execution Time Cloudlet"+i+"-VM"+j+" : "+executionTime[i][j]);
            }
        }

        System.out.println("                     ");

        System.out.println(Arrays.deepToString(completionTime));
        System.out.println(completionTime.length);

        System.out.println("                     ");


        int minCloudlet = 0;
        int minVm=0;
        double min=1.0d;

        for(int c=0; c< clist.size(); c++){


            for(int i=0;i<clist.size();i++){
                for(int j=0;j<(vlist.size()-1);j++){
                    if(completionTime[i][j+1] > completionTime[i][j] && completionTime[i][j+1] > 0.0){
                        minCloudlet=i;
                    }
                }
            }


            for(int j=0; j<vlist.size(); j++){
                time = getCompletionTime(clist.get(minCloudlet), vlist.get(j));
                time = Math.round(time*100.0)/100.0;
                if(j==0){
                    min=time;
                }
                if(time < min && time > -1.0){
                    minVm=j;
                    min=time;
                }

            }



            //System.out.println("MinCloudlet: "+clist.get(minCloudlet));
            //System.out.println("MinVM: "+vlist.get(minVm));

            Cloudlet minimumCloudlet =  clist.get(minCloudlet);
            Vm minimumVm = vlist.get(minVm);

            System.out.println("MIN-MIN candidate is cloudlet: "+minimumCloudlet.getId()+",VM: "+minimumVm.getId());
            System.out.println("Completion Time: "+getCompletionTime(minimumCloudlet,minimumVm));

            bindCloudletToVm(minimumCloudlet, minimumVm);
            clist.remove(minCloudlet);

            for(int i=0; i<vlist.size(); i++){
                completionTime[minCloudlet][i]=-1.0;
            }

            System.out.println(Arrays.deepToString(completionTime));

            System.out.println("The number of cloudlets "+clist.size());
            System.out.println("-------------------------------------------------");

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






}
