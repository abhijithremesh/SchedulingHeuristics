package org.cloudsimplus.examples.GeneticAlgorithm9;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.examples.GeneticAlgorithm7.GA0;
import org.cloudsimplus.examples.GeneticAlgorithm7.Simulate0;
import org.cloudsimplus.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Simulate1 {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;

    private static final int VMS = 4;
    private static final int VM_PES = 1;

    private static final int CLOUDLETS = 5;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private CloudSim simulation;

    private DatacenterBroker broker;

    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private Datacenter datacenter;

    public static void main(String[] args) throws IOException, ParseException {
        new Simulate1();
    }

    private Simulate1() throws IOException, ParseException {

        Log.setLevel(Level.OFF);

        GA0 ga = new GA0();

        ArrayList<String[]> workloadEntries = getWorkloadEntries();
        cloudletList = createKTHWorloadCloudlets(workloadEntries, workloadEntries.size());

        //cloudletList = createCloudlets();

        displayCloudletLength(cloudletList);

        //ArrayList<ArrayList> chromosomeList = ga.createInitialPopulation(CLOUDLETS, VMS);
        ArrayList<ArrayList> chromosomeList = ga.createInitialPopulation(cloudletList.size(), VMS);

        ArrayList<Double> generationFitness = new ArrayList<Double>();

        System.out.println(chromosomeList);

        System.out.println("chromosome List size: "+chromosomeList.size());

        for (int generations = 0; generations < 20; generations++) {

            System.out.println("Generation: " + generations);

            ArrayList<Double> fitnessList = new ArrayList<Double>();

            for (int i = 0; i < chromosomeList.size(); i++) {

                ArrayList<Integer> chromosome = chromosomeList.get(i);

                simulation = new CloudSim();
                datacenter = createDatacenter();

                broker = new DatacenterBrokerSimple(simulation);

                vmList = createVms();
                //cloudletList = createCloudlets();
                cloudletList = createKTHWorloadCloudlets(workloadEntries, 30);

                //System.out.println("cloudletList size: "+cloudletList.size());

                broker.submitVmList(vmList);
                broker.submitCloudletList(cloudletList);

                //getSystemSpec();
                //System.out.println("cloudletList: "+cloudletList);
                //System.out.println("vmList: "+vmList);

                for (int j = 0; j < chromosome.size(); j++) {

                    Cloudlet c = cloudletList.get(j);
                    Vm v = vmList.get(chromosome.get(j));
                    broker.bindCloudletToVm(c, v);

                }

                simulation.start();

                double tft = ga.calculateFitness(broker);

                fitnessList.add(tft);

            }

            System.out.println("Chromosome List: " + chromosomeList);
            System.out.println("Fitness List: " + fitnessList);
            System.out.println("chromosomeList size: " + chromosomeList.size());
            System.out.println("fitnessList size: " + fitnessList.size());

            generationFitness.add(generationFitness(fitnessList, "min"));
            //ArrayList<Double> generationFitnessMinimum = generationFitness(fitnessList,"min");
            System.out.println("Generation Fitness: " + generationFitness);

            ArrayList<ArrayList> offspringsList = new ArrayList<ArrayList>();

            // If using Elite method for passing elite individuals to the next generation.
            ArrayList<ArrayList> eliteChromosomes = fittestEliteChromosome(fitnessList, chromosomeList, 3, "min");
            offspringsList.addAll(eliteChromosomes);

            removeWeakChromosomes(fitnessList, chromosomeList, "max", 3);

            int chosen = 3;

            System.out.println("Creating " + chosen + " offsprings......");

            for (int i = 0; i < chosen; i++) {

                Random r = new Random();
                ArrayList<Integer> parentChromosome1 = new ArrayList<Integer>();
                ArrayList<Integer> parentChromosome2 = new ArrayList<Integer>();

                int parentSelectionCriteria = r.nextInt(9 - 1 + 1) + 1;
                int rank = 0;
                int upperLimit = (int) (chromosomeList.size() * (30.0f / 100.0f));
                int lowerLimit = 2;
                if (upperLimit <= lowerLimit) {
                    lowerLimit--;
                }

                switch (parentSelectionCriteria) {
                    case 9:
                        System.out.println("Random & Most Fittest");
                        parentChromosome1 = randomChromosome(chromosomeList);
                        parentChromosome2 = fittestChromosome(fitnessList, chromosomeList, "min");
                        break;
                    case 1:
                        System.out.println("Tournament & Most Fittest");
                        parentChromosome1 = fittestTournamentChromosome(fitnessList, chromosomeList, "min", 4);
                        parentChromosome2 = fittestChromosome(fitnessList, chromosomeList, "min");
                        break;
                    case 8:
                        System.out.println("Random & Tournament");
                        parentChromosome1 = randomChromosome(chromosomeList);
                        parentChromosome2 = fittestTournamentChromosome(fitnessList, chromosomeList, "min", 4);
                        break;
                    case 2:
                        System.out.println("Tournament & Tournament");
                        parentChromosome1 = fittestTournamentChromosome(fitnessList, chromosomeList, "min", 4);
                        parentChromosome2 = fittestTournamentChromosome(fitnessList, chromosomeList, "min", 4);
                        break;
                    case 7:
                        System.out.println("Random  & Random");
                        parentChromosome1 = randomChromosome(chromosomeList);
                        parentChromosome2 = randomChromosome(chromosomeList);
                        break;
                    case 3:
                        //System.out.println("upperLimit: " + upperLimit + " lowerLimit: " + lowerLimit);
                        rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                        System.out.println(rank + "th Fittest & Most Fittest");
                        parentChromosome1 = nthFittestChromosome(fitnessList, chromosomeList, "min", 2);
                        parentChromosome2 = fittestChromosome(fitnessList, chromosomeList, "min");
                        break;
                    case 4:
                        //System.out.println("upperLimit: " + upperLimit + "lowerLimit: " + lowerLimit);
                        rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                        System.out.println("Tournament & " + rank + "th Fittest");
                        parentChromosome1 = fittestTournamentChromosome(fitnessList, chromosomeList, "min", 4);
                        parentChromosome2 = nthFittestChromosome(fitnessList, chromosomeList, "min", 2);
                        break;
                    case 6:
                        //System.out.println("upperLimit: " + upperLimit + "lowerLimit: " + lowerLimit);
                        rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                        System.out.println("Random & " + rank + "th Fittest");
                        parentChromosome1 = randomChromosome(chromosomeList);
                        parentChromosome2 = nthFittestChromosome(fitnessList, chromosomeList, "min", rank);
                        break;
                    case 5:
                        //System.out.println("upperLimit: " + upperLimit + " lowerLimit: " + lowerLimit);
                        rank = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                        int rank2 = r.nextInt(upperLimit - lowerLimit + 1) + lowerLimit;
                        System.out.println(rank + "th Fittest & " + rank2 + "th Fittest");
                        parentChromosome1 = nthFittestChromosome(fitnessList, chromosomeList, "min", 2);
                        parentChromosome2 = nthFittestChromosome(fitnessList, chromosomeList, "min", 3);
                        break;
                }

                System.out.println("parentChromosome1: " + parentChromosome1);
                System.out.println("parentChromosome2: " + parentChromosome2);

                ArrayList<Integer> childChromosome = new ArrayList<Integer>();
                int crossoverType = r.nextInt(4);

                switch (crossoverType) {
                    case 0:
                        System.out.println("Performing Random Crossover.....");
                        childChromosome = randomCrossover(parentChromosome1, parentChromosome2);
                        break;
                    case 1:
                        System.out.println("Performing Uniform Crossover....");
                        childChromosome = uniformCrossover(parentChromosome1, parentChromosome2);
                        break;
                    case 2:
                        System.out.println("Performing Single point Crossover.....");
                        childChromosome = singlePointCrossover(parentChromosome1, parentChromosome2);
                        break;
                    case 3:
                        System.out.println("Performing Two point Crossover.....");
                        childChromosome = twoPointCrossover(parentChromosome1, parentChromosome2);
                        break;
                }

                System.out.println("childChromosome: " + childChromosome);

                ArrayList<Integer> mutatedChildChromosome = new ArrayList<Integer>();
                int mutationType = r.nextInt(2);

                switch (mutationType) {
                    case 0:
                        System.out.println("Performing Swap Mutation...");
                        mutatedChildChromosome = mutateSwap(childChromosome);
                        break;
                    case 1:
                        System.out.println("Performing Reverse Mutation...");
                        //mutatedChildChromosome = mutateReverse(childChromosome);
                        mutatedChildChromosome = mutateSwap(childChromosome);
                        break;
                }

                System.out.println("mutatedChildChromosome: " + mutatedChildChromosome);

                offspringsList.add(mutatedChildChromosome);

                System.out.println("**************************");

            }

            chromosomeList.addAll(offspringsList);

        }

    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
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
            vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
            vm.setRam(512).setBw(1000).setSize(10000);
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets() {

        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        int customLength = 0;
        for (int i = 0; i < CLOUDLETS; i++) {
            //Random random = new Random();
            //int randomLength = random.nextInt(500);
            customLength += 100;
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH + customLength, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }
        return list;
    }

    private ArrayList<Integer> fittestChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag) {

        //System.out.println("chromosomeList size:"+chromosomeList.size());
        //System.out.println("fitnessList size: "+fitnessList.size());
        double fittestValue = 0.0;
        if (flag == "max") {
            fittestValue = Collections.max(fitnessList);
        } else if (flag == "min") {
            fittestValue = Collections.min(fitnessList);
        }
        //System.out.println("Fittest Value: " + fittestValue);
        int fittestIndex = fitnessList.indexOf(fittestValue);
        //System.out.println("Fittest Index: " + fittestIndex);
        ArrayList<Integer> fittestChromosome = chromosomeList.get(fittestIndex);
        //System.out.println("Fittest  Chromosome: " + fittestChromosome);
        return fittestChromosome;

    }

    private ArrayList<ArrayList> fittestEliteChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, int eliteCount, String flag) {

        System.out.println("Identifying the " + eliteCount + " elite chromosomes.....");
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
        } else if (flag == "min") {
            List<Double> top = new ArrayList<Double>(fitnessListSorted.subList(0, eliteCount));
            for (Double d : top) {
                int i = fitnessList.indexOf(d);
                eliteChromosome.add(chromosomeList.get(i));
                eliteFitness.add(d);
                chromosomeList.remove(chromosomeList.get(i));
                fitnessList.remove(d);
            }
        }

        System.out.println("Removing " + eliteCount + " elite chromosomes and it's associated fitness values.....");
        //chromosomeList.rem;
        //fitnessList.removeAll(eliteFitness);
        System.out.println("chromosomeList size: " + chromosomeList.size());
        System.out.println("fitnessList size: " + fitnessList.size());

        return eliteChromosome;

    }

    private ArrayList<Integer> fittestTournamentChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int tournamentSize) {

        //System.out.println("chromosomeListts size:"+chromosomeList.size());
        //System.out.println("fitnessListts size: "+fitnessList.size());
        ArrayList<ArrayList> chromosomeListTournament = new ArrayList<ArrayList>();
        ArrayList<Double> fitnessListTournament = new ArrayList<Double>();

        for (int i = 0; i < tournamentSize; i++) {
            Random rand = new Random();
            int n = rand.nextInt(chromosomeList.size() - 1);
            //System.out.println("Tournament random: "+n);
            chromosomeListTournament.add(chromosomeList.get(n));
            fitnessListTournament.add(fitnessList.get(n));
        }

        ArrayList<Integer> fittestChromosomeTournament = fittestChromosome(fitnessListTournament, chromosomeListTournament, flag);
        return fittestChromosomeTournament;

    }

    private ArrayList<Integer> randomChromosome(ArrayList<ArrayList> chromosomeList) {

        //System.out.println("chromosomeList:"+chromosomeList);
        Random rdm = new Random();
        int n = rdm.nextInt(chromosomeList.size());
        ArrayList<Integer> randChromosome = chromosomeList.get(n);
        return randChromosome;

    }

    private ArrayList<Integer> nthFittestChromosome(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int rank) {

        //System.out.println("chromosomeList size nth:"+chromosomeList.size());
        //System.out.println("fitnessList size nth: "+fitnessList.size());

        double fittestValue = 0.0;
        ArrayList<Double> fitnessListSorted = new ArrayList<>(fitnessList);
        Collections.sort(fitnessListSorted);


        if (flag == "max") {
            fittestValue = fitnessListSorted.get(fitnessListSorted.size() - rank);
        } else if (flag == "min") {
            fittestValue = fitnessListSorted.get(rank - 1);
        }

        int fittestIndex = fitnessList.indexOf(fittestValue);
        ArrayList<Integer> fittestChromosome = chromosomeList.get(fittestIndex);

        return fittestChromosome;

    }

    private Double generationFitness(ArrayList<Double> fitnessList, String flag) {

        //ArrayList<Double> generationFitness = new ArrayList<Double>();
        double generationFitness = 0.0;
        if (flag == "max") {
            generationFitness = Collections.max(fitnessList);
        } else if (flag == "min") {
            generationFitness = Collections.min(fitnessList);
        }

        return generationFitness;

    }

    private void removeWeakChromosomes(ArrayList<Double> fitnessList, ArrayList<ArrayList> chromosomeList, String flag, int count) {

        for (int i = 0; i < count; i++) {
            int index;
            if (flag == "max") {
                index = fitnessList.indexOf(Collections.max(fitnessList));
            } else {
                index = fitnessList.indexOf(Collections.min(fitnessList));
            }
            fitnessList.remove(index);
            chromosomeList.remove(index);
        }

        System.out.println("Removing " + count + " weak chromosomes and it's associated fitness values....");
        System.out.println("chromosomeList size: " + chromosomeList.size());
        System.out.println("fitnessList size: " + fitnessList.size());

    }

    private ArrayList<Integer> uniformCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int size = chromosomeOne.size();
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                chromosome.add(chromosomeOne.get(i));
            } else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }

    private ArrayList<Integer> randomCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo) {

        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        Random rand = new Random();

        for (int i = 0; i < chromosomeOne.size(); i++) {
            if (rand.nextInt(2) == 1) {
                chromosome.add(chromosomeOne.get(i));
            } else {
                chromosome.add(chromosomeTwo.get(i));
            }
        }
        return chromosome;
    }

    private ArrayList<Integer> singlePointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo) {

        Random r = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();
        int crossoverPoint = r.nextInt(chromosomeOne.size());
        //System.out.println(crossoverPoint);
        for (int i = 0; i < chromosomeOne.size(); i++) {
            if (i < crossoverPoint)
                chromosome.add(chromosomeOne.get(i));
            else
                chromosome.add(chromosomeTwo.get(i));
        }
        return chromosome;

    }

    private ArrayList<Integer> twoPointCrossover(ArrayList<Integer> chromosomeOne, ArrayList<Integer> chromosomeTwo) {

        Random r = new Random();
        ArrayList<Integer> chromosome = new ArrayList<Integer>();

        int crossoverPointOne = r.nextInt(chromosomeOne.size());
        int crossoverPointTwo = r.nextInt(chromosomeOne.size());

        if (crossoverPointOne == crossoverPointTwo) {
            if (crossoverPointOne == 0) {
                crossoverPointTwo++;
            } else {
                crossoverPointOne--;
            }
        }

        if (crossoverPointTwo < crossoverPointOne) {
            int temp = crossoverPointOne;
            crossoverPointOne = crossoverPointTwo;
            crossoverPointTwo = temp;
        }

        //System.out.println(crossoverPointOne+" "+crossoverPointTwo);

        for (int i = 0; i < chromosomeOne.size(); i++) {
            if (i < crossoverPointOne || i > crossoverPointTwo)
                chromosome.add(chromosomeOne.get(i));
            else
                chromosome.add(chromosomeTwo.get(i));
        }
        return chromosome;

    }

    private ArrayList<Integer> mutateReverse(ArrayList<Integer> chromosome) {

        ArrayList<Integer> childChromosomeReverse = new ArrayList<>(chromosome);
        Collections.reverse(childChromosomeReverse);
        return childChromosomeReverse;

    }

    private ArrayList<Integer> mutateSwap(ArrayList<Integer> chromosome) {

        Random r = new Random();
        ArrayList<Integer> childChromosomeSwap = new ArrayList<>(chromosome);
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

        return childChromosomeSwap;

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


    private List<Cloudlet> createKTHWorloadCloudlets(ArrayList<String[]> workloadEntries, int n) throws ParseException {

        final List<Cloudlet> list = new ArrayList<>(n);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < n; i++) {
            long cloudletLength = jobLength(workloadEntries.get(i)[6],workloadEntries.get(i)[4],workloadEntries.get(i)[8]);
            long submitTime = submitTime(workloadEntries.get(i)[3]);
            if (cloudletLength > 0){
                final Cloudlet cloudlet = new CloudletSimple(cloudletLength, CLOUDLET_PES, utilizationModel);
                cloudlet.setSizes(1024);
                cloudlet.setSubmissionDelay(submitTime);
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

    private void displayCloudletLength (List<Cloudlet> cloudletList) {

        ArrayList<Long> cLengths = new ArrayList<Long>();
        ArrayList<Double> cSubmitTimes = new ArrayList<Double>();

        for (Cloudlet c : cloudletList) {
            cLengths.add(c.getLength());
            cSubmitTimes.add(c.getSubmissionDelay());
        }

        System.out.println("cloudlet Lengths: "+cLengths);
        System.out.println("cloudlet Submit Times: "+cSubmitTimes);
        System.out.println("cloudlet max submit time: "+Collections.max(cSubmitTimes));

    }

}



