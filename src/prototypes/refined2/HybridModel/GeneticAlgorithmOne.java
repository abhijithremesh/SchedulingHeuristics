package org.cloudsimplus.examples.HybridModel;


import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmOne {
    public ArrayList<ArrayList> createInitialPopulation(int popCount, int num_heuristic){

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        ArrayList<ArrayList> chromosomeList = new ArrayList<ArrayList>();

        for( int i=0; i < popCount ; i++) {
            chromosome = createChromosome(24,num_heuristic);
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

    private static ArrayList<Integer> createChromosome(int length,int range) {
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

    public Double getGenerationAvgFittestValue (ArrayList<Double> fitnessList){

        double generationAvgFittestValue = 0.0;
        double generationSumFittestValue = 0.0;

        for (Double v:fitnessList
        ) {
            generationSumFittestValue = generationSumFittestValue + v;
        }
        generationAvgFittestValue = generationSumFittestValue/fitnessList.size();
        return ((double)Math.round(generationAvgFittestValue *  100.0)/100);
    }

    public Double getGenerationBestFittestValue (ArrayList<Double> fitnessList, String flag){
        double generationBestFittestValue = 0.0;
        if (flag == "min"){
            generationBestFittestValue = Collections.min(fitnessList);
        } else if (flag == "max") {
            generationBestFittestValue = Collections.max(fitnessList);
        }
        return generationBestFittestValue;
    }

    public ArrayList<Integer> getGenerationBestFittestSolutionCandidate (ArrayList<ArrayList> chromosomeList,ArrayList<Double> fitnessList, String flag){

        double generationBestFittestValue = getGenerationBestFittestValue(fitnessList,flag);

        ArrayList<Integer> generationBestFittestChromosome = chromosomeList.get(fitnessList.indexOf(generationBestFittestValue));

        return generationBestFittestChromosome;

    }

    public Double getBestFittestValue (ArrayList<Double> fitnessList, String flag){
        double BestFittestValue = 0.0;
        if (flag == "min"){
            BestFittestValue = Collections.min(fitnessList);
        } else if (flag == "max") {
            BestFittestValue = Collections.max(fitnessList);
        }
        return BestFittestValue;
    }

    public ArrayList<Integer> getBestFittestSolutionCandidate (ArrayList<ArrayList> chromosomeList,ArrayList<Double> fitnessList, String flag){

        double BestFittestValue = getBestFittestValue(fitnessList,flag);

        ArrayList<Integer> BestFittestChromosome = chromosomeList.get(fitnessList.indexOf(BestFittestValue));

        return BestFittestChromosome;

    }


    public ArrayList<ArrayList> fittestEliteChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, int eliteCount, String flag) {

        System.out.println("Identifying the "+eliteCount+" elite chromosomes.....");
        ArrayList<ArrayList> eliteChromosome = new ArrayList<ArrayList>();
        ArrayList<Double> eliteFitness = new ArrayList<Double>();
        ArrayList<Double> fitnessListSorted = new ArrayList<>(fitnessList);
        Collections.sort(fitnessListSorted);
        System.out.println("fitnessListSorted: "+fitnessListSorted);

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

    public ArrayList<Integer> fittestChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag) {

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

    public ArrayList<Integer>uniformCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo, double crossoverRate) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        Random rand = new Random();

        double crossoverProb = Math.round(rand.nextDouble() * 100.0)/100.0;

        if (crossoverProb > crossoverRate){
            for(int i=0;i<chromosomeOne.size();i++) {
                if(i%2==0) {
                    chromosome.add(chromosomeOne.get(i));
                }
                else {
                    chromosome.add(chromosomeTwo.get(i));
                }
            }
        }
        else{
            chromosome = ((rand.nextInt(1) == 0) ? chromosomeOne : chromosomeTwo);
        }

        return chromosome;
    }

    public ArrayList<Integer>randomCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo,double crossoverRate) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        Random rand = new Random();

        double crossoverProb = Math.round(rand.nextDouble() * 100.0)/100.0;

        if (crossoverProb > crossoverRate){
            for(int i=0;i<chromosomeOne.size();i++) {
                if(rand.nextInt(2) == 1) {
                    chromosome.add(chromosomeOne.get(i));
                }
                else {
                    chromosome.add(chromosomeTwo.get(i));
                }
            }
        }
        else{
            chromosome = ((rand.nextInt(1) == 0) ? chromosomeOne : chromosomeTwo);
        }

        return chromosome;
    }

    public ArrayList<Integer> singlePointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo, double crossoverRate){

        Random rand = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();

        double crossoverProb = Math.round(rand.nextDouble() * 100.0)/100.0;

        if (crossoverProb > crossoverRate) {
            int crossoverPoint = rand.nextInt(chromosomeOne.size());
            for (int i = 0; i < chromosomeOne.size(); i++) {
                if (i < crossoverPoint)
                    chromosome.add(chromosomeOne.get(i));
                else
                    chromosome.add(chromosomeTwo.get(i));
            }
        }
        else {
            chromosome = ((rand.nextInt(1) == 0) ? chromosomeOne : chromosomeTwo);
        }

        return chromosome;

    }

    public ArrayList<Integer> twoPointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo, double crossoverRate){

        Random rand = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();

        double crossoverProb = Math.round(rand.nextDouble() * 100.0)/100.0;

        if (crossoverProb > crossoverRate){

            int crossoverPointOne = rand.nextInt(chromosomeOne.size());
            int crossoverPointTwo = rand.nextInt(chromosomeOne.size());

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

        }

        else {

            chromosome = ((rand.nextInt(1) == 0) ? chromosomeOne : chromosomeTwo);

        }


        return chromosome;

    }

    public ArrayList<Integer> mutateSwap (ArrayList<Integer> chromosome, double mutationRate){

        Random r = new Random();
        ArrayList<Integer> childChromosomeSwap = new ArrayList<>(chromosome);

        double mutationProb = Math.round(r.nextDouble() * 100.0)/100.0;

        if (mutationProb > mutationRate) {
            int lowerLim = r.nextInt(chromosome.size());
            int upperLim = r.nextInt(chromosome.size());

            if (lowerLim == upperLim) {
                if (lowerLim == 0) {
                    upperLim++;
                } else {
                    lowerLim--;
                }
            }
            int positionOne = chromosome.get(lowerLim);
            int positionTwo = chromosome.get(upperLim);
            childChromosomeSwap.set(upperLim, positionOne);
            childChromosomeSwap.set(lowerLim, positionTwo);
        }
        else {
            childChromosomeSwap = chromosome;
        }

        return childChromosomeSwap;

    }

    public ArrayList<Integer> mutateReverse (ArrayList<Integer> chromosome){

        ArrayList<Integer> childChromosomeReverse = new ArrayList<>(chromosome);
        Collections.reverse(childChromosomeReverse);
        return childChromosomeReverse;

    }

    public ArrayList<ArrayList> generationEvolve(ArrayList<ArrayList> chromosomeList, ArrayList<Double> fitnessList, String flag, int eliteCount, int tournamentCount, double crossoverRate, double mutationRate){

        System.out.println("Candidate List: " + chromosomeList);
        System.out.println("Fitness List: " + fitnessList);
        System.out.println("Candidate List Size: " + chromosomeList.size());
        System.out.println("Fitness List Size: " + fitnessList.size());
        System.out.println("Flag: "+flag);
        System.out.println("eliteCount: "+eliteCount);

        ArrayList<ArrayList> nextGenPopulation = new ArrayList<ArrayList>();
        ArrayList<ArrayList> offspringsList = new ArrayList<ArrayList>();

        // Gets the elite chromosomes from the chromosomeList
        // Removes the elite chromosomes from the chromosomeList
        // Removes the fitness values of elite chromosomes from the fitnessList
        ArrayList<ArrayList> elites = fittestEliteChromosome(fitnessList,chromosomeList,eliteCount,flag);
        System.out.println("Elites: "+elites);

        nextGenPopulation.addAll(elites);
        System.out.println("NextGenPop: "+nextGenPopulation);
        System.out.println("NextGenPop: "+nextGenPopulation.size());


        for (int i=0; i < chromosomeList.size(); i++){

            Random r = new Random();
            ArrayList<Integer> parentChromosome1 = new ArrayList<Integer>();
            ArrayList<Integer> parentChromosome2 = new ArrayList<Integer>();

            int parentSelectionCriteria = r.nextInt(5) ;
            System.out.println("ParentSelectionCriteria: "+parentSelectionCriteria);

            switch (parentSelectionCriteria) {
                case 0:
                    System.out.println("Random & Most Fittest");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = fittestChromosome(fitnessList,chromosomeList,flag);
                    break;
                case 1:
                    System.out.println("Tournament & Most Fittest");
                    parentChromosome1 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,tournamentCount);
                    parentChromosome2 = fittestChromosome(fitnessList,chromosomeList,"min");
                    break;
                case 2:
                    System.out.println("Random & Tournament");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,tournamentCount);
                    break;
                case 3:
                    System.out.println("Tournament & Tournament");
                    parentChromosome1 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,tournamentCount);
                    parentChromosome2 = fittestTournamentChromosome(fitnessList,chromosomeList,flag,tournamentCount);
                    break;
                case 4:
                    System.out.println("Random  & Random");
                    parentChromosome1 = randomChromosome(chromosomeList);
                    parentChromosome2 = randomChromosome(chromosomeList);
                    break;

            }

            System.out.println("parentChromosome1: "+parentChromosome1);
            System.out.println("parentChromosome2: "+parentChromosome2);

            ArrayList<Integer> childChromosome = new ArrayList<Integer>();
            int crossoverType = r.nextInt(4);

            switch (crossoverType) {
                case 0:
                    System.out.println("Performing Random Crossover.....");
                    childChromosome = randomCrossover(parentChromosome1, parentChromosome2,crossoverRate);
                    break;
                case 1:
                    System.out.println("Performing Uniform Crossover....");
                    childChromosome = uniformCrossover(parentChromosome1, parentChromosome2,crossoverRate);
                    break;
                case 2:
                    System.out.println("Performing Single point Crossover.....");
                    childChromosome = singlePointCrossover(parentChromosome1, parentChromosome2,crossoverRate);
                    break;
                case 3:
                    System.out.println("Performing Two point Crossover.....");
                    childChromosome = twoPointCrossover(parentChromosome1, parentChromosome2,crossoverRate);
                    break;
            }

            System.out.println("childChromosome: "+childChromosome);

            int mutationType = r.nextInt(1);

            switch (mutationType) {
                case 0:
                    System.out.println("Performing Swap Mutation...");
                    childChromosome = mutateSwap(childChromosome, mutationRate);
                    break;
                case 1:
                    System.out.println("Performing Reverse Mutation...");
                    childChromosome = mutateReverse(childChromosome);
                    //mutatedChildChromosome = mutateSwap(childChromosome);
                    break;
            }

            System.out.println("childChromosome: " + childChromosome);

            offspringsList.add(childChromosome);

        }

        nextGenPopulation.addAll(offspringsList);
        System.out.println("NextGenPop Size: "+nextGenPopulation.size());

        return nextGenPopulation;

    }

    public double getTotalFinishTime(List<Cloudlet> finishedCloudletList ){
        Cloudlet c = finishedCloudletList.get(finishedCloudletList.size()-1);
        double tft = c.getFinishTime();
        tft = Math.round(tft * 100.0) / 100.0;
        return tft;
    }

    public double getTotalExecutionTime(List<Cloudlet> finishedCloudlets){
        // Total Execution Time
        double executionTime = 0.0;
        for (Cloudlet c : finishedCloudlets) {
            executionTime = executionTime + c.getActualCpuTime(); }
        executionTime = Math.round(executionTime * 100.0) / 100.0;
        return executionTime;
    }

    public double getTotalWaitingTime(List<Cloudlet> finishedCloudletList ){
        double twt = 0.0;
        for (Cloudlet c:finishedCloudletList ) {
            twt = twt + c.getWaitingTime(); }
        twt = Math.round(twt * 100.0) / 100.0;
        return twt;
    }

    public double getTotalFlowTime(List<Cloudlet> finishedCloudlets){
        // Total Flow Time
        double executionTime = 0.0;
        double waitingTime = 0.0;
        for (Cloudlet c : finishedCloudlets) {
            executionTime = executionTime + c.getActualCpuTime();
            waitingTime = waitingTime + c.getWaitingTime();
        }
        double flowTime = executionTime + waitingTime;
        flowTime = Math.round(flowTime * 100.0) / 100.0;
        return flowTime;
    }

    public double getAverageExecutionTime(List<Cloudlet> finishedCloudlets){
        // Average Execution time
        double executionTime = 0.0;
        for (Cloudlet c : finishedCloudlets) {
            executionTime = executionTime + c.getActualCpuTime();
        }
        double averageExecutionTime = executionTime/finishedCloudlets.size();
        averageExecutionTime = Math.round(averageExecutionTime * 100.0) / 100.0;
        return averageExecutionTime;
    }


    public double getAverageWaitingTime(List<Cloudlet> finishedCloudlets){
        // Average waiting time
        double waitingTime = 0.0;
        for (Cloudlet c : finishedCloudlets) {
            waitingTime = waitingTime + c.getWaitingTime();
        }
        double averageWaitingTime = waitingTime/finishedCloudlets.size();
        averageWaitingTime = Math.round(averageWaitingTime * 100.0) / 100.0;
        return averageWaitingTime;
    }


    public double getAverageFlowTime(List<Cloudlet> finishedCloudlets){
        // Total Flow Time
        double executionTime = 0.0;
        double waitingTime = 0.0;
        for (Cloudlet c : finishedCloudlets) {
            executionTime = executionTime + c.getActualCpuTime();
            waitingTime = waitingTime + c.getWaitingTime();
        }
        double averageFlowTime = ((waitingTime/finishedCloudlets.size()) + (executionTime/finishedCloudlets.size()))/finishedCloudlets.size();
        averageFlowTime = Math.round(averageFlowTime * 100.0) / 100.0;
        return averageFlowTime ;
    }


    public double calculateFitnessFunctionFour(DatacenterBroker broker){

        List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
        double fitnessValue = getTotalFinishTime(finishedCloudlets) + getAverageWaitingTime(finishedCloudlets) + getAverageExecutionTime(finishedCloudlets);

        fitnessValue = Math.round(fitnessValue * 100.0) / 100.0;

        return fitnessValue;

    }






}
