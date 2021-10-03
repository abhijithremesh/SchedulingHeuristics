/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.MyHeuristics;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.HybridApproach.GeneticAlgorithm;
import org.cloudsimplus.examples.HybridApproach.HybridHeuristicBroker;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A minimal but organized, structured and re-usable CloudSim Plus example
 * which shows good coding practices for creating simulation scenarios.
 *
 * <p>It defines a set of constants that enables a developer
 * to change the number of Hosts, VMs and Cloudlets to create
 * and the number of {@link Pe}s for Hosts, VMs and Cloudlets.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class MyInfrastructureZ {

    private static final double INTERVAL = 3600;

    private static final int HOSTS = 4;
    private static final int HOST_PES = 256;
    private static final int HOST_RAM = 20000;
    private static final int HOST_SIZE = 1000000;
    private static final int HOST_BW = 10000;

    private static final int VMS = Integer.numberOfTrailingZeros(HOST_PES) * HOSTS;
    private static int VM_PES;
    private static int VM_RAM = 512;
    private static int VM_BW = 1000;
    private static final int VM_SIZE = 10000;

    private static int VM_MIPS = 1000;

    private static final int CLOUDLETS = 100;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;
    private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";    // 28476
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    //private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239

    private CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int heuristicIndex = 1;
    int schedulingHeuristic;
    MyBroker myBroker;

    ArrayList<Integer> solutionCandidate = new ArrayList<>();
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();

    public static void main(String[] args) {
        new MyInfrastructureZ();
    }

    private MyInfrastructureZ() {

        Log.setLevel(Level.OFF);

        // Generating Initial Population
        GeneticAlgorithm mh_ga_2 = new GeneticAlgorithm();
        ArrayList<ArrayList> solutionCandidatesList = mh_ga_2.createInitialPopulation(1, 11);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        // Identifying and Storing the best solution candidates of each generation
        double generationAvgFittestValue;
        double generationBestFittestValue;
        ArrayList<Double> generationAvgFitnessValuesList = new ArrayList<Double>();
        ArrayList<Double> generationBestFitnessValuesList = new ArrayList<Double>();
        ArrayList<Integer> generationBestSolutionCandidate = new ArrayList<>();

        for (int generations = 0; generations < 1; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();

            System.out.printf("%n=================================== GENERATION "+generations+" STARTS ==========================================%n");

            System.out.printf("%nsolutionCandidatesList: "+solutionCandidatesList+"%n%n");

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                heuristicIndex = 1;

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" STARTS ****************%n");

                simulation = new CloudSim();
                datacenter0 = createDatacenter();
                datacenter0.setSchedulingInterval(0.5);

                //broker0 = new DatacenterBrokerSimple(simulation);
                myBroker = new MyBroker(simulation);

                //cloudletList = createCloudlets();
                cloudletList = createCloudletsFromWorkloadFile(CLOUDLETS);
                modifySubmissionTimes();

                vmList = createVms();

                myBroker.submitVmList(vmList);
                myBroker.submitCloudletList(cloudletList);

                simulation.addOnClockTickListener(this::pauseSimulation);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

                solutionCandidate = solutionCandidatesList.get(i);
                System.out.printf("%nSolution Candidate: "+solutionCandidate+"%n%n");

                schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
                System.out.println("Heuristic Switched to "+schedulingHeuristic);
                myBroker.selectSchedulingPolicy(schedulingHeuristic,vmList);


                simulation.start();

                /*
                -makespan
                -totalResponseTime
                -avgResponseTime
                -totalWaitingTime
                -avgWaitingTime
                -totalExecutionTime
                -avgExecutionTime
                -SlowdownRatio
                -totalVmRunTime
                -processorUtilization
                 */

                double fitness = evaluatePerformanceMetrics("avgResponseTime");

                System.out.println("Simulation Time: " + simulation.clock());
                System.out.println("Total cloudlets processed: " + myBroker.getCloudletFinishedList().size());
                //System.out.println("Total cloudlets processed: " + broker0.getCloudletFinishedList().size());

                solutionCandidatesFitnessList.add(fitness);
                System.out.println("Solution Candidate Fitness List: "+solutionCandidatesFitnessList);

                System.out.println("Total cloudlets processed: "+myBroker.getCloudletFinishedList().size());
                System.out.println("Any cloudlets waiting: "+myBroker.getCloudletWaitingList().size());

                System.out.printf("%n***************** SOLUTION CANDIDATE "+i+" ENDS ****************%n");


            }


            System.out.println("solutionCandidatesList:" + solutionCandidatesList);
            System.out.println("solutionCandidatesFitnessList: " + solutionCandidatesFitnessList);
            System.out.println("solutionCandidatesListSize: " + solutionCandidatesList.size());
            System.out.println("solutionCandidatesFitnessListSize: " + solutionCandidatesFitnessList.size());


/*
            generationAvgFittestValue = mh_ga_2.getGenerationAvgFittestValue(solutionCandidatesFitnessList);
            generationAvgFitnessValuesList.add(generationAvgFittestValue);
            generationBestFittestValue = mh_ga_2.getGenerationBestFittestValue(solutionCandidatesFitnessList,"min");
            generationBestFitnessValuesList.add(generationBestFittestValue);
            //generationBestSolutionCandidate = mh_ga_2.getGenerationBestFittestSolutionCandidate(solutionCandidatesList, solutionCandidatesFitnessList,"min");
            System.out.println("generationAvgFitnessValue: "+generationAvgFittestValue);
            System.out.println("generationAvgFitnessValuesList: "+generationAvgFitnessValuesList);
            System.out.println("generationBestFittestValue: "+generationBestFittestValue);
            System.out.println("generationBestFitnessValuesList: "+generationBestFitnessValuesList);
            //System.out.println("generationBestSolutionCandidate: "+generationBestSolutionCandidate);



            System.out.println("=================================== GENERATION "+generations+" ENDS ==========================================");

            String flag = "min";
            int eliteCount = 3;
            int tournamentCount = 4;
            double crossoverRate = 0.5;
            double mutationRate = 0.4;

            solutionCandidatesList = mh_ga_2.generationEvolve(solutionCandidatesList,solutionCandidatesFitnessList,flag,eliteCount,tournamentCount, crossoverRate, mutationRate);

            System.out.println("=================================== GENERATION "+generations+" EVOLVED ==========================================");





 */




        }

    }

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {

        if (heuristicIndex < 24){
            heuristicIndex ++;
        }
        else if (heuristicIndex >= 24 ){
            heuristicIndex = 1;
        }

        schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
        System.out.println("Heuristic Switched to "+schedulingHeuristic);
        myBroker.selectSchedulingPolicy(schedulingHeuristic, vmList);

        simulation.resume();
        System.out.println("simulation resumed...");
    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            postSimulationHeuristicSpecificFinishedCloudlets(myBroker);
            System.out.printf("Total Cloudlets processed: "+myBroker.getCloudletFinishedList().size()+"%n");
            cloudletList.removeAll(myBroker.getCloudletFinishedList());
            System.out.printf("Remaining Cloudlets: "+cloudletList.size()+"%n%n");
        }
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_SIZE, peList);
        h.setVmScheduler(new VmSchedulerSpaceShared());
        return h;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            long pes = HOST_PES / (long) (Math.pow(2, i / HOSTS + 1));
            VM_PES = (int)pes;
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1500);
            cloudlet.setSubmissionDelay(0);
            list.add(cloudlet);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile(int count) {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, VM_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        //cloudletList.forEach(c->c.setLength(c.getLength()*c.getNumberOfPes()));
        List<Cloudlet> list = new ArrayList<>();
        for (int i=0; i < count; i++){
            list.add(cloudletList.get(i));
        }
        cloudletList = list;
        return cloudletList;
    }

    private void modifySubmissionTimes() {
        double minSubdelay = cloudletList.get(0).getSubmissionDelay();
        for (Cloudlet c : cloudletList
        ) {
            c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
            //c.setSubmissionDelay(0);
        }
    }

    private void showCloudletsDetails(){
        for (Cloudlet c: cloudletList
        ) {
            System.out.println("ID: "+c.getId()+" Length: "+c.getLength()+" PEs: "+c.getNumberOfPes()+" Submission delay: "+c.getSubmissionDelay()+" Waiting Time: "+c.getWaitingTime()+" Start time: "+c.getExecStartTime()+" CPU time: "+c.getActualCpuTime()+" Finish time: "+c.getFinishTime());
        }
    }

    private double evaluatePerformanceMetrics(String metric) {

        double metricValue = 0;
        double makespan = myBroker.getCloudletFinishedList().get(myBroker.getCloudletFinishedList().size() - 1).getFinishTime();

        double totalResponseTime = 0.0;
        double totalWaitingTime = 0.0;
        double totalExecutionTime = 0.0;
        for (Cloudlet c : myBroker.getCloudletFinishedList()
        ) {

            totalResponseTime = totalResponseTime + (c.getSubmissionDelay() + c.getWaitingTime() + c.getActualCpuTime());
            totalWaitingTime = totalWaitingTime + c.getWaitingTime();
            totalExecutionTime = totalExecutionTime + c.getActualCpuTime();

        }

        double totalVmRunTime = 0.0;
        for (Vm v : vmList
        ) {
            totalVmRunTime = totalVmRunTime + v.getTotalExecutionTime();
        }

        if (metric == "makespan") {
            metricValue = makespan;
            System.out.println("makespan: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "totalResponseTime") {
            metricValue = totalResponseTime;
            System.out.println("totalResponseTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgResponseTime") {
            metricValue = totalResponseTime / cloudletList.size();
            System.out.println("avgResponseTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalWaitingTime") {
            metricValue = totalWaitingTime;
            System.out.println("totalWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100));
        } else if (metric == "avgWaitingTime") {
            metricValue = totalWaitingTime / cloudletList.size();
            System.out.println("avgWaitingTime: " + ((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "totalExecutionTime"){
            metricValue = totalExecutionTime;
            System.out.println("Total Execution Time: "+((double)Math.round(metricValue *  100.0)/100) );
        } else if (metric == "avgExecutionTime"){
            metricValue = totalExecutionTime/cloudletList.size();
            System.out.println("avgExecutionTime: "+ ((double)Math.round(metricValue *  100.0)/100)  );
        } else if (metric == "totalVmRunTime"){
            metricValue = totalVmRunTime;
            System.out.println("totalVmRunTime: "+totalVmRunTime);
        } else if (metric == "SlowdownRatio") {
            metricValue = (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size());
            System.out.println("SlowdownRatio: " +((double)Math.round(metricValue *  100.0)/100)  );
        } else if(metric == "processorUtilization"){
            metricValue = totalVmRunTime/simulation.getLastCloudletProcessingUpdate();
            System.out.println("processorUtilization: "+((double)Math.round(metricValue *  100.0)/100));
        }

        return ((double)Math.round(metricValue *  100.0)/100);

    }

    public void postSimulationHeuristicSpecificFinishedCloudlets(MyBroker myBroker){

        List<Cloudlet> allFinishedCloudlets = myBroker.getCloudletFinishedList();
        heuristicSpecificFinishedCloudletsList.add(allFinishedCloudlets);
        int items = heuristicSpecificFinishedCloudletsList.size();
        List<Cloudlet> heuristicSpecificFinishedCloudlets = new ArrayList<Cloudlet>();
        //if (brokerh.getCloudletSubmittedList().size() > brokerh.getCloudletFinishedList().size()) {
        if (items == 1) {
            heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(0);
        } else if (items > 1) {
            List<Cloudlet> lastItem = heuristicSpecificFinishedCloudletsList.get(items - 1);
            List<Cloudlet> secondLastItem = heuristicSpecificFinishedCloudletsList.get(items - 2);
            List<Cloudlet> differences = new ArrayList<>(lastItem);
            differences.removeAll(secondLastItem);
            //heuristicSpecificFinishedCloudletsList.get(items - 1).removeAll(heuristicSpecificFinishedCloudletsList.get(items - 2));
            //heuristicSpecificFinishedCloudlets = heuristicSpecificFinishedCloudletsList.get(items - 1);
            heuristicSpecificFinishedCloudlets = differences;
        }
        //}

        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();
        new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();
        System.out.printf("Heuristic Cloudlets processed: "+heuristicSpecificFinishedCloudlets.size()+"%n");
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }






}
