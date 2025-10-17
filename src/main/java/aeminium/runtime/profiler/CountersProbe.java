package aeminium.runtime.profiler;

import aeminium.runtime.implementations.Configuration;

import com.jprofiler.api.agent.probe.*;

public class CountersProbe implements TelemetryProbe {
	/* These variables hold the corresponding positions in the array of
	 * information for that will be sent to JProfiler.
	 */
	private final static int ATOMIC_TASK = 0;
	private final static int NON_BLOCKING_TASK = 1;
	private final static int BLOCKING_TASK = 2;
	private final static int NO_TASKS_COMPLETED = 3;

	private final static int NO_UNSCHEDULED_TASKS = 4;
	private final static int NO_WAITING_FOR_DEPENDENCIES_TASKS = 5;
	private final static int NO_WAITING_FOR_CHILDREN_TASKS = 6;
	private final static int NO_TASKS_WAITING_IN_QUEUE = 7;
	private final static int NO_RUNNING_TASKS = 8;
	private final static int NO_TASKS_IN_BLOCKING_QUEUE = 9;

	private final static int QUEUE_INFO = 10;

	private DataCollection data;

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	private int maxParallelism = 1;

    public ProbeMetaData getMetaData() {

    	/* First, we get the number of processors available. */
    	maxParallelism = Configuration.getProcessorCount()*2;

    	ProbeMetaData metaData = ProbeMetaData.create("Counters").recordOnStartup(true)
    								.telemetry(true)
    								.description("Measures the counting variables of the system");

    	/* First, add space for the number of completed tasks. */
    	metaData.addCustomTelemetry("Atomic Tasks Completed", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("Non Blocking Tasks Completed", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("Blocking Tasks Completed", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("Tasks Completed", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No of Unscheduled Tasks", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No of Tasks Waiting for Dependencies", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No of Tasks Waiting for Children", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No of Tasks Waiting in a Queue", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No Running Tasks", Unit.PLAIN, 1f);
    	metaData.addCustomTelemetry("No Tasks in Blocking Queue", Unit.PLAIN, 1f);

    	/* Then, creates a set of information for each thread capable of handling tasks
    	 * (i.e., the set will have the size of maxParallelism, corresponding to the number
    	 * of processors available).
    	 */
    	for (int i = 0; i < maxParallelism; i++)
    	{
    		metaData.addCustomTelemetry("Tasks in Non-blocking Queue (" + (i + 1) + ")", Unit.PLAIN, 1f);
        	metaData.addCustomTelemetry("No of Atomic Tasks Handled (" + (i + 1) + ")", Unit.PLAIN, 1f);
        	metaData.addCustomTelemetry("No of Blocking Tasks Handled (" + (i + 1) + ")", Unit.PLAIN, 1f);
        	metaData.addCustomTelemetry("No of Non-blocking Tasks Handled (" + (i + 1) + ")", Unit.PLAIN, 1f);
        }

    	/* Initializes the data collection object. */
    	data = new DataCollection(maxParallelism);

        return metaData;
    }

	@Override
	public void fillTelemetryData(ProbeContext context, int[] customTelemetries)
	{
		/* Get the data from the graph. */
		if (AeminiumProfiler.graph != null)
		{
			AeminiumProfiler.graph.collectData(data);

			customTelemetries[ATOMIC_TASK] = data.noTasksCompleted[ATOMIC_TASK];
			customTelemetries[NON_BLOCKING_TASK] = data.noTasksCompleted[NON_BLOCKING_TASK];
			customTelemetries[BLOCKING_TASK] = data.noTasksCompleted[BLOCKING_TASK];
			customTelemetries[NO_TASKS_COMPLETED] = data.noCompletedTasks;
			customTelemetries[NO_UNSCHEDULED_TASKS] = data.noUnscheduledTasks;
			customTelemetries[NO_WAITING_FOR_DEPENDENCIES_TASKS] = data.noWaitingForDependenciesTasks;
			customTelemetries[NO_WAITING_FOR_CHILDREN_TASKS] = data.noWaitingForChildrenTasks;
			customTelemetries[NO_TASKS_WAITING_IN_QUEUE] = data.noTasksWaitingInQueue;
			customTelemetries[NO_RUNNING_TASKS] = data.noRunningTasks;
			customTelemetries[NO_TASKS_IN_BLOCKING_QUEUE] = data.taskInBlockingQueue;
		}

		/* Information related to the scheduler. */
		if (AeminiumProfiler.scheduler != null)
		{
			AeminiumProfiler.scheduler.collectData(data);

			for (int i = 0; i < maxParallelism; i++)
			{
				customTelemetries[QUEUE_INFO + i*4] = data.taskInNonBlockingQueue[i];
				customTelemetries[QUEUE_INFO + i*4 + 1] = data.tasksHandled[i][ATOMIC_TASK];
				customTelemetries[QUEUE_INFO + i*4 + 2] = data.tasksHandled[i][BLOCKING_TASK];
				customTelemetries[QUEUE_INFO + i*4 + 3] = data.tasksHandled[i][NON_BLOCKING_TASK];
			}

		}
	}
}
