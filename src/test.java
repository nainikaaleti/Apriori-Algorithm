import java.sql.*;

class test { //PreparedStatement Query Example
	public static void main(String[] Args) {

		String url = "sql1.njit.edu";
		String ucid = "mr572";	//your ucid
		String dbpassword = "jogging64";	//your MySQL password


		System.out.println("This example program uses a PreparedStatement to Query.");

		System.out.println("Starting test . . .");
		
		
		System.out.println("Loading driver . . .");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			System.err.println("Unable to load driver.");
			e.printStackTrace();
		}
		System.out.println("Driver loaded.");
		System.out.println("Establishing connection . . . ");
		try {
			Connection conn;
	 
			conn = DriverManager.getConnection("jdbc:mysql://"+url+"/"+ucid+"?user="+ucid+"&password="+dbpassword);
			
			System.out.println("Connection established.");			
			System.out.println("Creating a Statement object . . . ");

			Statement stmt = conn.createStatement();
			System.out.println("Statement object created.");
			stmt.executeUpdate("DROP TABLE IF EXISTS user");
			System.out.println("Old table dropped (if it existed).");

			System.out.println("Creating a table . . .");
			stmt.executeUpdate("CREATE TABLE user("+
				"idnum INT NOT NULL AUTO_INCREMENT, "+
				"PRIMARY KEY(idnum), "+
				"userid VARCHAR(100) UNIQUE NOT NULL, "+
				"fullname VARCHAR(100) NOT NULL, "+
				"email VARCHAR(100) NOT NULL, "+
				"notes TEXT)");
			System.out.println("Table created.");

			System.out.println("Inserting data in table . . .");

			stmt.executeUpdate("INSERT INTO user (userid, fullname, email, notes) "+
				"VALUES(\"john\", \"john smith\", \"john@njit.edu\", \"blah, blah . . \")");
			stmt.executeUpdate("INSERT INTO user (userid, fullname, email, notes) "+
				"VALUES(\"jane\", \"jane doe\", \"jane@njit.edu\", \"blah, blah . . \")");
			stmt.executeUpdate("INSERT INTO user (userid, fullname, email, notes) "+
				"VALUES(\"zip\", \"zip zippy\", \"zippy@njit.edu\", \"blah, blah . . \")");
			stmt.executeUpdate("INSERT INTO user (userid, fullname, email, notes) "+
				"VALUES(\"theo\", \"Ted Nicholson\", \"theo@njit.edu\", \"Whatever . . . \")");
			System.out.println("Inserted data.");

			
			
			
			System.out.println("Querying table  . . . ");
			PreparedStatement queryusers = conn.prepareStatement("Select * from user where userid=?");
			queryusers.setString(1, "theo");
			ResultSet rs = queryusers.executeQuery();
			
			while (rs.next()) {
				System.out.print(rs.getString("idnum")+"\t"+rs.getString("userid")+"\t"+rs.getString("fullname")+"\t"+
					rs.getString("email")+"\t"+rs.getString("notes"));
				System.out.println();			}

			rs.close();
			stmt.close();
			conn.close();
			System.out.println("If you see one row of data above, the test example sucessfully completed.");
		}
		catch (SQLException E) {
			System.out.println("SQLException: " + E.getMessage());
			System.out.println("SQLState:     " + E.getSQLState());
			System.out.println("VendorError:  " + E.getErrorCode());
		}
	}	
}

