
## Steps

* OLB ([Dynamic Mapping of a Class of Independent Tasks onto Heterogeneous Computing Systems](https://d1wqtxts1xzle7.cloudfront.net/46749291/Dynamic_Matching_and_Scheduling_of_a_Cla20160623-32119-ghlt76.pdf?1466746234=&response-content-disposition=inline%3B+filename%3DDynamic_matching_and_scheduling_of_a_cla.pdf&Expires=1617551610&Signature=aALwJuvfEXHuMQVyKdkFan5bGBEDXxqAt15OcB0xLU2Ccdm2duvJdf0iyUke~fWPzMKiaeGTm-fpP6DlJP-VhtzI805QV1WCJkC34xjVwu3eI6V3gMsIX0yKSRR47DbIhG3iRl4jplKjY0j7MYQ2WnEYHJAhBLCjcpfUS1rLu~gV2BamPCGOqeu4aBOSIE~X6FKSfsNfh~QH~8AHO-ZBI9FC6np7N7qs3uQbbAaVbcNl3224w4aV-awYawJhEwKK7jZKGJXn4PtxpV2R2P3-hvwnyeRk9Ppz1EYIburGw7jMRb7GcCchASga8mruHuXhmQHNRMc-ZXUl3qIscqP9XQ__&Key-Pair-Id=APKAJLOHF5GGSLRBV4ZA))

  * For each cloudlet in the cloudletlist, find the VM that becomes ready next.
  * While assigning the cloudlet to the VM, it's execution time on that VM not considered.
  * If multiple VMs becomes ready at the same time while assigning, then one machine is arbitrary chosen.
  * Iterate for all the remaining tasks.
