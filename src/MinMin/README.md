## Steps

### Min-Min Broker 2

* For each task, determine its minimum completion time over all resources(VMs) which results in task-VM matrix.
* Over all the tasks, Find the minimum completion time
* Assign the task to the resource that gives this completion time.
* Update the completion times in task-VM matrix.
* Iterate till all the tasks are scheduled.
* Completion Time
  * Cij = Eij + Rj
    * Cij = completion time of task i on VM j 
    * Eij = execution time of task i on VM j (cloudlet length / (VM MIPS * VM PES))
    * Rj = ready time of VM j (waiting time)

-------------------------------------------------------------------------------------------


![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/MIN-MIN%20example.PNG)


### Min-Min Broker 3 (RASA: A New Task Scheduling algorithm in grid environment)

* Computing the task-VM completion time matrix of all tasks-VM combination.
* For each task, find the earliest completion time over all VMs.
* Find the task with minimum earliest completion time.
* Assign task to the VM that gives the earliest completion time.
* Delete that task from the matrix.
* Update the completion times in task-VM matrix.
* Iterate till all the tasks are scheduled.
