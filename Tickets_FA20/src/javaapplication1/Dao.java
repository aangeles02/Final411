package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createDeleteTable = "CREATE TABLE deletedticketstable01(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_reason VARCHAR (200))";
		final String createTicketsTable = "CREATE TABLE aange01_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), ticket_start_date VARCHAR(200), ticket_end_date VARCHAR(200))";
		final String createUsersTable = "CREATE TABLE aange_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			//statement.executeUpdate(createTicketsTable);
			//statement.executeUpdate(createUsersTable);
			//statement.executeUpdate(createDeleteTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into aange_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc, String sDate) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into aange01_tickets" + "(ticket_issuer, ticket_description,ticket_start_date ) values(" + " '"
					+ ticketName + "','" + ticketDesc +"','"+sDate+ "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}
	
	

	public int insertRecordstoDelete(String ticketIssuer,String ticketReason) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into deletedticketstable01" + "(ticket_issuer, ticket_reason) values(" + "'"+ticketIssuer+"','"+ticketReason+"')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	
	
	public ResultSet readRecords(boolean isAdmin, String ticketName) {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			if (isAdmin == true)
			results = statement.executeQuery("SELECT * FROM aange01_tickets");
			else
				results = statement.executeQuery("SELECT * FROM aange01_tickets WHERE ticket_issuer ='"+ ticketName +"'");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	// continue coding for updateRecords implementation
	public void updateRecords(boolean isAdmin,String username,String ticketnum, String ticketDesc) {  
		try {
			statement = getConnection().createStatement();
			int Ud = statement.executeUpdate("UPDATE aange01_tickets SET ticket_description ='"+ticketDesc+"' WHERE ticket_id ='"+ ticketnum+"'");
			
			if (Ud != 0)
				System.out.println("Ticket Number: "+ ticketnum+" was updated");
			else
				System.out.println("Ticket Number: "+ ticketnum+" is invaild");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// continue coding for deleteRecords implementation
	public void deleteRecords(String ticketnum) {
		try {
			statement = getConnection().createStatement();
			int vNum = statement.executeUpdate("DELETE FROM aange01_tickets WHERE ticket_id ="+ ticketnum);
			
			if (vNum != 0)
				System.out.println("Ticket Number: "+ticketnum+" was deleted and was added to deleted records table");
			else
				System.out.println("Ticket Number: "+ticketnum+" is invaild");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void closeRecords(String ticketnum, String ticketeDate) {
		try {
			statement = getConnection().createStatement();
			int eD = statement.executeUpdate("UPDATE aange01_tickets SET ticket_end_date ='"+ticketeDate+"' WHERE ticket_id ='"+ ticketnum+"'");
			if (eD !=0 ) 
				System.out.println("Ticket Number: "+ticketnum+"was closed on: "+ ticketeDate);
			else
				System.out.println("Ticket Number: "+ticketnum+ " is invaild");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ResultSet readRecordsDeleted() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM deletedticketstable01");

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
}
