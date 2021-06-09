package org.cloudsimplus.examples.GeneticAlgorithm6;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;

public class Chromosomes {

    private ArrayList<Gene> chromosome;

    public Chromosomes(ArrayList<Gene> chromosome){
        this.chromosome = chromosome;
    }

    public ArrayList<Gene> getChromsome(){
        return this.chromosome;
    }

    public void updateGene(int index, Vm vm){
         Gene g = this.chromosome.get(index);
         g.setVmForGene(vm);
         this.chromosome.set(index,g);
    }

    public void printChromosome() {
        this.chromosome.forEach(g ->g.printGene());
    }

}
