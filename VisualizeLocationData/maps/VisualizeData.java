import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class VisualizeData extends JLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6500348134531243931L;
	
	Shape circle = new Ellipse2D.Float(100.0f, 100.0f, 100.0f, 100.0f);
	Shape redCircle = new Ellipse2D.Float(150.0f, 150.0f, 50.0f, 50.0f);
	
	private ImageIcon backgroundImage;
	private String floor;
	private int user;
	
	public VisualizeData(String floor, int user) {		
		this.setUser(user);
		this.setFloor(floor);
		
		displayData(floor,user);
	}
	
	public void displayData(String floor, int user) {
		ImageIcon image = new ImageIcon("maps/" + floor + ".jpg");
		this.backgroundImage = image;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D ga = (Graphics2D)g;
		ga.drawImage(backgroundImage.getImage(),0,0,null);
		ga.draw(circle);
		ga.setPaint(Color.green);
		ga.fill(circle);
		ga.draw(redCircle);
		ga.setPaint(Color.red);
		ga.fill(redCircle);
	}	

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}
}
