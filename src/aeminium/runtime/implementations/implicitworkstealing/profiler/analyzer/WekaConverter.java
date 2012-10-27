package aeminium.runtime.implementations.implicitworkstealing.profiler.analyzer;

import java.io.*;
import java.sql.*;

public class WekaConverter {

	private static String RAW_FILENAME = "raw.txt";
	private static String WEKA_CSV = "weka.csv";
	private static final int NO_EXPERIENCES = 19;
	private static final int NO_RUNS = 30;
	private static final int NO_FEATURES = 18;
	private static final int NO_FEATURES_CLEAN = NO_FEATURES - 2;

	/* There are 19 experiences, each one with 30 runs. */
	private static int lengthCounter[] = new int[NO_EXPERIENCES * NO_RUNS];

	public static void main(String[] args) {
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
			writeCSVFile(executeQuery(connection, getAllParametersQuery()));

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static int executeQuery(Connection conn, String query)
			throws Exception {
		File file = new File(RAW_FILENAME);
		Writer output = new BufferedWriter(new FileWriter(file));

		int counter = 0;
		int previousRunId = 1, currentRunId = 1, currentExpId = 1;
		int savedTaskNo = 0, savedTaskSize = 0;
		double savedTotalTime = 0;

		// Create a Statement
		Statement stmt = conn.createStatement();

		// Select the ENAME column from the EMP table
		ResultSet rset = stmt.executeQuery(query);

		// Iterate through the result and print the employee names
		boolean isToPrintComma = false;
		String contents;
		int perLineCounter = 0;
		int minPerLineCounter = Integer.MAX_VALUE;

		while (rset.next()) {
			contents = "";
			counter++;
			int i;

			currentRunId = Integer.parseInt(rset.getString(2));

			if (currentRunId != previousRunId) {
				contents = "," + savedTaskSize + "," + savedTaskNo + ","
						+ savedTotalTime + "\n";
				
				lengthCounter[(currentExpId - 1) * NO_RUNS + (previousRunId - 1)] = perLineCounter;

				previousRunId = currentRunId;
				isToPrintComma = false;
				
				if (perLineCounter < minPerLineCounter)
					minPerLineCounter = perLineCounter;

				perLineCounter = 0;
				currentExpId = Integer.parseInt(rset.getString(1));
			}

			perLineCounter++;

			if (isToPrintComma)
				contents += ",";

			/*
			 * 18 is the number of parameters collected from the samples table.
			 * The other three remain for the info table.
			 * We start in three, as we are ignoring the ID's.
			 */
			for (i = 3; i < NO_FEATURES; i++)
				contents += rset.getString(i) + ",";
			contents += rset.getString(i);

			savedTaskSize = Integer.parseInt(rset.getString(i + 1));
			savedTaskNo = Integer.parseInt(rset.getString(i + 2));
			savedTotalTime = Double.parseDouble(rset.getString(i + 3));

			if (counter % 1000 == 0)
				System.out.println(counter);

			output.write(contents);

			isToPrintComma = true;
		}

		contents = "," + savedTaskSize + "," + savedTaskNo + ","
				+ savedTotalTime + "\n";
		output.write(contents);
		
		lengthCounter[(currentExpId - 1) * NO_RUNS + (currentRunId - 1)] = perLineCounter;
		System.out.println("At " + ((currentExpId - 1) * NO_RUNS + (currentRunId - 1)) + " with " + perLineCounter);

		// Close the RseultSet
		rset.close();
		rset = null;

		// Close the Statement
		stmt.close();
		stmt = null;
		output.close();

		System.out.println("Min per line counter is " + minPerLineCounter);

		return minPerLineCounter;
	}

	public static String getAllParametersQuery() {
		// 21 parameters
		return "SELECT info.id_exp, info.id_run, at_completed, nbt_completed, bt_completed,"
				+ "t_completed, no_u_t, no_wd_t, no_wq_t, no_r_t, no_t_bq,"
				+ "cpu_load, runnable_threads, blocked_threads,threads_in_net_io,"
				+ "waiting_threads, memory_commited_size, memory_used_size, task_size, task_no, total_time "
				+ "FROM info, samples "
				+ "WHERE info.id_run = samples.id_run AND info.id_exp = samples.id_exp "
				+ "ORDER BY info.id_exp, info.id_run";
	}

	public static void writeCSVFile(int minPerLine) throws Exception
	{		
		DataInputStream in = new DataInputStream(new FileInputStream(RAW_FILENAME));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		File file = new File(WEKA_CSV);
		Writer output = new BufferedWriter(new FileWriter(file));
		
		String strLine;
		
		int counter = 0;
		
		/* Writes the header of the CSV file. */
		int s;
		for (s = 0; s < minPerLine - 1; s++)
		{
			output.write("at_completed_" + s + ","
					+ "nbt_completed" + s + ","
					+ "bt_completed" + s + ","
					+ "t_completed" + s + ","
					+ "no_u_t" + s + ","
					+ "no_wd_t" + s + ","
					+ "no_wq_t" + s + ","
					+ "no_r_t" + s + ","
					+ "no_t_bq" + s + ","
					+ "cpu_load" + s + ","
					+ "runnable_threads" + s + ","
					+ "blocked_threads" + s + ","
					+ "threads_in_net_io" + s + ","
					+ "waiting_threads" + s + ","
					+ "memory_commited_size" + s + ","
					+ "memory_used_size" + s + ",");
		}
		
		/* In the final round, we have to add the header for the task size, number and time. */
		output.write("at_completed_" + s + ","
				+ "nbt_completed" + s + ","
				+ "bt_completed" + s + ","
				+ "t_completed" + s + ","
				+ "no_u_t" + s + ","
				+ "no_wd_t" + s + ","
				+ "no_wq_t" + s + ","
				+ "no_r_t" + s + ","
				+ "no_t_bq" + s + ","
				+ "cpu_load" + s + ","
				+ "runnable_threads" + s + ","
				+ "blocked_threads" + s + ","
				+ "threads_in_net_io" + s + ","
				+ "waiting_threads" + s + ","
				+ "memory_commited_size" + s + ","
				+ "memory_used_size" + s + ","
				+ "task_size,"
				+ "task_no,"
				+ "total_time\n");
		
		// Read File Line By Line
		while ((strLine = br.readLine()) != null)
		{
			int noSets = lengthCounter[counter];
			int diff = noSets - minPerLine;
			int downsamplingSize = diff / minPerLine + 1;
			
			//System.out.printf("Having noSets: %d, downsampling: %d and diff: %d.\n", noSets, downsamplingSize, diff);

			String[] splitted = strLine.split(",");
			int len = splitted.length;
			
			/* We don't count the last three, relative to the info table. */
			for (int i = 0; i < minPerLine*NO_FEATURES_CLEAN; i += NO_FEATURES_CLEAN*downsamplingSize)
			{
				double means[] = new double[NO_FEATURES_CLEAN];
				
				for (int k = 0; k < downsamplingSize; k++)
				{
					for (int j = 0; j < NO_FEATURES_CLEAN; j++)
					{
						double number = Double.parseDouble(splitted[k*NO_FEATURES_CLEAN + j + i]);
						means[j] += number;
					}
				}
				
				/* Now, calculate the mean and write it into file. */
				for (int m = 0; m < NO_FEATURES_CLEAN; m++)
				{
					means[m] /= downsamplingSize;
					
					/* Position 9 corresponds to the CPU load, which must be a double. */
					if (m != 9)
						output.write((int)(means[m]) + ",");
					else
						output.write(means[m] + ",");
				}
			}
			
			counter++;
			/* Now, concludes the write of this line by appending the number of tasks, its size
			 * and the total time.
			 */
			output.write(splitted[len-3]+ "," + splitted[len-2] + "," + splitted[len-1]);
			output.write("\n");
		}
		
		// Closes the two streams.
		in.close();
		output.close();

	}
}
