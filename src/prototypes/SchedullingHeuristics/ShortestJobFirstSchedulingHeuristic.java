package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShortestJobFirstSchedulingHeuristic {

    List<Cloudlet> cloudletList;
    List<Vm> vmList;
    HeuristicBroker broker0;

    ShortestJobFirstSchedulingHeuristic(HeuristicBroker broker0, List<Cloudlet> cloudletList, List<Vm> vmList) {
        this.cloudletList = cloudletList;
        this.vmList = vmList;
        this.broker0 = broker0;

    }

    public void shortestJobFirstScheduling1 (){



        cloudletList.sort((Cloudlet c1,Cloudlet c2) -> {
            if (c1.getLength() > c2.getLength())
                return 1;
            if (c1.getLength() < c2.getLength())
                return -1;
            return 0;
        });

        //for (Cloudlet c : cloudletList) {
        //    System.out.println(c.getLength());
        //}


        //broker0.submitVmList(vmList);
        //broker0.submitCloudletList(cloudletList);

        System.out.println(cloudletList.get(0).getId());

        // Binding the sorted cloudlets to VMs as per the FCFS policy
        for(int i=0; i < cloudletList.size(); i++){
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            broker0.bindCloudletToVm(cl,vm);
            //System.out.println("Cloudlet "+cl.getId()+" of length "+cl.getLength()+" is bound with VM "+vm.getId());
        }

        //broker0.submitCloudletList(cloudletList);
        //broker0.submitVmList(vmList);



    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Init ArrayList for storing cloudlet specifications and sorted cloudlets
    ArrayList<cloudletSpec> cloudletSpecList = new ArrayList<cloudletSpec>();
    ArrayList<Cloudlet> sortedCloudletList = new ArrayList<Cloudlet>();

    // Init random to randomly choose a VM as the execution time for cloudlet in any VM appears to be same
    Random random = new Random();

    // CloudletSpec class which stores the cloudlet,it's execution time on any VM and it's respective length
    public class cloudletSpec{

        private Cloudlet cloudlet;
        private double executionTime;
        private long length;

        public  cloudletSpec(Cloudlet cloudlet, double executionTime,long length){
            this.cloudlet = cloudlet;
            this.executionTime = executionTime;
            this.length = length;
        }

    }

    public void shortestJobFirstScheduling2() {

        // Getting the amount of cloudlets and VMs
        int noOfVms = vmList.size();
        int noOfCloudlets = cloudletList.size();

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

        double time = 0;
        double executionTime[][] = new double[noOfCloudlets][noOfVms];

        // Computing the execution time matrix for cloudlet-VM
        for(int i=0; i<noOfCloudlets; i++){

            int v = random.nextInt(vlist.size());
            time=getExecutionTime(clist.get(i),vlist.get(v));
            time = Math.round(time*100.0)/100.0;
            executionTime[i][v] = time;
            //System.out.println("Execution Time Cloudlet"+i+"-VM"+j+" : " +executionTime[i][j]);
            cloudletSpecList.add(new cloudletSpec(clist.get(i),executionTime[i][v],clist.get(i).getLength()));

        }

        // Cloudlets before sorting in ascending order with respect to execution time
        for (cloudletSpec c : cloudletSpecList)
        {
            System.out.println(" Cloudlet "+c.cloudlet.getId()+" has execution time: "+c.executionTime+" with length "+c.length);
        }

        System.out.println("*************************************************************************");

        // Sorting Cloudlets in ascending order based on their execution time
        cloudletSpecList.sort((cloudletSpec c1, cloudletSpec c2) -> {
            if (c1.executionTime > c2.executionTime)
                return 1;
            if (c1.executionTime < c2.executionTime)
                return -1;
            return 0;
        });

        // Cloudlets after sorting in ascending order with respect to execution time
        for (cloudletSpec c : cloudletSpecList)
        {
            System.out.println(" Cloudlet "+c.cloudlet.getId()+" has execution time: "+c.executionTime+" with length "+c.length);
            sortedCloudletList.add(c.cloudlet);
        }

        System.out.println("************** sortedCloudletList ******************************");

        // Creating a sortedCloudletList to store just the sorted cloudlets
        for (Cloudlet c : sortedCloudletList)
        {
            System.out.println(" Cloudlet "+c.getId()+" with length "+c.getLength());
        }

        broker0.submitCloudletList(sortedCloudletList);
        broker0.submitVmList(vmList);

        // Binding the sorted cloudlets to VMs as per the FCFS policy
        for(int i=0; i < sortedCloudletList.size(); i++){
            Cloudlet cl = sortedCloudletList.get(i);
            Vm vm = vmList.get((i % vmList.size()));
            broker0.bindCloudletToVm(cl,vm);
            System.out.println("Cloudlet "+cl.getId()+" of length "+cl.getLength()+" is bound with VM "+vm.getId());
        }

    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm){
        return cloudlet.getLength() / (vm.getMips()*vm.getNumberOfPes());
    }


}
