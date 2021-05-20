# GoCJ: Google Cloud Jobs
* GoCJ java generator with which arbitrary number of cloudlets can be generated whose sizes are derived or based on GoCJ dataset.
* However, It does not generate inbuilt timestamps. But the generator can be modified to include randomly generated incremental time (in seconds).
* This time value can be upto 86400 which corresponds to the value of the last second of a day.
* Link: https://data.mendeley.com/datasets/b7bp6xhrcd/1


# SWIM Workload
* This trace file contains the following attributes
    * new_unique_job_id
    * submit_time_seconds
    * inter_job_submit_gap_seconds
    * map_input_bytes
    * shuffle_bytes
    * reduce_output_bytes
* submit_time_seconds to be used as the submission delay for cloudlets ?
* Size of cloudlet = map_input_bytes + shuffle_bytes + reduce_output_bytes
* Link: https://github.com/SWIMProjectUCB/SWIM/wiki/Workloads-repository


