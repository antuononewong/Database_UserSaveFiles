package User.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/* Script that establishes a database connection, adds/removes user 
 * save files from the database, and closes the same connection.
 */

public class DatabaseHandler {
	
	// Database credentials
	private String userName = "root";
	private String password = "pw";
	private String serverName = "localhost";
	private String portNumber = "3306";
	
	// Database connection
	private Connection connection = null;
	
	// Attempt to connect to database based on stored credentials.
	public void getConnection() throws SQLException {
		Properties properties = new Properties();
		properties.put("user", userName);
		properties.put("password", password);
		
		String connectionInput = "jdbc:mysql://" + serverName + ":" + portNumber + "/";
		connection = DriverManager.getConnection(connectionInput, properties);
	}
	
	// Close database connection to release any used database resources
	public void closeConnection() throws SQLException {
		connection.close();
	}
	
	// Add save file to database based on parameters.
	// Sample query - INSERT INTO save_files (username, hp, mana, credits, checkpoint, quest) 
	//				  VALUES (username, hp, mana, credits, checkpoint, quest);
	// Database has auto-incrementing ID as primary key.
	public void addSaveFile(String username, int hp, int mana, int credits, String checkpoint, String quest) throws SQLException {
		Statement statement = null;
		String query = "INSERT INTO save_files (username, hp, mana, credits, checkpoint, quest) " + 
					   "VALUES (" + username + "," + Integer.toString(hp) + "," + Integer.toString(mana) +
					   "," + Integer.toString(credits) + "," + checkpoint + "," + quest + ");";
		
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
		}
		catch (Exception e) {
			printErrorStatement(e);
		}
		finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
	
	// Retrieve save file from database for appropriate user.
	// Sample query - SELECT * FROM save_files WHERE username = username;
	public void getSaveFile(String username) throws SQLException {
		Statement statement = null;
		String query = "SELECT * FROM save_files WHERE username = " + username + ";";
		
		try {
			statement = connection.createStatement();
			ResultSet set = statement.executeQuery(query);
			if (set != null) {
				processSaveFileSet(set);
			}
		}
		catch (Exception e) {
			printErrorStatement(e);
		}
		finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
	
	// Parse output from database when sending SELECT query 
	// to grab save file for specific user
	// ResultSet includes id, username, hp, mana, credits, checkpoint, quest
	private void processSaveFileSet(ResultSet set) throws SQLException{
		while (set.next()) {
			String username = set.getString("username");
			int hp = set.getInt("hp");
			int mana = set.getInt("mana");
			int credits = set.getInt("credits");
			String checkpoint = set.getString("checkpoint");
			String quest = set.getString("quest");
			System.out.println(username + " @ " + checkpoint + " with quest: " + quest + ". /nResources: " +
							   "HP: " + hp + " Mana: " + mana + " Credits: " + credits);
		}
	}
	
	private void printErrorStatement(Exception e) {
		System.out.println(e);
		System.out.println("Error trying to process SQL query.");
	}
}
