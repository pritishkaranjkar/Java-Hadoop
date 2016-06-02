/**
 * This MapReduce program helps to identify potential suspects of Enron scandal by going through the
 * Enron email corpus and counting how many times each employee receives or sents an email that contains 
 * one suspicious word. 
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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class FindPotentialSuspect {


	public static ArrayList<String> suspiciousWordList;

	public static void loadSuspisiousWordList(String path) throws FileNotFoundException{
		suspiciousWordList = new ArrayList<String>();
		Scanner s = new Scanner (new File(path));
		while (s.hasNextLine()){
			suspiciousWordList.add(s.nextLine().trim());
		}
	}

	public static String extractSenderEmail(String s){
		
		 if (s.indexOf("From:")!=-1 && s.indexOf("From:")<111 && s.indexOf("To:")!=-1 && s.indexOf("To:")<170 ){
			 String senderEmail;
				int indexPoint1 = s.indexOf("From:");
				int indexPoint2 = s.indexOf("To:",indexPoint1);
				 senderEmail = s.substring(indexPoint1+5,indexPoint2).trim();
				if (senderEmail.contains("<")){
					senderEmail = senderEmail.substring(senderEmail.indexOf("<")+1,senderEmail.indexOf(">"));
				}	
				return senderEmail;	
		 }
		 else
			 return null;
	}

	public static String[] extractReceiverEmail(String message){
		if (message.indexOf("To:")!=-1 && message.indexOf("Subject:")!=-1 && message.indexOf("To:") <  150 ){
			int indexPoint1 = message.indexOf("To:");
			int indexPoint2 = message.indexOf("Subject:",indexPoint1);
			if(indexPoint2>indexPoint1){
				String receivers = message.substring(indexPoint1+3,indexPoint2);
				if (receivers.contains(",")){
					String[] receiverEmails = receivers.split(",");
					for (int i =0; i<receiverEmails.length;i++){
						String temp  = receiverEmails[i].trim();
						if (temp.contains("<")){
							receiverEmails[i] = temp.substring(temp.indexOf("<")+1, temp.indexOf(">"));
						}		
					}
					return receiverEmails;
				}
				else {
					if (receivers.contains("<")){
						receivers = receivers.substring(receivers.indexOf("<")+1, receivers.indexOf(">"));
					}
					String[] result = {receivers};
					return result;
				}
			}
		}
		return null;
	}

	public static boolean isSuspicious(String s){
		for (int i =0; i<suspiciousWordList.size(); i++){
			if (s.toLowerCase().trim().equals(suspiciousWordList.get(i))){
				return true;
			}
		}
		return false;
	}


	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		Text suspect = new Text();

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String message = "Message-ID:"+value.toString();
			StringTokenizer tokenizer = new StringTokenizer(message);
			String sender = extractSenderEmail(message);
			String[] receivers = extractReceiverEmail(message);
			while (tokenizer.hasMoreTokens()){
				String temp = tokenizer.nextToken();
				if (isSuspicious(temp)){
					if (sender!=null){
						suspect.set(sender);
						context.write(suspect, one);
					}

					if (receivers!=null){
						for (int i =0; i<receivers.length;i++){
							suspect.set(receivers[i]);
							context.write(suspect, one);
						}
					}
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
			context.write(key, new IntWritable(sum));

		}
	}

	public static void main(String[] args) throws Exception {
		loadSuspisiousWordList(args[0]);
		Configuration conf = new Configuration();

		Job job = new Job(conf, "FindSuspiciousEmployee");
		job.setJarByClass(FindPotentialSuspect.class);
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

