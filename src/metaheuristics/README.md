# Generic Genetic Algorithm Implementations

* Generate an initial population of N solution candidates for evolution. N is the population size (10)
* Fitness function is formulated. Fitness value of solution candidates is computed in every generation.
* Based on the fitness value, Elite Individuals of predetermined size (2) are selected which are passed directly to the next generation.
* Rest of the parent individuals are chosen from the tournament selection. (Tournament size = 3) 
* The above individuals undergo cross over and mutation.
  * Cross over
    * Cross over: Random cross over, Single point cross over, Two point cross over, Uniform cross over
    * Cross over performed if the randomly generated cross over probability less than provided cross over rate (0.5)
  * Mutation
    * The Offsprings further undergo a mutation where the genes within an offspring are changed depending on the type of mutation.
    * Mutation performed if the randomly generated mutation probability is less than the given mutation rate (0.2) 
* Every generation's fittest individual is compared with the previous best fitness value for each objective function.
* If the current individual better than the previous one, then the best value is updated.
* The steps are repeated until the termination criteria is satisfied which is nothing but the generation size set as 25. 







