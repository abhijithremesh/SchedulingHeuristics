package org.cloudsimplus.examples.GeneticAlgorithm6;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

public class Gene {

    private Cloudlet cloudlet;
    private Vm vm;

    public Gene (Cloudlet cl, Vm v){
        this.cloudlet = cl;
        this.vm = v;
    }

    public Cloudlet getCloudletFromGene(){
        return this.cloudlet;
    }

    public Vm getVmFromGene(){
        return this.vm;
    }

    public void setCloudletForGene(Cloudlet cl){
        this.cloudlet = cl;
    }

    public void setVmForGene(Vm v){
        this.vm = v;
    }

    public void printGene() {
        System.out.print("( "+this.cloudlet.getId()+" , "+this.vm.getId()+")");
    }

}
