package gov.abs.mobiledev.resteasy;


//import javax.naming.InitialContext;
import javax.naming.NamingException;
//import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
import java.sql.*;

@Path("/acquiredataservice")
public class AcquireDataService {

	@GET
	@Path("/requestaddress/{username}/{password}")
	@Produces("application/json")
	@Mapped
	public AddressDTO requestaddress(@PathParam("username") String username, @PathParam("password") String password) throws ClassNotFoundException{

		AddressDTO dto = new AddressDTO();

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			
			String collector_id = null;
			
			if(username.equals(password)){
				collector_id = username;
			}else{
				return dto;
			}

			// create a database connection
			javax.naming.Context ctx = new javax.naming.InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource)
					ctx.lookup("java:comp/env/jdbc/AcquireMobileDevDB");
			connection = ds.getConnection();

			// create statement and execute queries and updates
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			ResultSet rs;
			int address_id = -1;
			
			rs = statement.executeQuery("SELECT id FROM address WHERE status = 'A' and collector_id = " + collector_id);
			if (rs.next()){
				address_id = rs.getInt("id");
			}
			else
			{
				rs = statement.executeQuery("SELECT id FROM address WHERE status = 'N' LIMIT 1");
				if(rs.next())
				{
					address_id = rs.getInt("id");
					statement.executeUpdate("UPDATE address SET collector_id = " + collector_id + ", status = 'A' WHERE id = " + address_id);
				}
			}

			if (address_id > -1)
			{
				rs = statement.executeQuery("SELECT * FROM address WHERE id = " + address_id);

				if(rs.next())
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
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@POST
	@Path("/uploadresponse/{address_id}/{photo}/{q1}/{q2}/{q3}")
	public void uploadresponse(@PathParam("address_id") String address_id, 
			@PathParam("photo") String photo, 
			@PathParam("q1") String q1, 
			@PathParam("q2") String q2, 					
			@PathParam("q3") String q3) throws ClassNotFoundException{

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// create a database connection
			javax.naming.Context ctx = new javax.naming.InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource)
					ctx.lookup("java:comp/env/jdbc/AcquireMobileDevDB");
			connection = ds.getConnection();

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			String query = "INSERT INTO response VALUES (" + address_id + ", \"" + photo + "\", " + q1 + ", \"" + q2 + "\", " + q3 + ")";
			statement.executeUpdate(query);
			
			statement.executeUpdate("update address set status='S' where id=" + address_id);

			// ALSO NEED TO UPDATE THE STATUS OF THE ADDRESS
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

	@POST
	@Path("/uploadresponseperson/{address_id}/{name}/{age}/{work}")
	public void uploadresponseperson(@PathParam("address_id") String address_id, 
			@PathParam("name") String name, 
			@PathParam("age") String age, 
			@PathParam("work") String work) throws ClassNotFoundException{

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// create a database connection
			javax.naming.Context ctx = new javax.naming.InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource)
					ctx.lookup("java:comp/env/jdbc/AcquireMobileDevDB");
			connection = ds.getConnection();

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			String query = "INSERT INTO response_person VALUES (" + address_id + ", \"" + name + "\", " + age + ", \"" + work + "\")";
			System.out.println(query);

			statement.executeUpdate(query);

		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

	@GET
	@Path("/alldata")
	@Produces("text/plain")
	public String alldata() throws ClassNotFoundException{

		Connection connection = null;
		
		String alladdresses = "";
		
		try
		{
			
			// create a database connection
			javax.naming.Context ctx = new javax.naming.InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource)
					ctx.lookup("java:comp/env/jdbc/AcquireMobileDevDB");
			connection = ds.getConnection();

			// create statement and execute queries and updates
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("SELECT * FROM address");
			while(rs.next())
			{
				alladdresses += "id: " + rs.getInt("id") + "\n";
				alladdresses += "address_text: " + rs.getString("address_text") + "\n";
				alladdresses += "latitude: " + rs.getDouble("latitude") + "\n";
				alladdresses += "longtitude: " + rs.getDouble("longtitude") + "\n";
				alladdresses += "status: " + rs.getString("status") + "\n";
				alladdresses += "collector_id: " + rs.getInt("collector_id") + "\n";
			}

			rs = statement.executeQuery("SELECT * FROM response");
			while(rs.next()){
				
				alladdresses += "\taddress_id: " + rs.getInt("address_id") + "\n";
				alladdresses += "\tphoto: " + rs.getString("photo") + "\n";
				alladdresses += "\tq1: " + rs.getInt("q1") + "\n";
				alladdresses += "\tq2: " + rs.getString("q2") + "\n";
				alladdresses += "\tq3: " + rs.getInt("q3") + "\n";					
			}
			
			rs = statement.executeQuery("SELECT * FROM response_person");
			while(rs.next()){
				
				alladdresses += "\t\taddress_id: " + rs.getInt("address_id") + "\n";
				alladdresses += "\t\tname: " + rs.getString("name") + "\n";
				alladdresses += "\t\tage: " + rs.getDouble("age") + "\n";
				alladdresses += "\t\twork: " + rs.getString("work") + "\n";
			}
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return alladdresses;
	}
}
