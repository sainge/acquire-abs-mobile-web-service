package gov.abs.mobiledev.resteasy;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
import java.sql.*;

@Path("/acquiredataservice")
public class AcquireDataService {

	static boolean initialised = false;

	final static String queries[] = {
		"DROP TABLE IF EXISTS ADDRESS",
		"DROP TABLE IF EXISTS COLLECTOR",
		"DROP TABLE IF EXISTS RESPONSE",
		"DROP TABLE IF EXISTS RESPONSE_PERSON",

		"CREATE TABLE ADDRESS ( id integer PRIMARY KEY, address_text text, latitude real, longtitude real, status text, collector_id integer, FOREIGN KEY(collector_id) REFERENCES collector(id) )",
		"CREATE TABLE COLLECTOR ( id integer PRIMARY KEY, name text )",
		"CREATE TABLE RESPONSE ( address_id integer, photo blob, q1 integer, q2 text, q3 integer, FOREIGN KEY(address_id) REFERENCES address(id))",
		"CREATE TABLE RESPONSE_PERSON ( address_id integer, name text, age integer, work text, FOREIGN KEY(address_id) REFERENCES address(id) )",

		"INSERT INTO ADDRESS VALUES (1 , \"1 Example Street Adelaide SA 5000\", 111.11, 111.11, 'N', NULL)",
		"INSERT INTO ADDRESS VALUES (2 , \"2 Example Street Adelaide SA 5000\", 222.22, 222.22, 'N', NULL)",
		"INSERT INTO ADDRESS VALUES (3 , \"3 Example Street Adelaide SA 5000\", 333.33, 333.33, 'N', NULL)",
		"INSERT INTO ADDRESS VALUES (4 , \"4 Example Street Adelaide SA 5000\", 444.44, 444.44, 'N', NULL)",
		"INSERT INTO ADDRESS VALUES (5 , \"5 Example Street Adelaide SA 5000\", 555.55, 555.55, 'N', NULL)",

		"INSERT INTO COLLECTOR VALUES (1, \"Bruce Wayne\")",
		"INSERT INTO COLLECTOR VALUES (2, \"Alfred Pennyworth\")",
		"INSERT INTO COLLECTOR VALUES (3, \"Lucis Fox\")"
	};

	public void initialise() {

		//System.out.println("initialising");

		// load the sqlite-JDBC driver using the current class loader
		try{
			Class.forName("org.sqlite.JDBC");

			Connection connection = null;
			try
			{
				// create a database connection
				connection = DriverManager.getConnection("jdbc:sqlite:data.db");
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);  // set timeout to 30 sec.

				// path incorrect - look into this later
				//BufferedReader reader = new BufferedReader(new FileReader("/src/main/resources/initdb.txt"));

				//String line;

				//while((line = reader.readLine()) != null){
				// should delete tailing semi-colon from the file version if we implement it
				// statement.executeUpdate(line);
				//}

				for(int i = 0; i < queries.length; i++){
					statement.executeUpdate(queries[i]);
				}

			}
			catch(SQLException e)
			{
				// if the error message is "out of memory", 
				// it probably means no database file is found
				System.err.println(e.getMessage());
			}
			//catch(IOException e)
			//{			
			//	System.err.println(e);
			//}
			finally
			{
				try
				{
					if(connection != null){
						connection.close();
						initialised = true;
					}
				}
				catch(SQLException e)
				{
					// connection close failed.
					System.err.println(e);
				}
			}
		}catch(ClassNotFoundException e){
			System.err.println(e);
		}

	}

	@GET
	@Path("/requestaddress/{collector_id}")
	@Produces("application/json")
	@Mapped
	public AddressDTO requestaddress(@PathParam("collector_id") String collector_id) throws ClassNotFoundException{

		if(!initialised) initialise();

		AddressDTO dto = new AddressDTO();

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:data.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("SELECT id FROM address WHERE status = 'N' LIMIT 1");
			if(rs.next())
			{
				int address_id = rs.getInt("id");
				statement.executeUpdate("UPDATE address SET collector_id = " + collector_id + ", status = 'A' WHERE id = " + address_id);

				rs = statement.executeQuery("SELECT * FROM address WHERE id = " + address_id);

				while(rs.next())
				{
					dto.setId( rs.getInt("id"));
					dto.setAddressText(rs.getString("address_text"));
					dto.setLatitude(rs.getDouble("latitude"));
					dto.setLongitude(rs.getDouble("longtitude"));
					dto.setStatus(rs.getString("status"));
					dto.setCollectorId(rs.getInt("collector_id"));
				}
			}
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}

		return dto;

	}

	@GET
	@Path("/uploadresponse/{address_id}/{photo}/{q1}/{q2}/{q3}")
	@Produces("text/plain")
	public String uploadresponse(@PathParam("address_id") String address_id, 
			@PathParam("photo") String photo, 
			@PathParam("q1") String q1, 
			@PathParam("q2") String q2, 					
			@PathParam("q3") String q3) throws ClassNotFoundException{

		if(!initialised) initialise();

		String db = "";

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:data.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			String query = "INSERT INTO response VALUES (" + address_id + ", \"" + photo + "\", " + q1 + ", \"" + q2 + "\", " + q3 + ")";
			System.out.println(query);

			statement.executeUpdate(query);

			ResultSet rs = statement.executeQuery("SELECT * FROM response WHERE address_id = " + address_id);

			while(rs.next())
			{
				// read the result set
				db+= ("address_id = " + rs.getInt("address_id")) + "\n";
				db+= ("photo = " + rs.getString("photo")) + "\n";
				db+= ("q1 = " + rs.getInt("q1")) + "\n";
				db+= ("q2 = " + rs.getString("q2")) + "\n";
				db+= ("q3 = " + rs.getInt("q3")) + "\n";
			}

		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}

		return db;

	}

	@GET
	@Path("/uploadresponseperson/{address_id}/{name}/{age}/{work}")
	@Produces("text/plain")
	public String uploadresponseperson(@PathParam("address_id") String address_id, 
			@PathParam("name") String name, 
			@PathParam("age") String age, 
			@PathParam("work") String work) throws ClassNotFoundException{

		if(!initialised) initialise();

		String db = "";

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:data.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			String query = "INSERT INTO response_person VALUES (" + address_id + ", \"" + name + "\", " + age + ", \"" + work + "\")";
			System.out.println(query);

			statement.executeUpdate(query);

			ResultSet rs = statement.executeQuery("SELECT * FROM response_person WHERE address_id = " + address_id);

			while(rs.next())
			{
				// read the result set
				db+= ("address_id = " + rs.getInt("address_id")) + "\n";
				db+= ("name = " + rs.getString("name")) + "\n";
				db+= ("age = " + rs.getInt("age")) + "\n";
				db+= ("work = " + rs.getString("work")) + "\n";
			}

		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}

		return db;

	}


}
