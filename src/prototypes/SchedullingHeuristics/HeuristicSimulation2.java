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
package org.cloudsimplus.examples.SchedullingHeuristics;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class HeuristicSimulation2 {

    private static final int HOSTS = 100;
    private static final int HOST_PES = 1;

    private static final int VMS = 1;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 2000;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100;

    private CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    HeuristicBroker brokerh;
    int heuristicIndex;
    int schedulingHeuristic;
    ArrayList<List<Cloudlet>> heuristicSpecificFinishedCloudletsList = new ArrayList<List<Cloudlet>>();
    ArrayList<Integer> solutionCandidate = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException {
        new HeuristicSimulation2();
    }

    private HeuristicSimulation2() throws IOException, ParseException {

        Log.setLevel(Level.OFF);

        // Generating Initial Population
        GAMetaHeuristic2 mh_ga_2 = new GAMetaHeuristic2();
        ArrayList<ArrayList> solutionCandidatesList = mh_ga_2.createInitialPopulation(5, 4);
        System.out.println("initialPopulation: " + solutionCandidatesList);

        // Identifying and Storing the best solution candidates of each generation
        double generationBestFittestValue;
        ArrayList<Double> generationBestFitnessValuesList = new ArrayList<Double>();
        ArrayList<Integer> generationBestSolutionCandidate = new ArrayList<>();

        for (int generations = 0; generations < 2 ; generations++) {

            ArrayList<Double> solutionCandidatesFitnessList = new ArrayList<>();
            System.out.println("=================================== GENERATION "+generations+" STARTS ==========================================");
            System.out.println("solutionCandidatesList: "+solutionCandidatesList);

            for (int i = 0; i < solutionCandidatesList.size(); i++) {

                heuristicIndex = 1;

                System.out.printf("%n***************** A SOLUTION CANDIDATE STARTS ****************%n");

                simulation = new CloudSim();
                datacenter0 = createDatacenter();
                //broker0 = new DatacenterBrokerSimple(simulation);
                brokerh = new HeuristicBroker(simulation);
                vmList = createVms();
                cloudletList = createCloudlets();

                ArrayList<String[]> workloadEntries = getWorkloadEntries();
                //cloudletList = createKTHWorloadCloudlets(workloadEntries, workloadEntries.size());

                brokerh.submitVmList(vmList);
                brokerh.submitCloudletList(cloudletList);

                //simulation.addOnEventProcessingListener(this::pauseSimulationAtSpecificTime);
                simulation.addOnClockTickListener(this::pauseSimulationAtSpecificTime);
                simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

                solutionCandidate = solutionCandidatesList.get(i);
                System.out.println("Solution Candidate: "+solutionCandidate);

                schedulingHeuristic = solutionCandidate.get(heuristicIndex-1);
                brokerh.selectSchedulingHeuristics(schedulingHeuristic,vmList, cloudletList);

                simulation.start();

                System.out.printf("%n________SOLUTION CANDIDATE SIMULATION RESULTS ________%n");

                // Fitness Objectives
                double makespan = mh_ga_2.getTotalFinishTime(brokerh.getCloudletFinishedList());
                double totalWaitingTime = mh_ga_2.getTotalWaitingTime(brokerh.getCloudletFinishedList());
                double totalExecutionTime = mh_ga_2.getTotalExecutionTime(brokerh.getCloudletFinishedList());
                double totalFlowTime = mh_ga_2.getTotalFlowTime(brokerh.getCloudletFinishedList());
                double avgWaitingTime = mh_ga_2.getAverageWaitingTime(brokerh.getCloudletFinishedList());
                double avgExecutionTime = mh_ga_2.getAverageExecutionTime(brokerh.getCloudletFinishedList());
                double avgFlowTime = mh_ga_2.getAverageFlowTime(brokerh.getCloudletFinishedList());

                System.out.println("Total Cloudlets processed: "+brokerh.getCloudletFinishedList().size());
                System.out.println("Fitness : "+makespan);
                solutionCandidatesFitnessList.add(makespan);
                System.out.println("Solution Candidate Fitness List: "+solutionCandidatesFitnessList);

                System.out.printf("%n________________________________________________________%n");

                System.out.printf("%n***************** A SOLUTION CANDIDATE ENDS ****************%n");

                //List<Cloudlet> finishedCloudlets = brokerh.getCloudletFinishedList();
                //new CloudletsTableBuilder(finishedCloudlets)
                //    .build();
            }

            System.out.println("solutionCandidatesList:" + solutionCandidatesList);
            System.out.println("solutionCandidatesFitnessList: " + solutionCandidatesFitnessList);
            System.out.println("solutionCandidatesListSize: " + solutionCandidatesList.size());
            System.out.println("solutionCandidatesFitnessListSize: " + solutionCandidatesFitnessList.size());

            // getGenerationBestFittestValue
            // getGenerationBestFittestSolutionCandidate

            generationBestFittestValue = mh_ga_2.getGenerationBestFittestValue(solutionCandidatesFitnessList,"min");
            generationBestFitnessValuesList.add(generationBestFittestValue);
            generationBestSolutionCandidate = mh_ga_2.getGenerationBestFittestSolutionCandidate(solutionCandidatesList, solutionCandidatesFitnessList,"min");
            System.out.println("generationBestFitnessValue: "+generationBestFittestValue);
            System.out.println("generationBestFitnessValuesList: "+generationBestFitnessValuesList);
            System.out.println("generationBestSolutionCandidate: "+generationBestSolutionCandidate);

            String flag = "min";
            int eliteCount = 2;
            int tournamentCount = 4;
            double crossoverRate = 0.5;
            double mutationRate = 0.4;

            /*
            double bestFitnessValue = mh_ga_2.generationFitness(fitnessList, flag);
            generationFitness.add(bestFitnessValue);
            System.out.println("Best Fitness Value: "+bestFitnessValue);
            System.out.println("Best Fitness Index: " +fitnessList.indexOf(bestFitnessValue));
            System.out.println("Best Fitness Candidate: "+candidateList.get(fitnessList.indexOf(bestFitnessValue)));
            System.out.println("Generation Fitness: " + generationFitness);
             */

            solutionCandidatesList = mh_ga_2.generationEvolve(solutionCandidatesList,solutionCandidatesFitnessList,flag,eliteCount,tournamentCount, crossoverRate, mutationRate);

            System.out.println("=================================== GENERATION "+generations+" ENDS ==========================================");

        }

    }

    // Creating Data center, Hosts, VMs and Cloudlets

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
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets() {

        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {

            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);

        }

        return list;
    }


    // Pausing and Switching Simulation

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {


        System.out.println("Total Cloudlets processed: "+brokerh.getCloudletFinishedList().size());
        postSimulationHeuristicSpecificFinishedCloudlets(brokerh);

        if (heuristicIndex < 24){

            System.out.println("Heuristic Switched!!!!!");
            schedulingHeuristic = solutionCandidate.get(heuristicIndex);
            brokerh.selectSchedulingHeuristics(schedulingHeuristic,vmList, cloudletList);


        }

        heuristicIndex++;
        simulation.resume();
        System.out.println("Simulation resumed");

    }

    public void pauseSimulationAtSpecificTime(EventInfo eInfo) {

        if (Math.floor(simulation.clock()) == 300 * heuristicIndex) {
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", eInfo.getTime());
        }
    }


    // Simulation Results
    public void postSimulationHeuristicSpecificFinishedCloudlets(HeuristicBroker brokerh){

        List<Cloudlet> allFinishedCloudlets = brokerh.getCloudletFinishedList();
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
        System.out.println("Heuristic Cloudlets processed: "+heuristicSpecificFinishedCloudlets.size());
        //System.out.println("Cloudlets Heuristics processed: "+heuristicSpecificFinishedCloudlets);
        //new CloudletsTableBuilder(heuristicSpecificFinishedCloudlets).build();

    }



    // KTH Workloads

    private List<Cloudlet> createKTHWorloadCloudlets(ArrayList<String[]> workloadEntries, int n) throws ParseException {

        final List<Cloudlet> list = new ArrayList<>(n);

        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < n; i++) {
            long cloudletLength = jobLength(workloadEntries.get(i)[6],workloadEntries.get(i)[4],workloadEntries.get(i)[8]);
            long submitTime = submitTime(workloadEntries.get(i)[3]);
            int reqPEs = Integer.parseInt(workloadEntries.get(i)[8]);
            if (cloudletLength > 0){
                final Cloudlet cloudlet = new CloudletSimple(cloudletLength, reqPEs, utilizationModel);
                cloudlet.setSizes(1024);
                //cloudlet.setSubmissionDelay(submitTime);
                list.add(cloudlet);
            }
        }

        return list;
    }

    private ArrayList<String[]> getWorkloadEntries() throws IOException {

        ArrayList<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        BufferedReader TSVReader = new BufferedReader(new FileReader("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/KTHWorkload/KTH-SP2-1996-0"));
        String line = null;
        while ((line = TSVReader.readLine()) != null) {
            String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
            Data.add(lineItems); //adding the splitted line array to the ArrayList
        }

        ArrayList<String[]> entry = new ArrayList<String[]>();
        String [] st;
        String sp = "";
        for (String[] s: Data) {
            sp = Arrays.toString(s);
            sp = sp.substring(1, sp.length() - 1);
            sp = sp.replaceAll("\\s+",",");
            if (sp.length()>5){
                sp = sp.substring(1, sp.length() - 1);
                st = sp.split(",");
                st[4] = st[4] + " " + st[5];  // combining date and time
                st[6] = st[6] + " " + st[7];  // combining date and time
                if (st[2].length() == 16){
                    st[3] = st[2].substring(8,st[2].length()-2);
                    st[3] = st[3].replaceAll("..(?!$)", "$0:");
                    entry.add(st);
                }
            }
        }

        return entry;

    }

    private long jobLength(String startTime, String endTime, String pe) throws ParseException {

        SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = datetimeFormatter.parse(startTime);
        Date end = datetimeFormatter.parse(endTime);
        long difference =  ((start.getTime()-end.getTime())/1000);
        long len =  (difference/Integer.parseInt(pe));
        return len;

    }

    private long submitTime (String submitTime) throws ParseException {

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        Date time = timeFormatter.parse(submitTime);
        return time.getTime()/1000;

    }


}
