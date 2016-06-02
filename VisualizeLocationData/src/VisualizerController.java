import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


public class VisualizerController extends JPanel{
	
	protected static JComboBox floorList;
	protected static JComboBox userList;
	protected static JComboBox modeList;
	protected static JComboBox dayList;
	protected static JComboBox timeslotChooser;
	protected static JLabel output;
	
	private DatabaseHandler dbHandler;
		    
	private static final long serialVersionUID = 1L;
    private BufferedImage image;
    private JPanel canvas;
    
    private static boolean guiLoaded = false;
    
    private JButton changeFloor;       
    
    private String[] floors = {"ghc1","ghc2","ghc3","ghc4","ghc5","ghc6","ghc7","ghc8"};
    private String season = "fall";

    private String currentMode;
    
    private String currentFloor = "ghc1";   
    private double[] currentActualLocation = {0,0};
    private double[] currentPredictedLocation = {0,0};
    
    private boolean updateRequested;
    
    private UserData[] users;
    private Vector<Integer> userIds;
    
    public VisualizerController() {
    	dbHandler = new DatabaseHandler("WiFi_Data");
    	
    	addUserData();
    	
        try {
            this.image = ImageIO.read(new File("maps/ghc1.jpg"));
        }catch(IOException ex) {
            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

        this.canvas = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D ga = (Graphics2D)g;
                ga.drawImage(image, 0, 0, null);
                
                
                if(currentMode.equals("locations")) {
                	Color currentColor = new Color(255,0,0);;
                    double x = 0;
                    double y = 0;
                	
	        		currentColor = new Color(0,0,255);
	        		
	        		ResultSet locations = dbHandler.getData("SELECT * FROM LocationTag WHERE z='ghc" + (floorList.getSelectedIndex()+1) + "'");
                    
                    try {
    					locations.beforeFirst();
    					
    					while(locations.next()) {
    						x = locations.getDouble("x");
    						y = locations.getDouble("y");
    						
    						Shape circle = new Ellipse2D.Float((float)x,(float)y, 15.0f, 15.0f);
    						//Shape rect = new  Rectangle2D.Float((float)x,(float)y, 17.0f, 17.0f);
    		                ga.draw(circle);
    		                ga.setPaint(currentColor);
    		        		ga.fill(circle);    		        		    		        		
    					}
                    } catch (SQLException e) {
    					System.out.println(e.toString());
    				}
                    
                }
                else if (currentMode.equals("userData")){
                		
                		int requestedUser = userIds.get(userList.getSelectedIndex());
                		
                		int uID = 0;
                		
                		for(int j=0;j<users.length;j++) {                			
                			if(users[j].getUserId() == requestedUser)
                				uID = j;
                		}
                		
                		Vector<Double> locationX = users[uID].getLocationX((floorList.getSelectedIndex()+1));
                		Vector<Double> locationY = users[uID].getLocationY((floorList.getSelectedIndex()+1));
                		Vector<Integer> locationFrequency = users[uID].getLocationFrequency((floorList.getSelectedIndex()+1));
                	
                		//System.out.println(userList.getSelectedIndex() + " " + (floorList.getSelectedIndex()+1));
                		
	                	Color currentColor = new Color(0,0,255);
	                    double x = 0;
	                    double y = 0;
	                    int frequency = 0;
	                    
	                    Shape circle = null;
	                    
	                    for(int i=0;i<locationX.size();i++) {
	                    	x = locationX.get(i);
	                    	y = locationY.get(i);
	                    	frequency = locationFrequency.get(i);
	                    	
	                    	circle = new Ellipse2D.Float((float)x,(float)y, 17.0f, 17.0f);
	                    	ga.setPaint(currentColor);
	                    	ga.draw(circle);			                
			        		ga.fill(circle);
			        		ga.setPaint(new Color(255,0,0));
			        		ga.drawString("" + frequency,(float)x-3.0f,(float)y+15.0f);
	                    }
	                    	                    
                	}
                	else {
                		System.out.println(currentActualLocation[0] + " " + currentActualLocation[1]);
                		System.out.println(currentPredictedLocation[0] + " " + currentPredictedLocation[1]);
                		
                		if(currentActualLocation[0] > 0) {
                			double x = currentActualLocation[0];
                			double y = currentActualLocation[1];
                			
                			Color currentColor = new Color(0,0,255);
                			
                			Shape circle = new Ellipse2D.Float((float)x,(float)y, 17.0f, 17.0f);
			                ga.draw(circle);
			                ga.setPaint(currentColor);
			        		ga.fill(circle);
			        		
			        		if(currentActualLocation[0] == currentPredictedLocation[0]) {
			        			x = currentPredictedLocation[0];
	                			y = currentPredictedLocation[1];
			        			
	                			currentColor = new Color(0,255,0);
	                			
			        			circle = new Ellipse2D.Float((float)x,(float)y, 17.0f, 17.0f);
				                ga.draw(circle);
				                ga.setPaint(currentColor);
				        		ga.fill(circle);
			        		}
			        		else {
			        			x = currentActualLocation[0] + 17.0f;
	                			y = currentActualLocation[1];
			        			
	                			currentColor = new Color(255,0,0);
	                			
			        			circle = new Ellipse2D.Float((float)x,(float)y, 17.0f, 17.0f);
				                ga.draw(circle);
				                ga.setPaint(currentColor);
				        		ga.fill(circle);
			        		}
                		}
                		else {
                			if(currentPredictedLocation[0] > 0) {
                				double x = currentPredictedLocation[0];
	                			double y = currentPredictedLocation[1];
			        			
	                			Color currentColor = new Color(0,255,0);
	                			
			        			Shape circle = new Ellipse2D.Float((float)x,(float)y, 17.0f, 17.0f);
				                ga.draw(circle);
				                ga.setPaint(currentColor);
				        		ga.fill(circle);
                			}
                		}
                	}
                               
            }
        };
        
        Box control = Box.createVerticalBox();
        
        changeFloor = new JButton("Change Floor");
        changeFloor.addActionListener(new ActionHandler());
        
        control.add(chooseModeList());
        control.add(createFloorList());                
                
        control.add(Box.createVerticalStrut(20));
        control.add(createUserList());
        control.add(createDayList());
        control.add(createTimeslotChooser());
        
        control.add(Box.createVerticalStrut(30));
        
        output = new JLabel("");
        output.setPreferredSize(new Dimension(600,30));
        output.setAlignmentX(Component.CENTER_ALIGNMENT);
        output.setAlignmentY(Component.TOP_ALIGNMENT);
        
        control.add(output);
        
        canvas.add(control);
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        JScrollPane sp = new JScrollPane(canvas);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        
        currentMode = "locations";
        updateRequested = true;
        
//        Vector<Double> locationX = users[4].getLocationX(8);
//        Vector<Double> locationY = users[4].getLocationY(8);
//        Vector<Integer> locationIds = users[4].getLocationIds(8);
//        
//        for(int i=0;i<locationIds.size();i++) {
//        	System.out.println(locationIds.get(i) + " " + locationX.get(i) + " " + locationY.get(i));
//        }
    }

    private void addUserData() {
    	users = new UserData[8];
    	
    	for(int i=1;i<=7;i++)
    		users[i-1] = new UserData(i);
    	
    	users[7] = new UserData(10);
    	
    	System.out.println(users[7].getUserId());
    	
    	for(int i=1;i<=7;i++) {
    		for(int j=1;j<=9;j++) {
	    		double x = 0;
	            double y = 0;
	            
	            int loc = 0;
	            int count = 0;
	            
	            Vector<Integer> locationIds = new Vector<Integer>(0,1);
	            Vector<Integer> locationFrequency = new Vector<Integer>(0,1);
	            Vector<Double> locationX = new Vector<Double>(0,1);
	            Vector<Double> locationY = new Vector<Double>(0,1);
	            
	            ResultSet locations = dbHandler.getData("SELECT location,COUNT(*) as frequency FROM IndoorLocationData WHERE userId=" + i + " AND floor='ghc" + j + "' GROUP BY location ORDER BY location DESC");
	            
	            try {
					locations.beforeFirst();
					
					while(locations.next()) {
						
						loc = locations.getInt("location");
						count = locations.getInt("frequency");
						
						locationIds.add(loc);
						locationFrequency.add(count);
						
						ResultSet coordinates = dbHandler.getData("SELECT x,y FROM LocationTag WHERE identifier=" + loc);
						
						coordinates.beforeFirst();
						
						while(coordinates.next()) {
							x = coordinates.getDouble("x");
							y = coordinates.getDouble("y");
						}					
						
						locationX.add(x);
						locationY.add(y);
					}
					
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
	            
	            users[i-1].setLocationIds(j,locationIds);
	            users[i-1].setLocationFrequency(j,locationFrequency);
	            users[i-1].setLocationX(j,locationX);
	            users[i-1].setLocationY(j,locationY);
    		}
    	}
    	
    	for(int j=1;j<=9;j++) {
    		double x = 0;
            double y = 0;
            
            int loc = 0;
            int count = 0;
            
            Vector<Integer> locationIds = new Vector<Integer>(0,1);
            Vector<Integer> locationFrequency = new Vector<Integer>(0,1);
            Vector<Double> locationX = new Vector<Double>(0,1);
            Vector<Double> locationY = new Vector<Double>(0,1);
            
            ResultSet locations = dbHandler.getData("SELECT location,COUNT(*) as frequency FROM IndoorLocationData WHERE userId=" + 10 + " AND floor='ghc" + j + "' GROUP BY location ORDER BY location DESC");
            
            try {
				locations.beforeFirst();
				
				while(locations.next()) {
					
					loc = locations.getInt("location");
					count = locations.getInt("frequency");
					
					locationIds.add(loc);
					locationFrequency.add(count);
					
					ResultSet coordinates = dbHandler.getData("SELECT x,y FROM LocationTag WHERE identifier=" + loc);
					
					coordinates.beforeFirst();
					
					while(coordinates.next()) {
						x = coordinates.getDouble("x");
						y = coordinates.getDouble("y");
					}					
					
					locationX.add(x);
					locationY.add(y);
				}
				
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
            
            users[7].setLocationIds(j,locationIds);
            users[7].setLocationFrequency(j,locationFrequency);
            users[7].setLocationX(j,locationX);
            users[7].setLocationY(j,locationY);
		}
    }
    
    private JComboBox chooseModeList() {
    	ActionHandler handleAction = new ActionHandler();
		
		modeList = new JComboBox() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5336148051311798132L;

			/** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }

        };
		        
        modeList.setPreferredSize(new Dimension(200,30));
        modeList.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeList.setAlignmentY(Component.TOP_ALIGNMENT);
        modeList.setActionCommand("changeMode");
        modeList.addActionListener(handleAction);
        
        modeList.addItem("Visualize Locations");
        modeList.addItem("Visualize User Data");      
        modeList.addItem("Visualize Predictions");
		
        modeList.setSelectedIndex(0);
        
        return modeList;
    }       
    
    private JComboBox createFloorList() {
		
		ActionHandler handleAction = new ActionHandler();
		
		floorList = new JComboBox() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5336148051311798132L;

			/** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }

        };
		        
        floorList.setPreferredSize(new Dimension(200,30));
        floorList.setAlignmentX(Component.CENTER_ALIGNMENT);
        floorList.setAlignmentY(Component.TOP_ALIGNMENT);
        floorList.setActionCommand("changeFloor");
        floorList.addActionListener(handleAction);
        
        floorList.addItem("GHC FLoor 1");
        floorList.addItem("GHC FLoor 2");
        floorList.addItem("GHC FLoor 3");
        floorList.addItem("GHC FLoor 4");
        floorList.addItem("GHC FLoor 5");
        floorList.addItem("GHC FLoor 6");
        floorList.addItem("GHC FLoor 7");
        floorList.addItem("GHC FLoor 8");
        floorList.addItem("GHC FLoor 9");
		
        floorList.setSelectedIndex(0);
        
        return floorList;
	}
    
    private JComboBox createDayList() {
		
		ActionHandler handleAction = new ActionHandler();
		
		dayList = new JComboBox() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5336148051311798132L;

			/** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }

        };
		        
        dayList.setPreferredSize(new Dimension(200,30));
        dayList.setAlignmentX(Component.CENTER_ALIGNMENT);
        dayList.setAlignmentY(Component.TOP_ALIGNMENT);        
        dayList.setActionCommand("changeDay");
        dayList.addActionListener(handleAction);
        
        ResultSet days = dbHandler.getData("SELECT DISTINCT date FROM PPM_prediction_" + season + " WHERE userId=" + (userList.getSelectedIndex() + 1)  + " ORDER BY date");
        
        try {
			days.beforeFirst();
			
			while(days.next()) {
				dayList.addItem(days.getString("date"));
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
		
        dayList.setVisible(false);
        dayList.setSelectedIndex(0);
        
        return dayList;
	}
    
    private JComboBox createUserList() {
		
		ActionHandler handleAction = new ActionHandler();
		
		userList = new JComboBox() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5336148051311798132L;

			/** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }

        };
		        
        userList.setPreferredSize(new Dimension(200,30));
        userList.setAlignmentX(Component.CENTER_ALIGNMENT);
        userList.setAlignmentY(Component.TOP_ALIGNMENT);
        userList.setActionCommand("changeUser");
        userList.addActionListener(handleAction);        
        
        ResultSet users = dbHandler.getData("SELECT DISTINCT userId FROM PPM_prediction_" + season);
        
        userIds = new Vector<Integer>(0,1);
        
        try {
			users.beforeFirst();
			
			while(users.next()) {
				userList.addItem("User " + users.getInt("userId"));
				userIds.add(users.getInt("userId"));
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
                
        userList.setSelectedIndex(0);
        userList.setVisible(false);
        
        return userList;
	}
    
    private JComboBox createTimeslotChooser() {
    	ActionHandler handleAction = new ActionHandler();
    	
    	timeslotChooser = new JComboBox() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5336148051311798132L;

			/** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }

        };
                
        timeslotChooser.setPreferredSize(new Dimension(200,30));
        timeslotChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeslotChooser.setAlignmentY(Component.TOP_ALIGNMENT);
        timeslotChooser.setActionCommand("chooseTimeslot");
        timeslotChooser.addActionListener(handleAction);
        
        for(int i=0;i<144;i++)
        	timeslotChooser.addItem(i);
        
        timeslotChooser.setSelectedIndex(0);
        timeslotChooser.setVisible(false);
        
        return timeslotChooser;
    }
    
    private void updateDayList() {
    	dayList.removeAllItems();
    	
    	ResultSet days = dbHandler.getData("SELECT DISTINCT date FROM PPM_prediction_" + season + " WHERE userId=" + (userList.getSelectedIndex() + 1)  + " ORDER BY date");
        
        try {
			days.beforeFirst();
			
			while(days.next()) {
				dayList.addItem(days.getString("date"));
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
    }
    
    private void updateFloor() {
    	int timeslot = (Integer)timeslotChooser.getSelectedItem();
		String currentDay = (String) dayList.getSelectedItem();
		
		int currentLocation = -2;
		int predictedLocation = -2;
		
		System.out.println("Selected Timeslot: " + timeslot);		
		
		ResultSet locations = dbHandler.getData("SELECT actualLocation,predictedLocation FROM PPM_prediction_" + season + " WHERE userId=" + (userList.getSelectedIndex() + 1) + " AND date='" + currentDay + "' AND timeslot10=" + timeslot);
        
        try {
			locations.beforeFirst();
			
			while(locations.next()) {
				currentLocation = locations.getInt("actualLocation");
				predictedLocation = locations.getInt("predictedLocation");
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
		
        System.out.println("Location: " + currentLocation);
        
        ResultSet floor = dbHandler.getData("SELECT * FROM LocationTag WHERE identifier=" + currentLocation);
        
        try {
			floor.beforeFirst();
			
			while(floor.next()) {
				currentFloor = floor.getString("z");
				currentActualLocation[0] = floor.getDouble("x");
				currentActualLocation[1] = floor.getDouble("y");
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
        
        floor = dbHandler.getData("SELECT * FROM LocationTag WHERE identifier=" + predictedLocation);
        
        try {
			floor.beforeFirst();
			
			while(floor.next()) {				
				currentPredictedLocation[0] = floor.getDouble("x");
				currentPredictedLocation[1] = floor.getDouble("y");
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
        
        if(currentLocation == -2) {
        	output.setText("No data available for this timeslot!");
        	currentActualLocation[0] = -2;
			currentActualLocation[1] = -2;
			currentPredictedLocation[0] = -2;
			currentPredictedLocation[1] = -2;
        }
        
        if(currentLocation == -1)
        	if(predictedLocation == -1) {
        		output.setText("User is outside of the building for this timeslot! Algorithm predicted outside!");
        		currentActualLocation[0] = -2;
    			currentActualLocation[1] = -2;
    			currentPredictedLocation[0] = -2;
    			currentPredictedLocation[1] = -2;
        	}
        	else {
        		output.setText("User is outside of the building for this timeslot! Algorithm predicted: " + predictedLocation);
        		currentActualLocation[0] = -2;
    			currentActualLocation[1] = -2;
    			
    			if(predictedLocation <= 0) {
    				currentPredictedLocation[0] = -2;
        			currentPredictedLocation[1] = -2;
    			}
    				
        	}
        
        if(currentLocation == 0)
        	if(predictedLocation == 0) {
        		output.setText("User is moving between locations! Algorithm predicted movement!");
        		currentActualLocation[0] = -2;
    			currentActualLocation[1] = -2;
    			currentPredictedLocation[0] = -2;
    			currentPredictedLocation[1] = -2;
        	}
        	else {
        		output.setText("User is moving between locations! Algorithm predicted: " + predictedLocation);
        	
        		currentActualLocation[0] = -2;
    			currentActualLocation[1] = -2;
    			
    			if(predictedLocation <= 0) {
    				currentPredictedLocation[0] = -2;
        			currentPredictedLocation[1] = -2;
    			}
        	}
        
        if(currentLocation > 0) {
        	output.setText("Actual Location: " +  currentLocation + " PredictedLocation: " + predictedLocation);
        
        	if(predictedLocation <= 0) {
				currentPredictedLocation[0] = -2;
    			currentPredictedLocation[1] = -2;
			}
        }
    }
    
    class ActionHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if(guiLoaded) {
				if(arg0.getActionCommand().equals("changeMode")) {
					if(modeList.getSelectedIndex() == 0) {
						currentMode = "locations";
						
						floorList.setVisible(true);
						
						userList.setVisible(false);	
						dayList.setVisible(false);						
						timeslotChooser.setVisible(false);
						output.setVisible(false);
						
						try {
				            image = ImageIO.read(new File("maps/ghc" + (floorList.getSelectedIndex()+1) + ".jpg"));
				        } catch(IOException ex) {
				            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
				        	System.out.println(ex.toString());
				        }
						
						updateRequested = true;
						canvas.repaint();
					}
					else if (modeList.getSelectedIndex() == 1){
							currentMode = "userData";
							
							userList.setVisible(true);
							floorList.setVisible(true);
							
							dayList.setVisible(false);							
							timeslotChooser.setVisible(false);
							output.setVisible(false);
							
							
							try {
					            image = ImageIO.read(new File("maps/ghc" + (floorList.getSelectedIndex()+1) + ".jpg"));
					        }catch(IOException ex) {
					            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
					        	System.out.println(ex.toString());
					        }
							
							updateRequested = true;
							canvas.repaint();
						}
						else {
							currentMode = "predictionData";
							
							userList.setVisible(true);	
							dayList.setVisible(true);							
							timeslotChooser.setVisible(true);
							output.setVisible(true);
							
							floorList.setVisible(false);														
							
							timeslotChooser.setSelectedIndex(0);
							userList.setSelectedIndex(0);
							dayList.setSelectedIndex(0);
							
							updateDayList();
							updateFloor();
					        
							try {
					            image = ImageIO.read(new File("maps/" + currentFloor + ".jpg"));
					        }catch(IOException ex) {
					            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
					        	System.out.println(ex.toString());
					        }
							
							updateRequested = true;
							canvas.repaint();
						}
				}
				
				if(arg0.getActionCommand().equals("changeUser")) {
					//currentMode = "userData";
					
					timeslotChooser.setSelectedItem(0);
					
					if(currentMode.equals("predictionData")) {
						updateDayList();
						updateFloor();
					}
										
					try {
			            image = ImageIO.read(new File("maps/ghc" + (floorList.getSelectedIndex()+1) + ".jpg"));
			        }catch(IOException ex) {
			            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
			        	System.out.println(ex.toString());
			        }
					
					updateRequested = true;
					canvas.repaint();
				}
				
				if(arg0.getActionCommand().equals("changeFloor")) {					
					try {
			            image = ImageIO.read(new File("maps/ghc" + (floorList.getSelectedIndex()+1) + ".jpg"));
			        }catch(IOException ex) {
			            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
			        	System.out.println(ex.toString());
			        }
					
					updateRequested = true;
					canvas.repaint();
				}
				
				if(arg0.getActionCommand().equals("chooseTimeslot")) {					
					updateFloor();
			        
					try {
			            image = ImageIO.read(new File("maps/" + currentFloor + ".jpg"));
			        }catch(IOException ex) {
			            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
			        	System.out.println(ex.toString());
			        }
					
					updateRequested = true;
					canvas.repaint();
				}
				
				if(arg0.getActionCommand().equals("changeDay")) {										
					
					System.out.println("Changing Day");					
			        timeslotChooser.setSelectedItem(0);			       
			        updateFloor();
			        
					try {
			            image = ImageIO.read(new File("maps/" + currentFloor + ".jpg"));
			        }catch(IOException ex) {
			            //Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
			        	System.out.println(ex.toString());
			        }										
					
					updateRequested = true;
					canvas.repaint();
				}
			}
			
		}
		
	}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel p = new VisualizerController();
                JFrame f = new JFrame();
                f.setContentPane(p);
                f.setSize(1400, 800);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
                
                guiLoaded = true;
            }
        });
    }
}