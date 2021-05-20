# Generic Genetic Algorithm Implementations

# Type 1
* Chromosome whose length corresponds to the no. of cloudlets (numcl) and genes corresponds to the VMs.
* Initializing a population of 100 chromosomes --> chromosome List and corresponding Fitness list to store the respective fitness.
* Running for n generations (n = 10-20)
   * Taking (numcl) chromosomes from the population (chromsome list)
   * Calculation fitness for each chromosome (makespan = cloudletlength/VM_MIPS * VM_PES)
      * Such that the first gene of the chromosome contains the first cloudlet and a VM based on which the fitness value is calculated.
      * The fitness value with respect to each VM is calculated for each chromosome.
      * The maximum fitness value among all VMs  is regarded as the fitness value.
   * The fitness value of each chromosome is stored in the fitness list. Thus, fitness list and chromosome list has the same number of elements (numcl)
   * Minimum fitness value in the fitness list and it's respective index is identified.
   * This fitness value is regareded as the best fitness value and its corresponding chromosome is regarded as the fittest chromosome of the generation.
   * Identifying the weak chromosomes which have the minimum fitness values in the fitness list.
   * Thus, discarding around z (half or quarter) chromosomes from the chromosome list.
   * Creating new z offsprings via
      * Single point cross over at a random index of chromosome 
      * Uniform cross over --> even pos (take gene from chromosome A) and odd pos (take gene from chromosome B)
      * Random cross over ---> Random generated 0 or 1, if 0 then from chromosome A, if B then from chromosome B)
      * The candidates for cross over process: any two random chromosomes or a random chromosome with best chromosome or best chromosome with second best chromosome
   * Adding the offsprings to the chromosomeList.    
   

* The GeneticAlgorithm(1-5) corresponds to Type 1 implementations where
    * GeneticAlgorithm1 --> fitness value (makespan) of chromosome calculated with respect to each VM taking into account the upload/download time(filesize/bandwidth)
    * GeneticAlgorithm2 --> fitness value (makespan) of chromosome calculated without considering the upload/download time.
    * GeneticAlgorithm3 --> fitness value (makespan) of chromosome calculated with respect to each VM without considering the upload/download time.
    * GeneticAlgorithm4-5 --> dummy implementations open to new inclusions.

# Type 2







