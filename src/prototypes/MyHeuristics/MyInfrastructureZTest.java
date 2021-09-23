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
public class MyInfrastructureZTest {

    private static final double INTERVAL = 3600;

    private static final int HOSTS = 1;
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

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    int heuristicIndex = 1;
    int schedulingHeuristic = 13;
    MyBroker myBroker;

    public static void main(String[] args) {
        new MyInfrastructureZTest();
    }

    private MyInfrastructureZTest() {

        Log.setLevel(Level.OFF);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        datacenter0.setSchedulingInterval(0.5);

        //broker0 = new DatacenterBrokerSimple(simulation);
        myBroker = new MyBroker(simulation);

        vmList = createVms();
        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile(CLOUDLETS);
        modifySubmissionTimes();

        myBroker.submitVmList(vmList);
        myBroker.submitCloudletList(cloudletList);

        simulation.addOnClockTickListener(this::pauseSimulation);
        simulation.addOnSimulationPauseListener(this::switchSchedulingHeuristics);

        myBroker.selectSchedulingPolicy(schedulingHeuristic, vmList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = myBroker.getCloudletFinishedList();

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

        double makespan = evaluatePerformanceMetrics("makespan");
        double totalResponseTime = evaluatePerformanceMetrics("totalResponseTime");
        double avgResponseTime = evaluatePerformanceMetrics("avgResponseTime");
        double totalWaitingTime = evaluatePerformanceMetrics("totalWaitingTime");
        double avgWaitingTime = evaluatePerformanceMetrics("avgWaitingTime");
        double totalExecutionTime = evaluatePerformanceMetrics("totalExecutionTime");
        double avgExecutionTime = evaluatePerformanceMetrics("avgExecutionTime");
        double SlowdownRatio = evaluatePerformanceMetrics("SlowdownRatio");
        double totalVmRunTime = evaluatePerformanceMetrics("totalVmRunTime");
        double processorUtilization = evaluatePerformanceMetrics("processorUtilization");


        System.out.println("Total Cloudlets: "+myBroker.getCloudletSubmittedList().size());
        System.out.println("Cloudlets processed: "+myBroker.getCloudletFinishedList().size());
        System.out.println("Cloudlets waiting: "+myBroker.getCloudletWaitingList().size());


    }

    public void switchSchedulingHeuristics(EventInfo pauseInfo) {
        simulation.resume();
        heuristicIndex++;
        System.out.println("Heuristics switched....");
        System.out.println("simulation resumed...");
    }

    private void pauseSimulation( EventInfo evt) {
        if((int)evt.getTime() == INTERVAL * heuristicIndex){
            simulation.pause();
            System.out.printf("%n# Simulation paused at %.2f second%n%n", Math.floor(simulation.clock()));
            System.out.println("Cloudlets processed: "+myBroker.getCloudletFinishedList().size());
            myBroker.getCloudletSubmittedList().removeAll(myBroker.getCloudletFinishedList());
            System.out.println("Cloudlets remaining: "+myBroker.getCloudletSubmittedList().size());
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
        h.setVmScheduler(new VmSchedulerTimeShared());
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

            totalResponseTime = totalResponseTime + (c.getActualCpuTime() + c.getWaitingTime() +c.getSubmissionDelay());
            totalWaitingTime = totalWaitingTime + c.getWaitingTime();
            totalExecutionTime = totalExecutionTime + c.getActualCpuTime();

        }

        double totalVmRunTime = 0.0;
        for (Vm v : vmList
        ) {
            totalVmRunTime = totalVmRunTime + v.getTotalExecutionTime();
        }

        if (metric == "makespan") {
            System.out.println("makespan: " + makespan);
            metricValue = makespan;
        } else if (metric == "totalResponseTime") {
            System.out.println("totalResponseTime: " + totalResponseTime);
            metricValue = totalResponseTime;
        } else if (metric == "avgResponseTime") {
            System.out.println("avgResponseTime: " + totalResponseTime / cloudletList.size());
            metricValue = totalResponseTime / cloudletList.size();
        } else if (metric == "totalWaitingTime") {
            System.out.println("totalWaitingTime: " + totalWaitingTime);
            metricValue = totalWaitingTime;
        } else if (metric == "avgWaitingTime") {
            System.out.println("avgWaitingTime: " + totalWaitingTime / cloudletList.size());
            metricValue = totalWaitingTime / cloudletList.size();
        } else if (metric == "totalExecutionTime"){
            System.out.println("Total Execution Time: "+totalExecutionTime);
            metricValue = totalExecutionTime;
        } else if (metric == "avgExecutionTime"){
            System.out.println("avgExecutionTime: "+totalExecutionTime/cloudletList.size());
            metricValue = totalExecutionTime/cloudletList.size();
        } else if (metric == "totalVmRunTime"){
            System.out.println("totalVmRunTime: "+totalVmRunTime);
            metricValue = totalVmRunTime;
        } else if (metric == "SlowdownRatio") {
            System.out.println("SlowdownRatio: " + (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size()));
            metricValue = (totalResponseTime / cloudletList.size()) / (totalExecutionTime / cloudletList.size());
        } else if(metric == "processorUtilization"){
            System.out.println("processorUtilization: "+totalVmRunTime/simulation.getLastCloudletProcessingUpdate());
            metricValue = totalVmRunTime/simulation.getLastCloudletProcessingUpdate();
        }

        return metricValue;

    }





}
