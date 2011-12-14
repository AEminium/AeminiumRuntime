package aeminium.runtime.profiler;

import java.io.*;
import java.util.*;

import aeminium.runtime.Profiler;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.AeminiumThread;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

public class AeminiumProfiler extends AeminiumThread implements Profiler {
	
	private final long SLEEP_PRECISION = 100;
	private final long SPIN_YIELD_PRECISION = 100;
	private final long SLEEPING_TIME = 5;
	private final int LENGTH_SAVING_LIMIT = 100;
	
	private BlockingWorkStealingScheduler scheduler;
	private ImplicitGraph graph;
	private LinkedList<DataCollection> dataList;
	
	private Hashtable <Integer, TaskInfo> tasksInfo = new Hashtable <Integer, TaskInfo>();
	
	
	/* We save the files into a directory named after the starting time
	 * of the profiler.
	 */
	private String directoryPath;
	private int fileCounter = 0;
	private boolean saveDataToFile = true;
	
	private volatile boolean shutdown = false;
	
	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {
		
		this.scheduler = scheduler;
		this.graph = graph;
		this.dataList = new LinkedList<DataCollection>();
		
		String directoryName = (((new Date()).toString()).replace(" ", "_")
								.replaceFirst(":", "h")).replaceFirst(":", "m");
		
		//this.directoryPath = "C:/Users/Alcides/Desktop/Ivo/SavedData/" + directoryName + 
		//						"_" + System.nanoTime() + "/";
		this.directoryPath = "E:/Ivo/FCTUC/AEminium/SavedData/" + directoryName + 
								"_" + System.nanoTime() + "/";
	
		createDirectory();
		
		/* The Profiler will be working on top priority. */
		this.setPriority(MAX_PRIORITY);
		this.start();
	}
	
	@Override
	public void run() {

		while (!this.shutdown) {
			/* If the list containing the data gets too long, we save its
			 * contents to the file and make a fresh start.
			 */
			if (this.dataList.size() % this.LENGTH_SAVING_LIMIT == 0 && saveDataToFile) {
				new CsvFileWriter(fileCounter, directoryPath, dataList);
				this.fileCounter++;
				this.dataList = new LinkedList<DataCollection>();
			}
				
			DataCollection data = new DataCollection(this.scheduler.getMaxParallelism());
			data.samplingTime = System.nanoTime();
			
			/* We first collect the data concerning the graph. */
			this.graph.collectData(data);
			/* Then, the data collected with the scheduler. */
			this.scheduler.collectData(data);
			this.dataList.add(data);
			
	        final long end = System.nanoTime() + this.SLEEPING_TIME;
	        long timeLeft = this.SLEEPING_TIME;
	
	        /* Sleeps for the defined time before collecting new data. */
	        do {
	            if (timeLeft > this.SLEEP_PRECISION)
					try {
						Thread.sleep (SLEEPING_TIME);
					} catch (Exception e) {
						System.out.println("ERROR ON PROFILER: " + e.getMessage());
						return;
					}
				else
	                if (timeLeft > this.SPIN_YIELD_PRECISION)
	                    Thread.yield();
	
	            timeLeft = end - System.nanoTime();
	
	        } while (timeLeft > 0 && this.shutdown);
	    }
		
		/* Writes the leftovers into file. */
		if (saveDataToFile) {
			new CsvFileWriter(fileCounter, directoryPath, dataList);
			fileCounter++;
		}
	}
	
	@Override
	public void shutdown() {
		System.out.println("Profiler has been ordered to stop...");
		this.shutdown = true;
	}
	
	@Override
	public LinkedList<DataCollection> getDataList() {
		return this.dataList;
	}
	
	private void createDirectory() {
		  try {
			  (new File(this.directoryPath)).mkdir();
	
		  } catch (Exception e) {//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		  }
	}
	
	public void setTaskInfo(Task task) {
		
		TaskInfo info = new TaskInfo();
		this.tasksInfo.put(task.hashCode(), info);
	}
	
	public TaskInfo getTaskInfo(int taskHash) {
		
		return this.tasksInfo.get(taskHash);
	}
}

class CsvFileWriter extends Thread {
	
	private int fileCounter;
	private String directoryPath;
	private String csvFileHeader = "CSV FILE HEADER: FILL\n";
	private LinkedList<DataCollection> dataList;
	
	public CsvFileWriter(int fileCounter, String directoryPath, LinkedList<DataCollection> dataList2) {
		this.fileCounter = fileCounter;
		this.directoryPath = directoryPath;
		this.dataList = dataList2;
		
		/* The same for the file writing threads. */
		this.setPriority(MAX_PRIORITY);
		
		this.start();
	}
	
	public void run() {
		Writer output = null;
		File file = new File(this.directoryPath + "/data" + this.fileCounter + ".txt");

		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(this.csvFileHeader);
			String contents = "";
			
			/* Writing:
			 * 	-> Number of non-blocking queues;
			 *  -> Number of blocking queues.
			 */
			if (dataList.size() > 0) {
				contents += dataList.get(0).taskInNonBlockingQueue.length + ",";
				contents += dataList.get(0).taskInBlockingQueue.length + "\n";
			} else {
				output.write("EMPTY\n");
				output.close();
				return;
			}	
			
			/* Writes all the values into the file. */
			for (DataCollection element : dataList) 
			{
				/* Writes the time when was written this sample. */
				contents += element.samplingTime + ",";
				
				/* Writes:
				 *   -> Number of non-blocking tasks in the queues;
				 *   -> Number of tasks handled by this thread. 
				 */
				for (int i = 0; i < element.taskInNonBlockingQueue.length; i++) {
					contents += element.taskInNonBlockingQueue[i]
					            + "," + element.tasksHandled[i][0] +
					              "," + element.tasksHandled[i][1] +
					              "," + element.tasksHandled[i][2] + ",";
				}
				
				/* Writes the number of blocking tasks in the queues. */
				for (int i : element.taskInBlockingQueue)
					contents += i + ",";
				
				/* Writes the number of tasks completed so far. */
				contents += element.noTasksCompleted[0] + "," + 
							element.noTasksCompleted[1] + "," +
							element.noTasksCompleted[2] + ",";
				
				contents += element.noWaitingForDependenciesTasks + "," +
							element.noRunningTasks + "," + 
							element.noCompletedTasks;
				
				contents += "\n";
			}
	
			output.write(contents);
			output.close();
			
		} catch (Exception e) {
			System.out.println("PROFILER ERROR (" + e.getMessage() + "): Problems while saving data list into file.");
		}

		this.fileCounter++;
	}
}
