import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

/**
* File: StockTimeKey.java
* Creator: Hadoop Group 5
* Last Modified: 11/11/13
*
* This class members represent a composite key for a Mapper and Reducer.
* Holds the Symbol, Time and Side of a stock transaction.
*
*/
public class StockTimeKey  implements WritableComparable<StockTimeKey> {
	String symbol;
	Long time; //Time in miliseconds
	int side;
	
	//Empty Constructor
	StockTimeKey(){}
	
	//Constructor
	StockTimeKey(String symbol, String timeIn, String side){
		this.symbol = symbol;
		this.side = Integer.parseInt(side);
		//Parse the string into miliseconds
		String[] TimeIn_tokens = timeIn.split("[:.]");
		Long temp = Long.parseLong(TimeIn_tokens[0]) * 60 * 60 * 1000000; //Hours
		temp += Long.parseLong(TimeIn_tokens[1]) * 60 * 1000000; //Minutes
		temp += Long.parseLong(TimeIn_tokens[2]) * 1000000; //Seconds
		temp +=  Long.parseLong(TimeIn_tokens[3]); //Miliseconds
		this.time =  temp;
	}
	
	/**
	* Get methods
	*/
	public String getSymbol(){
		return symbol;
	}
	
	public Long getTime(){
		return time;
	}
	
	public int getSide(){
		return side;
	}
	
	public String getSymbolTime(){
		return symbol + time;
	}

	//To String method, used as the class output
	public String toString(){
		long temp = time; //So we don't modify the original value
		long hours = temp/60/60/1000000;
		temp -= hours*60*60*1000000;
		long minutes = temp/60/1000000;
		temp -= minutes*60*1000000;
		long seconds = temp/1000000;
		temp -= seconds*1000000;
		
		//Parse the time to be HH:MM:SS.mmmmmm
		String timeStr = stringRightMost( ("00" + hours), 2) + ":" + stringRightMost( ("00" + minutes), 2) + ":" + stringRightMost( ("00" + seconds), 2) + "." + temp;  
		
		return symbol + "\t" + side + "\t" + timeStr;
	}
	
	//Obtain the 2 right most characters of a string
	private static String stringRightMost(String str, int len){
		return str.substring(str.length() - len, str.length() );
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		symbol = WritableUtils.readString(in);
		time = in.readLong();
		side = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeString(out, symbol);
		out.writeLong(time);
		out.writeInt(side);
	}

	@Override
	public int compareTo(StockTimeKey o) {
		int result = symbol.compareTo(o.symbol);
		if(0 == result) {
			result = ((Integer)side).compareTo((Integer)side);
			if(0 == result){
				result = time.compareTo(o.time);
			}
		}
		return result;
	}
}
