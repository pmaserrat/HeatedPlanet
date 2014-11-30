package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import EarthSim.EarthGrid;


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
		
		PreparedStatement stmt = null;
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			//stmt = conn.createStatement();
			String simsetname = name + "SS";
			String invsetname = name + "IS";
			String physicalname = name + "P";
			String gridname = name + "G";
//			String sql = "INSERT into SIMULATION values ('"
//					+ name + "','" + simsetname + "','" + invsetname
//					+ "','" + physicalname + "','" + gridname + ")";
			StringBuffer sql = new StringBuffer("insert into SIMULATION values(?,?,?,?,?)");
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, name);
			stmt.setString(2, simsetname);
			stmt.setString(3, invsetname);
			stmt.setString(4, physicalname);
			stmt.setString(5, gridname);
					
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
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
		}
	}
	
	public void addPhysical(String name, int tilt, double eccentricity){
		String physname = name + "P";
		PreparedStatement stmt = null;
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
//			stmt = conn.createStatement();
//			String sql = "INSERT into PHYSTICALFACTORS (" + physname 
//					+ "," + tilt + "," + eccentricity + ")";
			StringBuffer sql = new StringBuffer("insert into PHYSICALFACTORS values(?,?,?)");
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, physname);
			stmt.setInt(2, tilt);
			stmt.setDouble(3, eccentricity);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
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
		}
		
	}
	
	public void addSimSettings(String name, int spacing, int timeStep, int length){
		String settingsname = name + "SS";
		PreparedStatement stmt = null;
		
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			//stmt = conn.createStatement();
//			String sql = "INSERT into SIMULATIONSETTINGS ("
//					+ settingsname + "," + spacing + "," + timeStep
//					+ "," + length + ")";
			StringBuffer sql = new StringBuffer("insert into SIMULATIONSETTINGS values(?,?,?,?)");
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, settingsname);
			stmt.setInt(2, spacing);
			stmt.setInt(3, timeStep);
			stmt.setInt(4, length);
			stmt.executeUpdate();
		} catch (SQLException e) {
			
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
		}
	}
	
	public void addInvSettings(String name, int precision, int geographic, int temporal){
		String invname = name + "IS";
		PreparedStatement stmt = null;
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
//			stmt = conn.createStatement();
//			String sql = "INSERT into INVOCATIONSETTINGS ("
//					+ invname + "," + precision + "," + geographic + ","
//					+ temporal + ")";
			
			StringBuffer sql = new StringBuffer("insert into INVOCATIONSETTINGS values(?,?,?,?)");
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, invname);
			stmt.setInt(2, precision);
			stmt.setInt(3, geographic);
			stmt.setInt(4, temporal);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			
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
		}
		
	}
	
	public void addGrid(String name, Calendar date, Calendar time, int x, int y, double temp){
		String gridname = name + "G";
		Statement stmt = null;
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "INSERT into GRID ("
					+ gridname + "," + date + "," + time + "," + x + ","
					+ y + "," + temp + ")";
					
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			
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
		}
	}
	
	public String readSimulations(String name, int tilt, double eccentricity){
		Statement stmt = null;
		String result = "";
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "SELECT * from SIMULATION where name=" + name; 
					
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				sql = "SELECT * from PHYSICALFACTORS where name=" + name
				+ "P and axialtilt=" + tilt + "and eccentricity=" + eccentricity;
				ResultSet rs1 = stmt.executeQuery(sql);
				while(rs1.next()){
					result += rs.getString("name") + " " + rs.getString("axialtilt") 
							+ " " + rs.getString("eccentricity");
					
					
				}
				
			}
		} catch (SQLException e) {
			
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
		}
		return result;
	}
	
	public String readQuery(String query){
		Statement stmt = null;
		String result = "";
		
		try {
			conn = DriverManager.getConnection(DBS_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = query; 
					
			ResultSet rs = stmt.executeQuery(sql);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
				while(rs.next()){
					for (int i = 0; i<rsmd.getColumnCount(); i++)
						result += rs.getString(i);
					result += "/n";
				}
				
			
		} catch (SQLException e) {
			
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
		}
		return result;
	}

	public void addGrid(String name, EarthGrid grid) {
		// TODO Auto-generated method stub
		String gridname = name + "G";
		PreparedStatement stmt = null;
		double gridtemp[][] = grid.getTempGrid();
		for (int i = 0; i < grid.getTempGridHeight();i++){
			for(int j = 0; j < grid.getTempGridWidth(); i++){
		
				try {
					conn = DriverManager.getConnection(DBS_URL, USER, PASS);
		//			stmt = conn.createStatement();
		//			String sql = "INSERT into GRID ("
		//					+ gridname + "," + date + "," + time + "," + x + ","
		//					+ y + "," + temp + ")";
					
					Calendar cal = Calendar.getInstance();
					Date day = cal.getTime();
					String date = day.toString();
					Long timel = cal.getTimeInMillis();
					String time = timel.toString();
					int x = (int)grid.getPosX();
					int y = (int)grid.getPosY();
					double temp = gridtemp[i][j];
					StringBuffer sql = new StringBuffer("insert into GRID values(?,?,?,?,?,?)");
					stmt = conn.prepareStatement(sql.toString());
					stmt.setString(1, gridname);
					stmt.setString(2, date);
					stmt.setString(3, time);
					stmt.setInt(4, x);
					stmt.setInt(5, y);
					stmt.setDouble(6, temp);
					stmt.executeUpdate();
				} catch (SQLException e) {
					
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
				}
			}
		}
	}
}
