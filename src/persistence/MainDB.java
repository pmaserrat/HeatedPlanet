package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;


public class MainDB {
	 
    private static final String DB_URL = "jdbc:mysql://localhost";
    private static final String DBS_URL = "jdbc:mysql://localhost/SIMULATIONS";
 
    private static final String USER = "root";
 
    private static final String PASS = "root";
    private Connection conn;
    private Statement stmt;
    private Statement stmt1;
	
	public void dbConnection(){
		try{
		     
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      
		      //Create Database if non existent
		      System.out.println("Creating database...");
		      stmt = conn.createStatement();
		      
		      String sql = "CREATE DATABASE IF NOT EXISTS SIMULATIONS";
		      stmt.executeUpdate(sql);
		      
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }
	}
	
	public void createTables(){
		try{
		      
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(DBS_URL, USER, PASS);
		      
		      //Create main table with keys
		      stmt1 = conn.createStatement();
		      
		      String sql1 = "CREATE TABLE IF NOT EXISTS SIMULATION " +
		                   "(name VARCHAR(255) not NULL, " +
		                   " simsetname VARCHAR(255), " + 
		                   " invsetname VARCHAR(255), " + 
		                   " physicalname VARCHAR(255), " + 
		                   " gridname VARCHAR(255), " +
		                   " PRIMARY KEY ( name ))"; 

		      stmt1.executeUpdate(sql1);
		      System.out.println("Created simulation table");
		      
		    //Create main table with keys
		      stmt = conn.createStatement();
		      
		      String sql = "CREATE TABLE IF NOT EXISTS PHYSICALFACTORS " +
		                   "(name VARCHAR(255) not NULL, " +
		                   " axialtilt INTEGER, " + 
		                   " eccentricity DOUBLE) "; 

		      stmt.executeUpdate(sql);
		      System.out.println("Created physical factors table");
		      
		    //Create main table with keys
		      Statement stmt2 = conn.createStatement();
		      
		      String sql2 = "CREATE TABLE IF NOT EXISTS SIMULATIONSETTINGS " +
		                   "(name VARCHAR(255) not NULL, " +
		                   " gridspacing INTEGER, " + 
		                   " timestep INTEGER, " + 
		                   " SimulationLength INTEGER) "; 
		      System.out.println(sql2);

		      stmt2.executeUpdate(sql2);
		      System.out.println("Created sim settings table");
		      
		    //Create main table with keys
		      Statement stmt3 = conn.createStatement();
		      
		      String sql3 = "CREATE TABLE IF NOT EXISTS INVOCATIONSETTINGS " +
		                   "(name VARCHAR(255) not NULL, " +
		                   " precisions INTEGER, " + 
		                   " geographic INTEGER, " + 
		                   " temporal INTEGER) "; 
		      System.out.println(sql3);

		      stmt3.executeUpdate(sql3);
		      System.out.println("Created invocation table");
		      
		    //Create main table with keys
		      stmt = conn.createStatement();
		      
		      sql = "CREATE TABLE IF NOT EXISTS GRID " +
		                   "(name VARCHAR(255) not NULL, " +
		                   " readingdate VARCHAR(255), " + 
		                   " readingtime VARCHAR(255), " + 
		                   " latitude INTEGER, " + 
		                   " longitude INTEGER, " +
		                   " temperature DOUBLE) ";

		      stmt.executeUpdate(sql);
		      System.out.println("Created grid table");
		      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	// add the name to Simulation table and keys to other tables
	public void addSimulation(String name){
		
		Statement stmt = null;
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "INSERT into SIMULATION name=" + name + ",simsetname="
					+ name + "SS,invsetname=" + name + "IS,physicalname=" 
					+ name + "P,gridname=" + name + "G";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addPhysical(String name, int tilt, double eccentricity){
		
	}
	
	public void addSimSettings(String name, int spacing, int timeStep, int length){
		
	}
	
	public void addInvSettings(String name, int precision, int geographic, int temporal){
		
		
	}
	
	public void addGrid(String name, Calendar date, Calendar time, int lat, int lon, double temp){
		
	}
	
	public void readSimulations(String name, int tilt, double eccentricity){
		
	}
}
