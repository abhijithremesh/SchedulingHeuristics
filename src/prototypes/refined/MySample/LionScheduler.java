/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.examples.MySample;

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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.HybridStrategy.MyHeuristicBroker;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class LionScheduler {

    private static final int  HOSTS = 2;
    private static final int  HOST_PES = 2;
    private static final int  HOST_MIPS = 1000;
    private static final int  HOST_RAM = 20_000; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes

    private static final int VMS = 20;
    private static final int VM_PES = 2;
    private static final int VM_RAM = 512;
    private static final int VM_BW = 1000;
    private static final int VM_SIZE= 10_000;
    private static int VM_MIPS;

    private static final int CLOUDLETS = 4;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10_000;

    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = 10; // Integer.MAX_VALUE
    //private static final String WORKLOAD_FILENAME = "workload/swf/KTH-SP2-1996-2.1-cln.swf.gz";
    //private static final String WORKLOAD_FILENAME = "workload/swf/HPC2N-2002-2.2-cln.swf.gz";     // 202871
    private static final String WORKLOAD_FILENAME = "workload/swf/NASA-iPSC-1993-3.1-cln.swf.gz";  // 18239


    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private Datacenter datacenter1;
    MyHeuristicBroker broker0;

    List<Integer> VM_MIPSList = new ArrayList<Integer>() {{
        add(1000);
        add(2000);
        add(3000);
        add(4000);
        add(5000);
        add(6000);
        add(7000);
        add(8000);
        add(9000);
        add(10000);
    } };


    public static void main(String[] args) {
        new LionScheduler();
    }

    private LionScheduler() {

        Log.setLevel(Level.INFO);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        datacenter1 = createDatacenter();

        broker0 = new MyHeuristicBroker(simulation);

        //vmList = createVmsTimeShared();
        vmList = createVmsSpaceShared();

        //cloudletList = createCloudlets();
        cloudletList = createCloudletsFromWorkloadFile();

        //modifyCloudletsForSpaceShared();
        cloudletList = filterCloudlets();


        cloudletList.forEach(c-> System.out.println(c.getId()+" : "+c.getNumberOfPes()));

        considerSubmissionTimes(0);

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);


        //broker0.Random(vmList);
        broker0.FirstComeFirstServe(vmList);
        //broker0.LongestJobFirst(vmList);
        //broker0.ShortestJobFirst(vmList);
        //broker0.ShortestCloudletFastestPE(vmList);
        //broker0.LongestCloudletFastestPE(vmList);
        //broker0.MinimumCompletionTime(vmList);
        //broker0.MinimumExecutionTime(vmList);
        //broker0.MaxMin(vmList);
        //broker0.MinMin(vmList);
        //broker0.Sufferage(vmList);
        //broker0.ShortestJobFirstFirstFit(vmList);
        //broker0.LongestJobFirstFirstFit(vmList);

        simulation.start();

        System.out.println(datacenter0.getHostList());
        System.out.println(datacenter1.getHostList());

        totalHostMIPSCapacity();
        totalVmMIPSCapacity();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

    }


    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(30000));
        }
        Host h = new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
        h.setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    private List<Vm> createVmsTimeShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPSList.get(i % 10);
            final Vm vm = new VmSimple(VM_MIPS , VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    private List<Vm> createVmsSpaceShared() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            VM_MIPS = VM_MIPSList.get(i % 10);
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudletsFromWorkloadFile() {
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(WORKLOAD_FILENAME, 1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        System.out.printf("# Created %12d Cloudlets for %n", this.cloudletList.size());
        return cloudletList;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }
        return list;
    }

    private void considerSubmissionTimes(int n) {

        if (n == 1) {
            double minSubdelay = cloudletList.get(0).getSubmissionDelay();
            for (Cloudlet c : cloudletList
            ) {
                c.setSubmissionDelay(c.getSubmissionDelay() - minSubdelay);
            }
        } else if (n == 0){
            cloudletList.forEach(c->c.setSubmissionDelay(0));
        }

    }

    private void totalHostMIPSCapacity(){
        List<Host> totalHostList = Stream.concat(datacenter0.getHostList().stream(), datacenter1.getHostList().stream())
            .collect(Collectors.toList());
        double totalHostMIPSCapacity = 0.0;
        for (Host h: totalHostList
        ) {
            totalHostMIPSCapacity += h.getTotalMipsCapacity();
        }
        System.out.println(totalHostMIPSCapacity);
    }

    private void totalVmMIPSCapacity(){
        double totalVmMIPSCapacity = 0.0;
        for (Vm v: broker0.getVmCreatedList()
        ) {
            totalVmMIPSCapacity += v.getTotalMipsCapacity();
        }
        System.out.println(totalVmMIPSCapacity);
    }

    private void modifyCloudletsForSpaceShared() {
        cloudletList.forEach(c->c.setLength(c.getTotalLength()));
        cloudletList.forEach(c->c.setNumberOfPes(1));
    }

    private List<Cloudlet> filterCloudlets() {

        List<Cloudlet> clist = cloudletList
                                    .stream()
                                    .filter(c-> c.getNumberOfPes() <= 2)
                                    .collect(Collectors.toList());
        return clist;

    }

}
