# Genetic Algorithm + Scheduling Heuristics

* Genetic Algorithm implemented on scheduling heuristic level. (Arbitrary cloudlets)

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/ga-heuristic-arbitrary.png)

* Genetic Algorithm implemented on scheduling heuristic level. (KTH workload based cloudlets)

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/ga-heuristic-KTHWorkload.png)

* Fitness Function = Makespan + Avg waiting time(all cloudlets) + Avg execution time(all cloudlets)
   *  Makespan = Total finish time = finish time of the last cloudlet.
   *  Avg waiting time = sum of waiting time of all cloudlets / no. of cloudlets
   *  Avg execution time = sum of execution time (actual cpu time) of all cloudlets / no. of cloudlets

* Hyperparameters
   * Cross over rate
   * Mutation rate
   * Elite count
   * Weak count
   * Offspring count
   * Simulation pause period / Heuristic switching frequency
   * No. of heuristics included

* System Spec
   * Hosts = 2
   * Host PEs = 2
   * Host PEs MIPS = 1500
   * VMs = 4
   * VM PEs = 1
   * VM PEs MIPS = [1000,1100,1200,1300]
   * Cloudlets = n ( n cannot be too high, cannot compute) / KTH workloads (27594)
   * Cloudlet lengths = 1000 + incremental(constant)  / KTH workloads (tstop-tstart/pe) 







