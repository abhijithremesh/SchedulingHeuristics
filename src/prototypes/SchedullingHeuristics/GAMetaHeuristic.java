package org.cloudsimplus.examples.SchedullingHeuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GAMetaHeuristic {

    public ArrayList<ArrayList> createInitialPopulation(int popCount, int num_heuristic){

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        ArrayList<ArrayList> chromosomeList = new ArrayList<ArrayList>();

        for( int i=0; i < popCount ; i++) {
            chromosome = createChromosome(num_heuristic,num_heuristic);
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

    public Double generationFitness (ArrayList<Double> fitnessList, String flag){

        //ArrayList<Double> generationFitness = new ArrayList<Double>();
        double generationFitness = 0.0;
        if (flag == "max"){
            generationFitness = Collections.max(fitnessList);
        }
        else if (flag == "min"){
            generationFitness = Collections.min(fitnessList);
        }
        return generationFitness;

    }

    public ArrayList<ArrayList> fittestEliteChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, int eliteCount, String flag) {

        System.out.println("Identifying the "+eliteCount+" elite chromosomes.....");
        ArrayList<ArrayList> eliteChromosome = new ArrayList<ArrayList>();
        ArrayList<Double> eliteFitness = new ArrayList<Double>();
        ArrayList<Double> fitnessListSorted = new ArrayList<>(fitnessList);
        Collections.sort(fitnessListSorted);

        if (flag == "max") {
            List<Double> elite = new ArrayList<Double>(fitnessListSorted.subList(fitnessListSorted.size() - eliteCount, fitnessListSorted.size()));
            for (Double d : elite) {
                int i = fitnessList.indexOf(d);
                eliteChromosome.add(chromosomeList.get(i));
                eliteFitness.add(d);
                chromosomeList.remove(chromosomeList.get(i));
                fitnessList.remove(d);
            }
        }
        else if (flag == "min"){
            List<Double> top = new ArrayList<Double>(fitnessListSorted.subList(0, eliteCount));
            for (Double d : top) {
                int i = fitnessList.indexOf(d);
                eliteChromosome.add(chromosomeList.get(i));
                eliteFitness.add(d);
                chromosomeList.remove(chromosomeList.get(i));
                fitnessList.remove(d);
            }
        }

        System.out.println("Removing "+eliteCount+" elite chromosomes and it's associated fitness values.....");
        //chromosomeList.rem;
        //fitnessList.removeAll(eliteFitness);
        System.out.println("chromosomeList size: "+chromosomeList.size());
        System.out.println("fitnessList size: "+fitnessList.size());

        return eliteChromosome;
    }

    public void removeWeakChromosomes (ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int count) {

        for (int i = 0; i < count; i++) {
            int index;
            if (flag == "max") {
                index = fitnessList.indexOf(Collections.max(fitnessList));
            } else{
                index = fitnessList.indexOf(Collections.min(fitnessList));
            }
            fitnessList.remove(index);
            chromosomeList.remove(index);
        }

        System.out.println("Removing "+count+" weak chromosomes and it's associated fitness values....");
        System.out.println("chromosomeList size: "+chromosomeList.size());
        System.out.println("fitnessList size: "+fitnessList.size());

    }

    public ArrayList<Integer> fittestChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag) {

        //System.out.println("chromosomeList size:"+chromosomeList.size());
        //System.out.println("fitnessList size: "+fitnessList.size());
        double fittestValue = 0.0;
        if (flag == "max") {
            fittestValue = Collections.max(fitnessList); }
        else if (flag == "min"){
            fittestValue = Collections.min(fitnessList);
        }
        //System.out.println("Fittest Value: " + fittestValue);
        int fittestIndex = fitnessList.indexOf(fittestValue);
        //System.out.println("Fittest Index: " + fittestIndex);
        ArrayList<Integer> fittestChromosome = chromosomeList.get(fittestIndex);
        //System.out.println("Fittest  Chromosome: " + fittestChromosome);
        return fittestChromosome;

    }

    public ArrayList<Integer> fittestTournamentChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int tournamentSize) {

        //System.out.println("chromosomeListts size:"+chromosomeList.size());
        //System.out.println("fitnessListts size: "+fitnessList.size());
        ArrayList<ArrayList> chromosomeListTournament = new ArrayList<ArrayList>();
        ArrayList<Double> fitnessListTournament = new ArrayList<Double>();

        for (int i = 0; i < tournamentSize; i++) {
            Random rand = new Random();
            int n = rand.nextInt(chromosomeList.size()-1);
            //System.out.println("Tournament random: "+n);
            chromosomeListTournament.add(chromosomeList.get(n));
            fitnessListTournament.add(fitnessList.get(n));
        }

        ArrayList<Integer> fittestChromosomeTournament = fittestChromosome(fitnessListTournament, chromosomeListTournament,flag);
        return fittestChromosomeTournament;

    }

    public ArrayList<Integer> randomChromosome (ArrayList<ArrayList> chromosomeList) {

        //System.out.println("chromosomeList:"+chromosomeList);
        Random rdm = new Random();
        int n = rdm.nextInt(chromosomeList.size());
        ArrayList<Integer> randChromosome = chromosomeList.get(n);
        return randChromosome;

    }

    public ArrayList<Integer> nthFittestChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int rank){

        //System.out.println("chromosomeList size nth:"+chromosomeList.size());
        //System.out.println("fitnessList size nth: "+fitnessList.size());

        double fittestValue = 0.0;
        ArrayList<Double> fitnessListSorted = new ArrayList<>(fitnessList);
        Collections.sort(fitnessListSorted);


        if ( flag == "max"){
            fittestValue = fitnessListSorted.get(fitnessListSorted.size() - rank);
        }
        else if (flag == "min"){
            fittestValue = fitnessListSorted.get(rank-1);
        }

        int fittestIndex = fitnessList.indexOf(fittestValue);
        ArrayList<Integer> fittestChromosome = chromosomeList.get(fittestIndex);

        return fittestChromosome;

    }

    public ArrayList<Integer>uniformCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int size = chromosomeOne.size();
        for(int i=0;i<size;i++) {
            if(i%2==0) {
                chromosome.add(chromosomeOne.get(i));
            }
            else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }


    public ArrayList<Integer>randomCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        Random rand = new Random();

        for(int i=0;i<chromosomeOne.size();i++) {
            if(rand.nextInt(2) == 1) {
                chromosome.add(chromosomeOne.get(i));
            }
            else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }

    public ArrayList<Integer> singlePointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo){

        Random r = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int crossoverPoint = r.nextInt(chromosomeOne.size());
        //System.out.println(crossoverPoint);
        for (int i =0; i < chromosomeOne.size(); i++){
            if (i < crossoverPoint)
                chromosome.add(chromosomeOne.get(i));
            else
                chromosome.add(chromosomeTwo.get(i));
        }
        return chromosome;

    }

    public ArrayList<Integer> twoPointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo){

        Random r = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();

        int crossoverPointOne = r.nextInt(chromosomeOne.size());
        int crossoverPointTwo = r.nextInt(chromosomeOne.size());

        if (crossoverPointOne == crossoverPointTwo){
            if (crossoverPointOne == 0 ) {
                crossoverPointTwo++;
            } else {
                crossoverPointOne--;
            }
        }

        if (crossoverPointTwo < crossoverPointOne){
            int temp = crossoverPointOne;
            crossoverPointOne = crossoverPointTwo;
            crossoverPointTwo = temp;
        }

        //System.out.println(crossoverPointOne+" "+crossoverPointTwo);

        for (int i =0; i < chromosomeOne.size(); i++){
            if (i < crossoverPointOne || i > crossoverPointTwo)
                chromosome.add(chromosomeOne.get(i));
            else
                chromosome.add(chromosomeTwo.get(i));
        }
        return chromosome;

    }

    public ArrayList<Integer> mutateReverse (ArrayList<Integer> chromosome){

        ArrayList<Integer> childChromosomeReverse = new ArrayList<>(chromosome);
        Collections.reverse(childChromosomeReverse);
        return childChromosomeReverse;

    }

    public ArrayList<Integer> mutateSwap (ArrayList<Integer> chromosome){

        Random r = new Random();
        ArrayList<Integer> childChromosomeSwap = new ArrayList<>(chromosome);
        int lowerLim = r.nextInt(chromosome.size());
        int upperLim = r.nextInt(chromosome.size());

        if (lowerLim == upperLim){
            if (lowerLim == 0 ) {
                upperLim++;
            } else {
                lowerLim--;
            }
        }
        int positionOne = chromosome.get(lowerLim);
        int positionTwo = chromosome.get(upperLim);
        childChromosomeSwap.set(upperLim,positionOne);
        childChromosomeSwap.set(lowerLim,positionTwo);

        return childChromosomeSwap;

    }


    public void generationEvolve (int chosen,String flag, ArrayList<ArrayList> chromosomeList, ArrayList<Double> fitnessList, ArrayList<ArrayList> offspringsList){

        for (int i = 0; i < chosen; i++){

            Random r = new Random();
            ArrayList<Integer> parentChromosome1 = new ArrayList<Integer>();
            ArrayList<Integer> parentChromosome2 = new ArrayList<Integer>();

            int parentSelectionCriteria = r.nextInt(9 - 1 + 1) + 1;
            int rank = 0;
            int upperLimit = (int)(chromosomeList.size()*(30.0f/100.0f));
            int lowerLimit = 2;
            if (upperLimit <= lowerLimit){
                lowerLimit--;
            }

            //System.out.println(parentSelectionCriteria);

            switch (parentSelectionCriteria) {
                case 9:
                    System.out.println("Random & Most Fittest");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = fittestChromosome(fitnessList,chromosomeList,flag);
                    break;
                case 1:
                    System.out.println("Tournament & Most Fittest");
                    parentChromosome1 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,4);
                    parentChromosome2 = fittestChromosome(fitnessList,chromosomeList,"min");
                    break;
                case 8:
                    System.out.println("Random & Tournament");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,4);
                    break;
                case 2:
                    System.out.println("Tournament & Tournament");
                    parentChromosome1 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,4);
                    parentChromosome2 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,4);
                    break;
                case 7:
                    System.out.println("Random  & Random");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = randomChromosome(chromosomeList);
                    break;
                case 3:
                    //System.out.println("upperLimit: "+upperLimit+" lowerLimit: "+lowerLimit);
                    rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                    System.out.println(rank+"th Fittest & Most Fittest");
                    parentChromosome1 = nthFittestChromosome(fitnessList,chromosomeList,flag,rank);
                    parentChromosome2 = fittestChromosome(fitnessList,chromosomeList,flag);
                    break;
                case 4:
                    //System.out.println("upperLimit: "+upperLimit+"lowerLimit: "+lowerLimit);
                    rank = r.nextInt(upperLimit - lowerLimit + 1 ) + lowerLimit;
                    System.out.println("Tournament & "+rank+"th Fittest");
                    parentChromosome1 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,4);
                    parentChromosome2 = nthFittestChromosome(fitnessList,chromosomeList,flag,rank);
                    break;
                case 6:
                    //System.out.println("upperLimit: "+upperLimit+"lowerLimit: "+lowerLimit);
                    rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                    System.out.println("Random & "+rank+"th Fittest");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = nthFittestChromosome(fitnessList,chromosomeList,flag,rank);
                    break;
                case 5:
                    //System.out.println("upperLimit: "+upperLimit+" lowerLimit: "+lowerLimit);
                    rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                    int rank2 = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                    System.out.println(rank+"th Fittest & "+rank2+"th Fittest");
                    parentChromosome1 = nthFittestChromosome(fitnessList,chromosomeList,flag,rank);
                    parentChromosome2 = nthFittestChromosome(fitnessList,chromosomeList,flag,rank2);
                    break;
            }

            System.out.println("parentChromosome1: "+parentChromosome1);
            System.out.println("parentChromosome2: "+parentChromosome2);

            //double givenCrossoverRate = 0.5;
            //double givenMutationRate = 0.2;
            //double crossoverProb = Math.round(r.nextDouble() * 100.0)/100.0;
            //double mutationProb = Math.round(r.nextDouble() * 100.0)/100.0;
            //System.out.println(crossoverProb);
            //System.out.println(mutationProb);

            ArrayList<Integer> childChromosome = new ArrayList<Integer>();
            int crossoverType = r.nextInt(4);

            switch (crossoverType) {
                case 0:
                    System.out.println("Performing Random Crossover.....");
                    childChromosome = randomCrossover(parentChromosome1,parentChromosome2);
                    break;
                case 1:
                    System.out.println("Performing Uniform Crossover....");
                    childChromosome = uniformCrossover(parentChromosome1,parentChromosome2);
                    break;
                case 2:
                    System.out.println("Performing Single point Crossover.....");
                    childChromosome = singlePointCrossover(parentChromosome1,parentChromosome2);
                    break;
                case 3:
                    System.out.println("Performing Two point Crossover.....");
                    childChromosome = twoPointCrossover(parentChromosome1,parentChromosome2);
                    break;
            }


            ArrayList<Integer> mutatedChildChromosome = new ArrayList<Integer>();
            int mutationType = r.nextInt(2);


            switch (mutationType) {
                case 0:
                    System.out.println("Performing Swap Mutation...");
                    mutatedChildChromosome = mutateSwap(childChromosome);
                    break;
                case 1:
                    System.out.println("Performing Reverse Mutation...");
                    mutatedChildChromosome = mutateReverse(childChromosome);
                    //mutatedChildChromosome = mutateSwap(childChromosome);
                    break;
            }

            System.out.println("mutatedChildChromosome: "+mutatedChildChromosome);

            offspringsList.add(mutatedChildChromosome);

            //System.out.println("offspringsList: "+offspringsList);

            //chromosomeList.clear();
            //fitnessList.clear();

            //System.out.println("chromosomeList size: "+chromosomeList.size());
            //System.out.println("fitnessList size: "+fitnessList.size());

            System.out.println("**************************");

        }
    }



}
