

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	
	private String host = "localhost";
	private int port = 3306;
	private String database = "MicroclimateControl";
	private String user = "root";
	private String password = "htvgt794jj";		
	
	private Connection connection = null;
	
	public boolean CONNECTION_POSSIBLE = false;
	
	public DatabaseHandler() {
		openConnection();
	}
	
	public DatabaseHandler(String database) {
		setDatabase(database);
		openConnection();
	}
	
	public void setDatabase(String database) {
		this.database= database;
	}	
	
	public String getDatabase() {
		return this.database;
	}
	
	public boolean openConnection() {
		connection = makeConnection(this.host, this.port, this.database, this.user, this.password);
		
		if(connection != null) 
			return true;
		else
			return false;
	}
	
	public boolean closeConnection() {
		if(connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			return true;
		}
		else 
			return false;
			
	}
	
	public void insertData(String queryString) {
				
		if(connection != null) {				
			executeInsert(queryString);
			
		}
		
	}
	
	public ResultSet getData(String queryString) {
		
		if(connection != null) {				
			return executeSelect(queryString);
		}
		return null;
	}
	
	private boolean executeInsert(String query) {
		try {  
			if(connection != null) {
				Statement cs = connection.createStatement();					
				cs.execute(query); 								
				
				return true;
			}
			else
				return false;
		} catch (SQLException e) {
			System.out.println(e.toString());
            return false;
		}
	}
	
	private ResultSet executeSelect(String selectQuery) {
		
		try {            			
			Statement cs = connection.createStatement();								 
			ResultSet result = cs.executeQuery(selectQuery); 	
			
			return result;
		} catch (SQLException e) {
			System.out.println(e.toString());
			return null;
		}				
	}
	
	private static boolean driverTest() {
		try {
            Class.forName("org.gjt.mm.mysql.Driver");
            //System.out.println("MySQL Driver Found");
            return true;
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println(e.toString());
            return false;
    	} 
	}
	
	private static Connection makeConnection(String host, int port, String database, String user, String password) {
		
		String url = "";
        try {
        	//System.out.println("Trying to establish database connection.....");
        	url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        	//url = "jdbc:mysql://" + host + "/" + database;
            Connection con = DriverManager.getConnection(url, user, password);
            //System.out.println("Connection established to " + url + "...");
        	return con;
        } catch (java.sql.SQLException e) {
        	System.out.println(e.toString());        
        }
		
		return null;
	}
}

