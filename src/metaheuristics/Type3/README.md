# Generic Genetic Algorithm Implementations

# Type 3
* The length of the chromosome (Number of genes in the chromosome) corresponds to the number of cloudlets.
* Each index of the chromosome contains a value where the value represents the VM and the index represents the cloudlet.
* Intitial population of chromosomes is generated. The size of the initial population is a multiple of the number of cloudlets.
* In each generation
 * For each chromosome in the population, its corresponding fitness value (makespan/total finish time) is computed.
 * The best fitness value of the generation is also noted and being tracked.
 * The best parents of the population is selected as per the fitness value (maximum or minimum), based on the following selection criteria.
   * Elite ( passed directly to the next generation)(count=2)
   * Random
   * Most Fittest 
   * Tournament selection (count=3)
   * Nth Fittest 
 * Two parents for the next generation is identified based on the combination of these selection schemes:
   * Random & Most Fittest
   * Nth Fittest & Most Fittest 
   * Tournament & Most Fittest
   * Tournament & Nth Fittest
   * Random & Nth Fittest
   * Random & Tournament
   * Tournament & Tournament
   * Random & Random
   * Nth Fittest & Nth Fittest
 * If the randomly generated cross over probability > cross over rate (0.5), any of the below cross over operations are performed to produce offspring
   * Random cross over
   * Uniform cross over
   * Single point cross over
   * Two point cross over 
 * If the randomly generated mutation probability > mutation rate (0.2), the offspring generated is mutated as per the following strategies
   * Mutation through Swapping
   * Mutation through Reversing the offspring
 * The offspring created is to be used in the next generation.







