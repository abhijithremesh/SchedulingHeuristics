## Steps

###  Minimum Completion Time 1
  *   Generating the Task-VM completion time matrix of all tasks-VM combination.
  *   For each task, find that VM which gives the minimum completion time.
  *   Bind that task to the corresponding VM.
  *   Iterate for all the remaining tasks.

### Minimum Completion Time 2
  * Similar to Minimum Completion Time 1
  * scheduling done after begin of simulation (guess, not the correct way)  

### Minimum Completion Time 3
  * Similar to MIN-MIN algorithm
  * Algorithm implemented based on the paper "Performance comparison of heuristic algorithms for task scheduling in IaaS cloud computing environment"
  * seems incorrect as it is closely related to MIN-MIN strategy. 

### Completion Time
  * Cij = Eij + Rj
    * Cij = completion time of task i on VM j 
    * Eij = execution time of task i on VM j (cloudlet length / (VM MIPS * VM PES))
    * Rj = ready time of VM j (waiting time)
