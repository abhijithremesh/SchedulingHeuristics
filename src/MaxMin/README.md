## Steps

### Max-Min Broker 2

* Computing the task-VM completion time matrix of all tasks-VM combination.
* For each task, determine its minimum completion time over all VMs.
* Over all the tasks, Find the maximum completion time
* Assign the task to the resource that gives this completion time.
* Update the completion times in task-VM matrix.
* Iterate till all the tasks are scheduled.

* Completion Time
  * Cij = Eij + Rj
    * Cij = completion time of task i on VM j 
    * Eij = execution time of task i on VM j (cloudlet length / (VM MIPS * VM PES))
    * Rj = ready time of VM j (waiting time)

-------------------------------------------------------------------------------------------

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/MAX-MIN%20example.PNG)
