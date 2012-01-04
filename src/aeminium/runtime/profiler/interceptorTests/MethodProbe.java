package aeminium.runtime.profiler.interceptorTests;

import com.jprofiler.api.agent.probe.*;

@SuppressWarnings("rawtypes")
public class MethodProbe implements InterceptorProbe {
	
	public void interceptionEnter(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object[] parameters) {
        
		System.out.println("INTERCEPTION ENTERED!!");
		
		System.out.println("Has " + (String) parameters[0]);
		
		// the "loadFactor" parameter is the content of the payload
        PayloadInfo payloadInfo = context.createPayloadInfo(parameters[0].getClass().getName());
        // save payload for use in interceptionExit
        context.push(payloadInfo);
    }

    public void interceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object returnValue) {
        
    	System.out.println("INTERCEPTION EXIT!!");
    	
    	// get payload that was saved in interceptionEnter
        PayloadInfo payloadInfo = context.pop();
        // in case of nested interceptions, only handle the outermost interception
        if (context.isPayloadStackEmpty()) {
            // perform the time measurement relative to the creation of the payload info and
            // attach the payload to the current call stack, so it is displayed in the call tree and the hot spot list
            context.addPayloadInfo(payloadInfo.calculateTime());
        }
    }

    public void interceptionExceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Throwable throwable) {
        // ignore exceptions
        context.pop();
    }

    public ProbeMetaData getMetaData() {
        return ProbeMetaData.create("AWT event types").payload(true);
    }

    public InterceptionMethod[] getInterceptionMethods() {
    	
    	InterceptionMethod[] methods2Intercept = new InterceptionMethod[2];
    	
    	methods2Intercept[0] = new InterceptionMethod("aeminium.runtime.profiler.interceptorTests.InterceptorLauncher", 
        		"methodOne", "(Ljava/lang/String;)V");
    	methods2Intercept[1] = new InterceptionMethod("aeminium.runtime.profiler.interceptorTests.InterceptorLauncher", 
        		"methodTwo", "(Ljava/lang/String;)V");
    	
        /*return new InterceptionMethod[] {
            // this is java.awt.EventQueue.dispatchEvent(java.awt.AWTEvent event)
            new InterceptionMethod("aeminium.runtime.profiler.interceptorTests.InterceptorLauncher", 
            		"methodOne", "(Ljava/lang/String;)V")
        };*/
    	
    	return methods2Intercept;
    }
}
