
## Steps

### Improved Max-Min

* Computing the task-VM completion time matrix and execution time matrix of all tasks-VM combination.
* Finding task with maximum execution time value from execution time matrix
* Assigning that task to the VM with minimum completion time.
* Removing that task from the matrix
* Update the completion times and execution times in task-VM matrix.
* Iterate till all the tasks are scheduled.

* Completion Time
  * Cij = Eij + Rj
    * Cij = completion time of task i on VM j 
    * Eij = execution time of task i on VM j (cloudlet length / (VM MIPS * VM PES))
    * Rj = ready time of VM j (waiting time)

-------------------------------------------------------------------------------------------
