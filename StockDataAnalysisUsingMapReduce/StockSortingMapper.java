
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
* File: StockSortingMapper.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class represents the Mapper. It taked a <LongWritable, Text> as input and writes
* <StockTimeKey, Text> as the output. It parses the input looking for the particular strings
* ""|44=", "|55=", "|54=", they represent: Price, Side and Stock respectively. Also the time
* of the transaction is stored.
* 
*
*/
public class StockSortingMapper extends Mapper<LongWritable, Text, StockTimeKey, Text> {

	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		String line = value.toString();
		int pricePosSt, pricePosEnd, sidePosSt, sidePosEnd, stockPosSt, stockPosEnd;
		//Search in the input line
		if(line.contains("|44=") && line.contains("|55=") && line.contains("|54=")){
			
			//Substring the line for the correct values
			String timestamp = line.substring(0, 15);
			pricePosSt = line.indexOf("|44=");
			pricePosEnd = line.indexOf("|",pricePosSt+1);
			sidePosSt = line.indexOf("|54=");
			sidePosEnd = line.indexOf("|", sidePosSt+1);
			stockPosSt = line.indexOf("|55=");
			stockPosEnd = line.indexOf("|",stockPosSt+1);
			
			String symbol = line.substring(stockPosSt + 4, stockPosEnd);
			String side = line.substring(sidePosSt + 4, sidePosEnd);
			Text price = new Text(line.substring(pricePosSt + 4, pricePosEnd));

			//Create the new key element
			StockTimeKey stockKey = new StockTimeKey(symbol, timestamp, side);
		
			context.write(stockKey, price);
		}
	}
}