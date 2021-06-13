package org.cloudsimplus.examples.GeneticAlgorithm7;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {


    public ArrayList<ArrayList> createInitialPopulation(int num_cloudlet,int num_vm){

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        ArrayList<ArrayList> chromosomeList = new ArrayList<ArrayList>();

        for( int i=0; i < num_cloudlet*2 ; i++) {
            chromosome = createChromosome(num_cloudlet,num_vm);
            chromosomeList.add(chromosome);
        }

        return chromosomeList;
    }

    private static int getNum(ArrayList<Integer> v) {

        int n = v.size();  // Size of the vector
        int index = (int)(Math.random() * n); // Make sure the number is within the index range
        int num = v.get(index); // Get random number from the vector
        v.set(index, v.get(n - 1)); // Remove the number from the vector
        v.remove(n - 1);
        return num;  // Return the removed number

    }

    // Function to generate n non-repeating random numbers
    private static ArrayList<Integer> generateRandom(int n)
    {
        ArrayList<Integer> v = new ArrayList<Integer>(n);
        ArrayList<Integer> ans = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++)   // Fill the vector with the values 1, 2, 3, ..., n
            v.add(i + 1);
        while (v.size() > 0) {        // While vector has elements get a random number from the vector and print it
            ans.add(getNum(v)-1);
        }
        return ans;
    }

    private static ArrayList<Integer> createChromosome(int length,int range)
    {
        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        ArrayList<Integer> part = new ArrayList<Integer>();
        Random rand = new Random();
        int c = 0;
        int remainder = length%range;
        int division = (length-remainder)/range;

        for(int i = 0;i<division;i++) {
            part = generateRandom(range);
            for(int j=0;j<part.size();j++) {
                chromosome.add(part.get(j));
                c = c+1;
            }
        }

        int temp;

        for(int i=c;i<length;i++) {
            temp = rand.nextInt(range);
            chromosome.add(temp);
        }
        return chromosome;
    }

    public double calculateFitness(DatacenterBroker broker){

            List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
            Cloudlet c = finishedCloudlets.get(finishedCloudlets.size()-1);
            double tft = c.getFinishTime();
            tft = Math.round(tft * 100.0) / 100.0;
            return tft;

    }

}
