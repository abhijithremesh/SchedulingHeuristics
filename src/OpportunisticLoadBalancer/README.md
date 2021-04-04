
## Steps

* OLB 
  * For each cloudlet in the cloudletlist, find the VM that becomes ready next.
  * While assigning the cloudlet to the VM, it's execution time on that VM not considered.
  * If multiple VMs becomes ready at the same time while assigning, then one machine is arbitrary chosen.
  * Iterate for all the remaining tasks.
