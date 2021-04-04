## Steps

* ShortestCloudletFastestProcessingElement (SCFP) ([Efficient Task Scheduling Algorithms for Cloud Computing Environment](https://www.researchgate.net/publication/292149231_Efficient_Task_Scheduling_Algorithms_for_Cloud_Computing_Environment))

  *	Sort the cloudlets in ascending order of length.
  *	Sort the PEs across all the hosts in descending order of processing power. 
  *	Create virtual machines in the sorted list of PEs by packing as many VMs as possible in the fastest PE.
  *	Map the cloudlets from the sorted list to the created VM.
