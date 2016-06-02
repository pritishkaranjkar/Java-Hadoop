import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
* File: NaturalKeyPartitioner.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class partition the mapping result into different reducers based on the symbol.
* It is important that all transactions for one symbols are handled by the same reducer
*
*/
public class NaturalKeyPartitioner extends Partitioner<StockTimeKey, Text> {

	@Override
	public int getPartition(StockTimeKey key, Text val, int numPartitions) {
		int hash = key.getSymbol().hashCode(); //Obtain the partition number based on the symbol
		int partition = hash % numPartitions;
		return partition;
	}

}