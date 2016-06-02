import java.util.Vector;


public class UserData {
	
	private int userId;
	
	private Vector<Vector<Integer>> locationIds;
	private Vector<Vector<Integer>> locationFrequency;
	private Vector<Vector<Double>> locationX;
	private Vector<Vector<Double>> locationY;

	public UserData(int userId) {
		this.userId = userId;
		
		locationIds = new Vector<Vector<Integer>>(0,1);
		locationFrequency = new Vector<Vector<Integer>>(0,1);
		locationX = new Vector<Vector<Double>>(0,1);
		locationY = new Vector<Vector<Double>>(0,1);
		
		for(int i=0;i<9;i++) {
			locationIds.add(new Vector<Integer>(0,1));
			locationFrequency.add(new Vector<Integer>(0,1));
			locationX.add(new Vector<Double>(0,1));
			locationY.add(new Vector<Double>(0,1));
		}
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Vector<Integer> getLocationIds(int floor) {
		return locationIds.get(floor-1);
	}

	public void setLocationIds(int floor,Vector<Integer> locationIds) {
		//System.out.println(floor);
		
		this.locationIds.set(floor-1,locationIds);
	}
	
	public Vector<Integer> getLocationFrequency(int floor) {
		return locationFrequency.get(floor-1);
	}

	public void setLocationFrequency(int floor,Vector<Integer> locationFrequency) {
		this.locationFrequency.set(floor-1,locationFrequency);
	}
	
	public Vector<Double> getLocationX(int floor) {
		return locationX.get(floor-1);
	}
	
	public void setLocationX(int floor,Vector<Double> locationX) {
		this.locationX.set(floor-1,locationX);
	}
	
	public Vector<Double> getLocationY(int floor) {
		return locationY.get(floor-1);
	}
	
	public void setLocationY(int floor,Vector<Double> locationY) {
		this.locationY.set(floor-1,locationY);
	}
}
