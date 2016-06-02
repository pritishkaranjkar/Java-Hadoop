/**
 * This MapReduce program calculate word frequency for Enron Email corpus. Common stop words,
 * symbols, numbers are discarded. The output only shows words with >=10 frequency.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;




public class FindFrequentWords {
	public static ArrayList<String> stopWordsList;

	public static void loadStopWordsList(String path) throws FileNotFoundException{
		stopWordsList = new ArrayList<String>();
		Scanner s = new Scanner (new File(path));
		while (s.hasNextLine()){
			stopWordsList.add(s.nextLine().trim());
		}
	}



	// This method checks if a word is a stopword.Email addresses, numbers and symbols are also considered to be stopwords here. 
	public static boolean isStopWord(String s){
		if (s.contains("@")){
			return true;
		}
		if (!s.matches("[a-zA-Z]+")){
			return true;
		}
		for (int i =0; i<stopWordsList.size(); i++){
			if (s.toLowerCase().equals(stopWordsList.get(i))){
				return true;
			}
		}
		return false;
	}


	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String message = value.toString().toLowerCase();
			StringTokenizer tokenizer = new StringTokenizer(message);
			while (tokenizer.hasMoreTokens()){
				String temp = tokenizer.nextToken();
				if (!isStopWord(temp)){
					word.set(temp);
					context.write(word, one);
				}
			}
		}
	} 

	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text key, Iterable<IntWritable> values, Context context) 
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if (sum>10){
				context.write(key, new IntWritable(sum));
				}
		}
	}

	public static void main(String[] args) throws Exception {
		loadStopWordsList(args[0]);
		Configuration conf = new Configuration();

		Job job = new Job(conf, "FindFrequentWords");
		job.setJarByClass(FindFrequentWords.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(EmailInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		job.waitForCompletion(true);
	}
}
