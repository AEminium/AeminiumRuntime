package aeminium.runtime.implementations.implicitworkstealing.profiler.analyzer;

import java.io.*;
import java.sql.*;

public class TotalTimeExtractor {

	private static String RAW_FILENAME = "total_times.txt";
	private static int numberExp = 19;

	public static void main(String[] args) {
		
		RAW_FILENAME = "total_times_" + numberExp + ".txt";
		// TODO Auto-generated method stub

		Connection connection = null;
		try {
			// Load the JDBC driver
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);

			// Create a connection to the database
			String serverName = "127.0.0.1";
			String portNumber = "1521";
			String sid = "orcl";
			String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber
					+ ":" + sid;
			String username = "aeminium";
			String password = "oracle";
			connection = DriverManager.getConnection(url, username, password);

			if (connection == null)
				System.out
						.println("There was some problem while connecting to the database.");

			/* Does all the work. */
			executeQuery(connection, getAllParametersQuery());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void executeQuery(Connection conn, String query)
			throws Exception {
		File file = new File(RAW_FILENAME);
		Writer output = new BufferedWriter(new FileWriter(file));

		// Create a Statement
		Statement stmt = conn.createStatement();

		// Select the ENAME column from the EMP table
		ResultSet rset = stmt.executeQuery(query);
		
		
		
		while (rset.next()) {

			try
			{
			String contents = Integer.parseInt(rset.getString(1)) + "\t"
						+ Integer.parseInt(rset.getString(2)) + "\t"
							+ Double.parseDouble(rset.getString(3)) + "\n";
			
			output.write(contents);
			} catch (Exception e)
			{
				System.out.println("Ignoring value.");
			}
			
			

		}

		rset.close();
		rset = null;
		
		// Close the Statement
		stmt.close();
		stmt = null;
		
		
		output.close();
	}

	public static String getAllParametersQuery() {
		// 21 parameters
		return "SELECT info.id_exp, info.id_run, total_time "
				+ "FROM info "
				+ "WHERE info.id_exp = " + numberExp + " "
				+ "ORDER BY total_time";
	}
}
