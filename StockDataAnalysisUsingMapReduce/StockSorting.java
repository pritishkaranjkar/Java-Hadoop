

import java.util.Date;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
* File: StockSorting.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class is the main driver program. It requires two inputs:
* <input path>
* <output path>
* It sets up the job and executes it.
* The execution time is shown after the execution is finished.
*
*/
public class StockSorting {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: StockSorting <input path> <output path>");
			System.exit(-1);
		}
		
		//Create the Job
		Job job = new Job();
		job.setJarByClass(StockSorting.class);
		job.setJobName("Stock Sorting");
		
		//Set the partitioner and the comparator
		//Send all the same stock registries to the same reducer
		job.setPartitionerClass(NaturalKeyPartitioner.class);
		//Decides the order of the output
		job.setSortComparatorClass(CompositeKeyComparator.class);
		
		//Set the input and output paths
		Path inputDir = new Path(args[0]);
		Path outputDir = new Path(args[1]);
		FileInputFormat.addInputPath(job, inputDir);
		FileOutputFormat.setOutputPath(job, outputDir);

		//Set the Mapper and Reducer classes
		job.setMapperClass(StockSortingMapper.class);
		job.setReducerClass(StockSortingReducer.class);
		
		//Set the Mapper and Reducer key types
		job.setMapOutputKeyClass(StockTimeKey.class);
		job.setOutputKeyClass(StockTimeKey.class);
		
		//Calculate execution time
		long start = new Date().getTime();
		System.out.println("Execution succesful => " + job.waitForCompletion(true));            
		long end = new Date().getTime();
		System.out.println("Job took "+(end-start) + " milliseconds");
		   
	}
}
