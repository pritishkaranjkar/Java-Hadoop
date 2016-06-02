import java.util.Vector;


public class DailyData {

	private int userId;
	
	private Vector<String> days;
	private Vector<Vector<Double>> locationX;
	private Vector<Vector<Double>> locationY;
	private Vector<Vector<String>> locationZ;
	private Vector<Vector<Integer>> timeslots;
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Vector<String> getDays() {
		return days;
	}
	
	public void setDays(Vector<String> days) {
		this.days = days;
	}
	
	public Vector<Double> getLocationX(int day) {
		return locationX.get(day);
	}
	
	public void setLocationX(int day,Vector<Double> locationX) {
		this.locationX.set(day,locationX);
	}
	
	public Vector<Double> getLocationY(int day) {
		return locationY.get(day);
	}
	
	public void setLocationY(int day,Vector<Double> locationY) {
		this.locationY.set(day,locationY);
	}
	
	public Vector<String> getLocationZ(int day) {
		return locationZ.get(day);
	}
	
	public void setLocationZ(int day,Vector<String> locationZ) {
		this.locationZ.set(day,locationZ);
	}
	
	public Vector<Integer> getTimeslot(int day) {
		return timeslots.get(day);
	}
}
