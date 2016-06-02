
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
* File: StockSortingReducer.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class represents the Reducer. It will read each StockTimeKey and its corresponding List
* and write it to the output.
*
*/
public class StockSortingReducer extends Reducer<StockTimeKey, Text, Text, Text> {

	
	@Override
	public void reduce(StockTimeKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		//Set the key output text
		Text keyOut = new Text(key.toString());
		
		Iterator<Text> it = values.iterator();
		while(it.hasNext()) {
			
			//Write the value to the output
			Text valueOut = new Text(it.next().toString());
			context.write(keyOut, valueOut);
			
		}
	}
}