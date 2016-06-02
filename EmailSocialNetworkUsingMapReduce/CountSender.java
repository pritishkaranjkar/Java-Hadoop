/**
 * This MapReduce program read through the input dataset, and count the number of emails each person sent.
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
        
public class CountSender {
	
	/*
	 * extractSenderEmail() extracts the sender's email addresses from each record.
	 * This mehtod also normalizes email address format.  
	 */
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
        
 public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text sender = new Text();
        
    
  // The map process obtains the sender's email address from each record, and send the result to the reduce process.
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String message = "Message-ID:"+value.toString();
        String senderEmail = extractSenderEmail(message);
        if (senderEmail!=null){
        	sender.set(senderEmail);
        	context.write(sender, one);
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
        
    Job job = new Job(conf, "CountSender");
    job.setJarByClass(CountSender.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(EmailInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path("/Users/Cassandra/Dataset/input"));
    FileOutputFormat.setOutputPath(job, new Path("/Users/Cassandra/Dataset/output"));
        
    job.waitForCompletion(true);
 }
        
}
