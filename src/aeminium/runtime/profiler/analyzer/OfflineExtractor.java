package aeminium.runtime.profiler.analyzer;

import java.io.*;

public class OfflineExtractor
{
	private final static int NO_TELEMETRY_GROUPS = 10;
	private final static int TOTAL_PARAMETERS = 13;
	private static String fullPath = "C:\\Users\\Ivo\\Desktop\\PIFinal\\";
	private static String filenamePrefix = "PIThreads_offline_run";
	private static String samplesTable = "samples";

	private static String justFileNameCSV;
	private static String justFileNameSQL;
	
	private static boolean isToAppend = false;
	private static boolean isLastExecution = false;
	
	private static int NO_EXPERIENCES = 19;
	private static int NO_RUNS = 30;
	
	
	public static void main(String[] args)
	{
		/* Arguments treatment. */
		if (args.length == 0 || args.length > 5)
		{
			System.out.println("Usage: java -jar Extractor.jar [NO_EXPERIENCES] [NO_RUNS] " +
									"[full path] [input file name prefix] [samples table name]");
			System.out.println("DEFAULT:\n" +
						"NO_EXPERIENCES = 19\n" +
						"NO_RUNS = 30\n" + 
						"INPUT FILE NAME PREFIX = 'PIThreads_offline_run'\n" + 
						"FULL PATH = 'C:\\Users\\Ivo\\Desktop\\PIFinal\\'\n" +
						"SAMPLES TABLE NAME = 'samples'");
			System.exit(0);
		}
		
		if (args.length >= 1)
			NO_EXPERIENCES = Integer.parseInt(args[0]);
		if (args.length >= 2)
			NO_RUNS = Integer.parseInt(args[0]);
		if (args.length >= 3)
			fullPath = args[2];
		if (args.length >= 4)
			filenamePrefix = args[3];
		if (args.length == 5)
			samplesTable = args[4];
		
		
		for (int k = 1; k <= NO_EXPERIENCES; k++)
		{
			for (int m = 1; m <= NO_RUNS; m++)
			{
				String filetemp = filenamePrefix + "_" + k + "_" + m + ".jps";
				
				String probeId = "aeminium.runtime.profiler.CountersProbe";
				//String filename = fullPath + args[0];
				String filename = fullPath + filetemp;
				String[] outputFiles = new String[TOTAL_PARAMETERS];
				justFileNameCSV = fullPath + "\\csv\\" + filetemp.substring(0, filetemp.length() - 4);
				
				if (m < 10)
					justFileNameSQL = fullPath + "\\extracted\\" + filetemp.substring(0, filetemp.length() - 6);
				else
					justFileNameSQL = fullPath + "\\extracted\\" + filetemp.substring(0, filetemp.length() - 7);
		
				System.out.println(filename);
				
				/* There are 10 different telemetryGroups.
				 * 	1.  Atomic tasks completed
				 *  2.  Non-blocking tasks completed
				 *  3.  Blocking tasks completed
				 *  4.  Tasks completed
				 *  5.  No. of unscheduled tasks
				 *  6.  No. of tasks waiting for dependencies
				 *  7.  No. of tasks waiting for children
				 *  8.  No. of tasks waiting in a queue
				 *  9.  No. running tasks
				 *  10. No. of tasks in blocking queue
				 */
				
				/* Creates a folder for this output. */		
				for (int i = 1; i <= NO_TELEMETRY_GROUPS; i++ )
				{
					String outputFilename =  justFileNameCSV + "_tg_" + i + ".csv";
					outputFiles[i-1] = outputFilename;
					
			        String[] cmd = {"jpexport", filename , "ProbeTelemetry", "-probeid=" + probeId,
			        		"-telemetryGroup=" + i, outputFilename};
			
			        //System.out.println("Going to run:\n" + cmd[0] + " " + cmd[1] + " "
			        //		+ cmd[2] + " " + cmd[3] + " " + cmd[4] + " " + cmd[5] + " ");
			        runCommand(cmd);
			        
				}
		
				/* Extracts CPU information. */
				{
					String outputFilename =  justFileNameCSV + "_cpu.csv";
					outputFiles[NO_TELEMETRY_GROUPS] = outputFilename;
					String[] cmd = {"jpexport", filename , "TelemetryCPU", outputFilename};
		
			        runCommand(cmd);
				}
				
				/* Extracts threads' information. */
				{
					String outputFilename =  justFileNameCSV + "_threads.csv";
					outputFiles[NO_TELEMETRY_GROUPS + 1] = outputFilename;
					String[] cmd = {"jpexport", filename , "TelemetryThreads", outputFilename};
		
			        runCommand(cmd);
				}
				
				/* Extracts memory information. */
				{
					String outputFilename =  justFileNameCSV + "_memory.csv";
					outputFiles[NO_TELEMETRY_GROUPS + 2] = outputFilename;
					String[] cmd = {"jpexport", filename , "TelemetryHeap", outputFilename};
		
			        runCommand(cmd);
				}
				
				try {
					if (m == NO_RUNS)
						isLastExecution = true;
					createDatabaseInput(outputFiles, k, m);
					isToAppend = true;
				} catch (Exception e)
				{
					System.out.println("Error: " + e.getMessage());
					System.exit(-1);
				}
			}
			
			isToAppend = false;
			isLastExecution = false;
		}
	}
	
	public static int runCommand(String[] cmd)
	{
		try {
	        Runtime rt = Runtime.getRuntime();
	        Process pr = rt.exec(cmd);
	        
	    
	        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
	
	        String line=null;
	
	        while((line=input.readLine()) != null)
	        	System.out.println(line);
	
	        int exitVal = pr.waitFor();
	        
	        System.out.println("Exited with error code " + exitVal);
	        
	        return exitVal;
	
	    } catch(Exception e) {
	        System.out.println(e.toString());
	        return -1;
	    }
	}
	
	public static void createDatabaseInput(String[] outputFiles, int expID, int runID) throws Exception
	{
		BufferedReader[] inputDB = new BufferedReader[TOTAL_PARAMETERS];
		BufferedWriter outputDB = new BufferedWriter(new FileWriter(justFileNameSQL + ".sql", isToAppend));
		String lines[] = new String[TOTAL_PARAMETERS];
		
		/* Opens all the files. */
		for (int i = 0; i < TOTAL_PARAMETERS; i++)
			inputDB[i] = new BufferedReader(new FileReader(outputFiles[i]));
		
		/* First, reads all the headers, which we are not going to use. */
		for (int i = 0; i < TOTAL_PARAMETERS; i++)
			lines[i] = inputDB[i].readLine();

		boolean isToContinue = true;
		
		do
		{
			String finalLine = "INSERT INTO " + samplesTable + " VALUES(" + expID + "," + runID + ",";
			
			for (int i = 0; i < TOTAL_PARAMETERS; i++)
			{
				if ((lines[i] = inputDB[i].readLine()) == null)
				{
					isToContinue = false;
					break;
				}
				
				/* In memory, we have to remove the dots. */
				if (i == TOTAL_PARAMETERS - 1)
					lines[i] = lines[i].replace(".", "");
				
				String[] splitted = lines[i].split(";");
				
				/* This is the first sample, so we save the value for the time too. */
				if (i == 0)
					finalLine += splitted[0] + ",";
				
				/* This isn't the last sample, so we need to add a comma at the end. */
				if (i < TOTAL_PARAMETERS - 2)
					finalLine += splitted[1] + ",";
				/* THREADS */
				else if (i < TOTAL_PARAMETERS - 1)
					finalLine += splitted[1] + "," + splitted[2] + "," + splitted[3] + "," + 
									splitted[4] + "," + splitted[5] + ",";
				/* MEMORY */
				else
					finalLine += splitted[1] + "," + splitted[2] + "," + splitted[3];
			}
			
			if (isToContinue)
			{
				finalLine += ");\n";
				outputDB.write(finalLine);
			}
			
		}while(isToContinue);
		
		if (isLastExecution)
			outputDB.write("exit;\n");
		
		/* Closes all the files. */
		for (int i = 0; i < TOTAL_PARAMETERS; i++)
			inputDB[i].close();
		
		outputDB.close();
	}

}
