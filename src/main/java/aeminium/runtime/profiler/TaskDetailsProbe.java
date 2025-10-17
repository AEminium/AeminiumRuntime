package aeminium.runtime.profiler;

import java.util.concurrent.ConcurrentHashMap;

import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import com.jprofiler.api.agent.probe.*;

@SuppressWarnings("rawtypes")
public class TaskDetailsProbe implements InterceptorProbe {

	private ConcurrentHashMap <Integer, PayloadInfo> waitingForDependenciesTime = new ConcurrentHashMap <Integer, PayloadInfo>();
	private ConcurrentHashMap <Integer, PayloadInfo> waitingInQueueTime = new ConcurrentHashMap <Integer, PayloadInfo>();
	private ConcurrentHashMap <Integer, PayloadInfo> runningTime = new ConcurrentHashMap <Integer, PayloadInfo>();
	private ConcurrentHashMap <Integer, PayloadInfo> waitingForChildrenTime = new ConcurrentHashMap <Integer, PayloadInfo>();

	@Override
	public void interceptionEnter(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object[] parameters) {

		ImplicitTask task;
		PayloadInfo payloadInfo;

		/* When we schedule a task, it is primarily marked as waiting in queue. */
		if (methodName.equals("addTask"))
		{
			task = (ImplicitTask) parameters[0];

			/* Starts recording the waiting in queue time. */
			payloadInfo = context.createPayloadInfo("Waiting for Dependencies");
			this.waitingForDependenciesTime.put(task.id, payloadInfo);

		} else if (methodName.equals("scheduleTask"))
		{
			task = (ImplicitTask) parameters[0];

			/* Calculates the waiting for dependencies time. */
			payloadInfo = this.waitingForDependenciesTime.remove(task.id);
	        context.addPayloadInfo(payloadInfo.calculateTime());

			/* Starts recording the waiting in queue time. */
			payloadInfo = context.createPayloadInfo("Waiting in Queue");
			this.waitingInQueueTime.put(task.id, payloadInfo);

		/* At the beginning of the method invoke, the task is marked as running. */
		} else if (methodName.equals("invoke"))
		{
			task = (ImplicitTask) object;

			/* Calculates the waiting in queue time. */
			payloadInfo = this.waitingInQueueTime.remove(task.id);
			context.addPayloadInfo(payloadInfo.calculateTime());

	        /* Starts recording the running time. */
			payloadInfo = context.createPayloadInfo("Running");
			this.runningTime.put(task.id, payloadInfo);

			//System.out.println("Counter: " + counter);
			//counter.getAndIncrement();

		/* When the method taskFinished is called, the state of the task turns into
		 * waiting for children.
		 */
		} else if (methodName.equals("taskFinished"))
		{
			task = (ImplicitTask) object;

			/* Calculates the running time. */
			payloadInfo = this.runningTime.remove(task.id);
			context.addPayloadInfo(payloadInfo.calculateTime());

	        /* Starts recording the waiting for children time. */
			payloadInfo = context.createPayloadInfo("Waiting for Children");
			this.waitingForChildrenTime.put(task.id, payloadInfo);

		/* When the method taskCompleted is called, the work for this task is
		 * completely done, meaning it has no longer any working children.
		 */
		} else if (methodName.equals("taskCompleted"))
		{
			task = (ImplicitTask) object;

			/* Calculates the waiting for children time. */
			payloadInfo = this.waitingForChildrenTime.remove(task.id);
			context.addPayloadInfo(payloadInfo.calculateTime());
		}

    }

	@Override
    public void interceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object returnValue) {

    }

	@Override
    public void interceptionExceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Throwable throwable) {
        System.err.println("Exception exit occured: " + throwable.getMessage());
    }

	@Override
    public ProbeMetaData getMetaData() {
        return ProbeMetaData.create("Task Details").events(true)
        		.payload(true).recordOnStartup(true)
        		.description("Measures the time each task take on each processing stage.");
    }

	@Override
    public InterceptionMethod[] getInterceptionMethods() {

    	String implicitWorkStealing = "aeminium.runtime.implementations.implicitworkstealing";

    	InterceptionMethod[] methods2Intercept = new InterceptionMethod[5];

    	/* IMPLICIT TASKS */
    	// invoke
    	methods2Intercept[0] = new InterceptionMethod(
    			implicitWorkStealing + ".task.ImplicitTask",
        		"invoke", "(Laeminium/runtime/implementations/implicitworkstealing/ImplicitWorkStealingRuntime;)V");

    	// taskFinished
    	methods2Intercept[1] = new InterceptionMethod(
    			implicitWorkStealing + ".task.ImplicitTask",
        		"taskFinished", "(Laeminium/runtime/implementations/implicitworkstealing/ImplicitWorkStealingRuntime;)V");

    	// taskCompleted
    	methods2Intercept[2] = new InterceptionMethod(
    			implicitWorkStealing + ".task.ImplicitTask",
        		"taskCompleted", "(Laeminium/runtime/implementations/implicitworkstealing/ImplicitWorkStealingRuntime;)V");

    	// scheduleTask
    	methods2Intercept[3] = new InterceptionMethod(
    			implicitWorkStealing + ".scheduler.BlockingWorkStealingScheduler",
        		"scheduleTask", "(Laeminium/runtime/implementations/implicitworkstealing/task/ImplicitTask;)V");

    	// addTask
    	methods2Intercept[4] = new InterceptionMethod(
    			implicitWorkStealing + ".graph.ImplicitGraph",
        		"addTask", "(Laeminium/runtime/implementations/implicitworkstealing/task/ImplicitTask;"
        			+ "Laeminium/runtime/Task;"
        			+ "Ljava/util/Collection;)V");

    	return methods2Intercept;
    }

}
