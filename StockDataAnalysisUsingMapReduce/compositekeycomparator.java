import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
* File: CompositeKeyComparator.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class compare the key members, used for sorting after the shuffling step.
*
*/
public class CompositeKeyComparator extends WritableComparator {

	/**
	 * Constructor.
	 */
	protected CompositeKeyComparator() {
		super(StockTimeKey.class, true);
	}
	
	
	//Sort first by Symbol, then Side and then time
	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		StockTimeKey k1 = (StockTimeKey)w1;
		StockTimeKey k2 = (StockTimeKey)w2;
		
		int result;
		//First sorting
		result = k1.getSymbol().compareTo(k2.getSymbol());
		if(result == 0){//Secondary sorting
			result = ((Integer) k1.getSide()).compareTo((Integer)k2.getSide());
			
			if(result == 0){ //Third Sorting
				result = (k1.getTime()).compareTo(k2.getTime());
			}
		}
		return result;
	}
}