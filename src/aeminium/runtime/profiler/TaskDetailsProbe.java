package aeminium.runtime.profiler;

import java.util.Hashtable;

import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

import com.jprofiler.api.agent.probe.*;

@SuppressWarnings("rawtypes")
public class TaskDetailsProbe implements InterceptorProbe {
	
	public static int number = 1;
	private Hashtable <Integer, PayloadInfo> invokingTime = new Hashtable <Integer, PayloadInfo>();
	private Hashtable <Integer, PayloadInfo> waitingForChildrenTime = new Hashtable <Integer, PayloadInfo>();
	
	public void interceptionEnter(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object[] parameters) {

		/* Given the method name, we need to take different actions. */
		if (methodName.equals("invoke"))
		{
			ImplicitTask task = (ImplicitTask) object;
			
			PayloadInfo payloadInfo = context.createPayloadInfo("invoke method");
	        
			this.invokingTime.put(task.id, payloadInfo);
		//TODO: Naturally, change this. I've just kept the else if so I can
		//		easily copy/paste for the next functions I need to implement.
		} else if (methodName.equals("SECOND"))
		{
			
		} else if (methodName.equals("THIRD"))
		{
			
		}

    }

    public void interceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object returnValue) {
        
    	PayloadInfo payloadInfo = null;

		/* Given the method name, we need to take different actions. */
		if (methodName.equals("invoke"))
		{
			ImplicitTask task = (ImplicitTask) object;
			payloadInfo = this.invokingTime.get(task.id);
			
			payloadInfo.setDescription("Task processing time");
	        context.addPayloadInfo(payloadInfo.calculateTime());
	        
	        /* Now, saves the time that it is waiting for children. */
	        PayloadInfo payloadInfo2 = context.createPayloadInfo("Waiting for children");
			this.waitingForChildrenTime.put(task.id, payloadInfo2);    
		
	    } else if (methodName.equals("taskCompleted"))
		{
	    	ImplicitTask task = (ImplicitTask) object;
			payloadInfo = this.waitingForChildrenTime.get(task.id);
			
			//TODO: Sometime we have null. Check again if this is still
			//		necessary, as I have cleaned up the collisions in the
			//		hash table.
			if (payloadInfo != null) 
				payloadInfo.setDescription("Waiting for children time");
		//TODO: Same has above.	
		} else if (methodName.equals("THIRD"))
		{
			
		}
		
		if (payloadInfo != null)
			context.addPayloadInfo(payloadInfo.calculateTime());
    }

    public void interceptionExceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Throwable throwable) {
        // ignore exceptions
        context.pop();
    }

    public ProbeMetaData getMetaData() {
        return ProbeMetaData.create("Task Details").events(true)
        		.payload(true).recordOnStartup(true)
        		.description("Measures the time each task take on each processing stage.");
    }

    public InterceptionMethod[] getInterceptionMethods() {

    	String implicitWorkStealing = "aeminium.runtime.implementations.implicitworkstealing";
    	
    	InterceptionMethod[] methods2Intercept = new InterceptionMethod[2];
    	
    	/* IMPLICIT TASKS */
    	/* Invoke */
    	methods2Intercept[0] = new InterceptionMethod(
    			implicitWorkStealing + ".task.ImplicitTask", 
        		"invoke", "(Laeminium/runtime/implementations/implicitworkstealing/ImplicitWorkStealingRuntime;)V");
    	methods2Intercept[1] = new InterceptionMethod(
    			implicitWorkStealing + ".task.ImplicitTask", 
        		"taskCompleted", "(Laeminium/runtime/implementations/implicitworkstealing/ImplicitWorkStealingRuntime;)V");
    	
    	//methods2Intercept[1] = new InterceptionMethod("aeminium.runtime.profiler.interceptorTests.InterceptorLauncher", 
        //		"methodTwo", "(Ljava/lang/String;)V");
    	
    	return methods2Intercept;
    }

}
