# Generic Genetic Algorithm Implementations

# Type 2
* The length of the chromosome (Number of genes in the chromosome) corresponds to the number of cloudlets.
* Each gene in the chromosome is associated with a cloudlet and vm
* Intitial population of chromosomes is generated. The size of the population is any arbitrary number.
* Compute crossover probability and mutation probability.
* If valid crossover probability
  * Take two random chromosomes from the population  
  * Clone the selected random chromosomes
  * Interchanging or Swapping the vms from the random gene of both selected chromosomes to make new two chromosomes
  * Updating the population with these two new chromosomes
* If valid mutation probability
  * Take a random chromosome from the population and make a clone of it.
  * Update the random gene of the cloned chromosome by replacing its vm with another vm.
  * Updating the population with this new cloned chromosome.
* Computing the fitness of all chromosomes in the population
  * The makespan is used as the fitness criteria.
* The chromosome with the best fitness value is selected.
* The genes of this chromosome contains the cloudlets and its corresponding VMs where it must get executed.







