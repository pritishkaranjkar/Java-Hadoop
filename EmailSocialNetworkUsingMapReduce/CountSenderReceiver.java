/**
 * This MapReduce program goes through the Enron email dataset, and count number of emails between each
 * unique sender-receiver pair. 
 * The output format of this program is:
 * From : [sender email address] + To:[receiver email address] + frequency
 */
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CountSenderReceiver {

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
		if (message.indexOf("To:")!=-1 && message.indexOf("Subject:")!=-1 && message.indexOf("To:") <  160 ){
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
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text senderReceiver = new Text();

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String message = "Message-ID:"+value.toString();
			String sender = extractSenderEmail(message);
			String[] receivers = extractReceiverEmail(message);
			if (sender!=null &&receivers!=null ){
				for(int i=0; i<receivers.length;i++){
					String output = "From: "+sender+" To: "+receivers[i];
					senderReceiver.set(output);
					context.write(senderReceiver, one);
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
		Configuration conf = new Configuration();

		Job job = new Job(conf, "CountSenderReceiver");
		job.setJarByClass(CountSenderReceiver.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(EmailInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}
