/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	//GLOBAL variable success used by all the functions we wrote
	public static boolean success = true;

	public static void AddPlane(DBproject esql) {//1
		int id = 0;
		String make = "";
		String model = "";
		int age = 0;
		int seats = 0;
		
		try{
			do {
				System.out.print("\tEnter plane make: $");
				make = in.readLine();
				if (make.length() == 0)
				{
					System.out.println("Please enter a valid make");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter plane model: $");
				model = in.readLine();
				if (model.length() == 0)
				{
					System.out.println("Please enter valid model");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while (!success);
			
			do {
				System.out.print("\tEnter age of the plane : $");
				age = Integer.parseInt(in.readLine());
				if (age == 0)
				{
					System.out.println("Please enter valid age");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while(!success);
			
			do {
				System.out.print("\tEnter number of seats on the plane: $");
				seats = Integer.parseInt(in.readLine());
				if (seats < 1 || seats > 499)
				{
					System.out.println("Please enter number of seats");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while(!success);
			
			String query = "INSERT INTO Plane (make, model, age, seats) " +
				"VALUES ('" + make + "', '" + model + "', " + age + "," + seats + ");";

            esql.executeUpdate(query);
            System.out.println ("Successfully inserted 1 Plane ");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void AddPilot(DBproject esql) {//2
		String name = "";
		String nationality = "";
		try{
			do {
				System.out.print("\tEnter pilot name: $");
				name = in.readLine();
				if (name.length() == 0)
				{
					System.out.println("Please enter a valid name");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter pilot nationality: $");
				nationality = in.readLine();
				if (nationality.length() == 0)
				{
					System.out.println("Please enter valid nationality");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while (!success);
			
			
			String query = "INSERT INTO Pilot (fullname, nationality) " +
				"VALUES ('" + name + "', '" + nationality + "');";

            esql.executeUpdate(query);
            System.out.println ("Successfully inserted 1 Pilot ");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		int cost = 0;
		int seatsSold = 0;
		int numStops = 0;
		String departureDate = "";
		String arrivalDate = "";
		String arrivalAirport = "";
	    String departureAirport = "";
		try{
			
			do {
				System.out.print("\tEnter flight cost: $");
				cost = Integer.parseInt(in.readLine());
				if (cost == 0)
				{
					System.out.println("Please enter a valid cost");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter number of seats sold on the flight: $");
				seatsSold = Integer.parseInt(in.readLine());
				if (seatsSold < 0)
				{
					System.out.println("Please enter valid number of seats sold");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while (!success);
			
			do {
				System.out.print("\tEnter number of stops for the flight: $");
				numStops = Integer.parseInt(in.readLine());
				if (numStops < 0)
				{
					System.out.println("Please enter valid number of stops");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while(!success);
			
			do {
				System.out.print("\tEnter flight departure Date (YYYY-MM-DD HR:MIN): $");
				departureDate = in.readLine();
				if (departureDate.length() == 0)
				{
					System.out.println("Please enter a valid departure Date");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter flight arrival Date (YYYY-MM-DD HR:MIN): $");
				arrivalDate = in.readLine();
				if (arrivalDate.length() == 0)
				{
					System.out.println("Please enter valid arrival Date");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while (!success);
			
			do {
				System.out.print("\tEnter flight arrival Airport: $");
				arrivalAirport = in.readLine();
				if (arrivalAirport.length() == 0)
				{
					System.out.println("Please enter a valid arrival Airport");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter flight departure Airport: $");
				departureAirport = in.readLine();
				if (departureAirport.length() == 0)
				{
					System.out.println("Please enter valid departure Airport");
					success = false;
				}
				else
				{ 
					success = true; 
					
				}
			} while (!success);
			
			String query = "INSERT INTO Flight (cost, num_sold, num_stops, actual_departure_date, " +
				"actual_arrival_date, arrival_airport, departure_airport) " +
				"VALUES (" + cost + "," + seatsSold + ", " + numStops + ", '" + departureDate 
				+ "', '" + arrivalDate + "', '" + arrivalAirport + "', '" + departureAirport + "');";

            esql.executeUpdate(query);
            System.out.println ("Successfully inserted 1 Flight");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void AddTechnician(DBproject esql) {//4
		try{
			String name = "";
			do {
				System.out.print("\tEnter flight Technician name: $");
				name = in.readLine();
				if (name.length() == 0)
				{
					System.out.println("Please enter a valid Technician name");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			String query = "INSERT INTO Technician (full_name) " +
                "VALUES ('" + name + "');";

            esql.executeUpdate(query);
            System.out.println ("Successfully added 1 Technician");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		try{
			int customerID = 0;
		    int flightID = 0;
			String status = "";
			
			do {
				System.out.print("\tEnter customer ID: $");
				customerID = Integer.parseInt(in.readLine());
				
				String query = "";
				query += "SELECT DISTINCT id FROM Customer WHERE id = \'";
				query += customerID + "\';";
				int rows = esql.executeQuery(query);
				if (rows == 0) {
					System.out.println("Customer ID don't exist.");
					success = false;
				}
				else {
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter flight number: $");
				flightID = Integer.parseInt(in.readLine());
				
				String query = "";
				query += "SELECT * FROM Flight WHERE fnum = \'"; 
				query += flightID + "\';";
				int rows = esql.executeQuery(query);
				if (rows == 0) {
					System.out.println("Flight number don't exist.");
					success = false;
				}
				else {
					success = true;
				}
				
			} while (!success);
			
			do {
				System.out.print("\tEnter status (C), (R), or (W): $");
				status = in.readLine();
				if (!(status.equals("C")) && !(status.equals("R")) && !(status.equals("W")))
				{
					System.out.println("Please enter a valid reservation status");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			// determine the status of the reservation (Waitlisted/Confirmed/Reserved)
			// if #seats_Sold < #seats -> STATUS = Reserved R
			// else if #seats_Sold >= #seats -> STATUS = Waitlisted W
			// if customerID already exists and status = Reserved -> STATUS = Confirmed C
			
			/*String query = "SELECT (P.seats - F.num_sold) ";
					query += "FROM Plane P, Flight F ";
					query += "WHERE pid = \'" + flightID + "\' AND fnum = \'" + flightID + "\' ";
					query += "WHERE F.flightNum = P.pid;";
			
			int rows = esql.executeQuery(query);
			if (rows > 0) {
				status = "R";
			}
			else{ status = "W";}*/
			
			
			String query = "INSERT INTO Reservation (cid, fid, status) " +
                "VALUES (" + customerID + "," + flightID + ", '" + status + "');";

            esql.executeUpdate(query);
            System.out.println ("Successfully added 1 Reservation");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
	    try{
			int flightID = 0;
		    String departureDate = "";
		    do {
				System.out.print("\tEnter flight number: $");
				flightID = Integer.parseInt(in.readLine());
				
				String query = "";
				query += "SELECT * FROM Flight WHERE fnum = \'"; 
				query += flightID + "\';";
				int rows = esql.executeQuery(query);
				if (rows == 0) {
					System.out.println("Flight number don't exist.");
					success = false;
				}
				else {
					success = true;
				}
				
			} while (!success);
			do {
				System.out.print("\tEnter flight departure Date (YYYY-MM-DD HR:MIN): $");
				departureDate = in.readLine();
				if (departureDate.length() == 0)
				{
					System.out.println("Please enter a valid departure Date");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			
			String query = "SELECT (Plane.seats - Flight.num_sold) AS SeatsAvailable FROM Flight " +
                "INNER JOIN Schedule ON Schedule.flightNum = Flight.fnum " +
                "INNER JOIN FlightInfo fi ON fi.flight_id = Flight.fnum " +
                "INNER JOIN Plane ON Plane.id = fi.plane_id " +
                "WHERE Flight.fnum = " + flightID + " AND Schedule.departure_time = '" + departureDate + "';";

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	    
	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		try{

			String query = "SELECT Plane.id, COUNT(Repairs.rid) AS RepairCount FROM Plane " +
                "INNER JOIN Repairs ON Repairs.plane_id = Plane.id " +
                "GROUP BY Plane.id " +
                "ORDER BY RepairCount DESC;";

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
		try{
			String query = "SELECT EXTRACT(YEAR FROM repair_date) AS Year, " +
			    "COUNT(rid) AS RepairCount FROM Repairs " +
                "GROUP BY EXTRACT(YEAR FROM repair_date) " +
                "ORDER BY RepairCount ASC;";

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		try{
		    int flightID = 0;
			String status = "";
			do {
				System.out.print("\tEnter flight number: $");
				flightID = Integer.parseInt(in.readLine());
				
				String query = "";
				query += "SELECT * FROM Flight WHERE fnum = \'"; 
				query += flightID + "\';";
				int rows = esql.executeQuery(query);
				if (rows == 0) {
					System.out.println("Flight number don't exist.");
					success = false;
				}
				else {
					success = true;
				}
				
			} while (!success);
			do {
				System.out.print("\tEnter reservation status ((C) confirmed, (R) reserved, or(W)aitlisted): $");
				status = in.readLine();
				if (!(status.equals("C")) && !(status.equals("R")) && !(status.equals("W")))
				{
					System.out.println("Please enter a valid reservation status");
					success = false;
				}
				else
				{
					success = true;
				}
				
			} while (!success);
			String query = "SELECT COUNT(Customer.id) FROM Customer " +
                "INNER JOIN Reservation ON Reservation.cid = Customer.id " +
                "WHERE Reservation.fid = " + flightID + " AND Reservation.status = '" + status + "';";

            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
	}
}
