# System Spec

* Hosts = 2
* Host PEs = 2
* Host PEs MIPS = 1500 MIPS
* VMs = 4
* VM PEs = 1
* VM PEs MIPs = [1100,1200,1300,1400]
* Cloudlets = 10
* Cloudlet length = 1000 + incremental(5)


# Scheduling Heuristics Behaviour

* FCFS

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/fcfs.PNG)

* Random

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/random.PNG)

* Min-Min

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/minmin.PNG)

* Max-Min

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/maxmin.PNG)

* Sufferage

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/sufferage.PNG)

* Minimum Execution Time

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/met.PNG)

* Minimum Completion Time

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/mct.PNG)

* Longest Cloudlet Fastest Processing Element 1
   * Cloudlets and VMs submitted to broker
   * Cloudlets and VMs sorted as per the heuristic. 
   
![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/lcfp1.PNG)

* Longest Cloudlet Fastest Processing Element 2
   * Cloudlets and VMs sorted as per the heuristic
   * Cloudlets and VMs submitted to broker  
   
![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/lcfp2.PNG)

* Shortest Cloudlet Fastest Processing Element 1
   * Cloudlets and VMs submitted to broker
   * Cloudlets and VMs sorted as per the heuristic. 

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/scfp1.PNG)

* Shortest Cloudlet Fastest Processing Element 2
   * Cloudlets and VMs sorted as per the heuristic
   * Cloudlets and VMs submitted to broker  

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/scfp2.PNG)

* Shortest Job First 1
   * Cloudlets and VMs submitted to the broker
   * Cloudlets sorted based on the length
   * Cloudlets bound to VM as per FCFS policy.  

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/sjf1.PNG)

* Shortest Job First 2
   * Cloudlets sorted based on the length
   * Cloudlets and VMs submitted to the broker
   * Cloudlets bound to VM as per FCFS policy. 

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/sjf2.PNG)

* Shortest Job First 3
   * Cloudlets sorted based on the execution time.
   * Cloudlets and VMs submitted to the broker
   * Cloudlets bound to VM as per FCFS policy.

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/sjf3.PNG)

* Shortest Job First 4
   * Cloudlets and VMs submitted to the broker
   * Cloudlets sorted based on the length
   * Cloudlets bound to VM as per FCFS policy.

![alt text](https://github.com/abhijithremesh/SchedulingHeuristics/blob/main/images/sjf4.PNG)



