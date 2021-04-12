
### Max-Min Broker 2

* Computing the task-VM completion time matrix of all tasks-VM combination.
* For each task, determine its minimum completion time over all VMs.
* Over all the tasks, Find the maximum completion time
* Assign the task to the resource that gives this completion time.
* Update the completion times in task-VM matrix.
* Iterate till all the tasks are scheduled.
* Host uses VmSchedulerSpaceShared.
* VM uses CloudletSchedulerSpaceShared.

* Completion Time
  * Cij = Eij + Rj
    * Cij = completion time of task i on VM j 
    * Eij = execution time of task i on VM j (cloudlet length / (VM MIPS * VM PES))
    * Rj = ready time of VM j (waiting time)

-------------------------------------------------------------------------------------------

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/MAX-MIN%20example.PNG)


### Max-Min Broker 3 (Improved Max-Min algorithm in cloud computing)

* Computing the task-VM completion time matrix and execution time matrix of all tasks-VM combination.
* Find the task with maximum completion time
* Assign that task to the VM that gives minimum execution time .
* Update the completion times  and execution times in task-VM matrix.
* Iterate till all the tasks are scheduled.

### Max-Min Broker 4 (RASA: A New Task Scheduling algorithm in grid environment)

* Computing the task-VM completion time matrix of all tasks-VM combination.
* For each task, find the earliest completion time over all VMs.
* Find the task with maximum earliest completion time.
* Assign task to the VM that gives the earliest completion time.
* Delete that task from the matrix.
* Update the completion times in task-VM matrix.
* Iterate till all the tasks are scheduled.
