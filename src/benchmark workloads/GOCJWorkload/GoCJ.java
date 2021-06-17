package org.cloudsimplus.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class GoCJ  {

    private static Map<Integer,Long> dataTable;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws NumberFormatException, IOException {
        Scanner scaner = new Scanner(System.in);
        System.out.println("Number of jobs required in GoCJ Dataset: ");
        int NoJobs = Integer.parseInt(scaner.nextLine());
        createGoCJ(NoJobs);
    }


    protected static long[] createGoCJ(int num) throws NumberFormatException, IOException
    {
        dataTable = new HashMap<Integer,Long>();
        Random random = new Random();
        long jobSizes[] = new long[num];

        double jobTime[] = new double[num];

        int per = 0;
        //Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples
        FileReader in = new FileReader("Z:/Cloudsim/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/Original_DataSet.txt");
        BufferedReader br = new BufferedReader(in);
        long size = 0;
        String chk;
        while ((chk = br.readLine()) != null) {
            System.out.println("chk"+chk);
            size = Long.parseLong(chk);
            System.out.println("size"+size);
            dataTable.put(per,size);
            //System.out.println(per + "    " + dataTable.get(per));   // This line can be used to print the dataTable
            per += 2;
        }
        br.close();



        for (int i=0;i<num;i++)
        {
            int  rand = random.nextInt(100);
            jobSizes[i] = (rand%2 == 0)? dataTable.get(rand): getJobSize(rand);
        }

        int low = 0;
        int high = 2;

        for(int i =0;i<num;i++){
            Random r = new Random();
            double result = r.nextInt(high-low)+low;
            jobTime[i] = result;
            low=low+5;
            high = high+5;
        }

        //System.out.println("Lengths of Jobs");
        //for (int i=1; i<=num;i++)
        //    System.out.println("Job." + i + " Size: " + jobSizes[i-1] + " MIs Time: "+jobTime[i-1]);
            //System.out.println("Job." + i + " Size: " + jobSizes[i-1] + " MIs" );

        return jobSizes;
    }

    private static long getJobSize(int rnd)
    {
        long jsize = 0;
        for(int i=0; i<100 ; i=i+2)
        {
            if (rnd > i)
                jsize = dataTable.get(i);
        }
        return jsize;
    }
}
