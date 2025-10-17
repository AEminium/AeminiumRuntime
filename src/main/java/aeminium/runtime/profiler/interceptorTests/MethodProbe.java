package aeminium.runtime.profiler.interceptorTests;

import com.jprofiler.api.agent.probe.*;

@SuppressWarnings("rawtypes")
public class MethodProbe implements InterceptorProbe, TelemetryProbe {

	public static int number = 1;

	public void interceptionEnter(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Object[] parameters) {

		System.out.println("INTERCEPTION ENTERED!!");

		System.out.println("Has " + methodName);

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
            payloadInfo.setDescription("Exited payload");
        	context.addPayloadInfo(payloadInfo.calculateTime());
        }
    }

    public void interceptionExceptionExit(InterceptorContext context, Object object, Class declaringClass, String declaringClassName, String methodName, String methodSignature, Throwable throwable) {
        // ignore exceptions
        context.pop();
    }

    public ProbeMetaData getMetaData() {
        return ProbeMetaData.create("Method Testing").events(true).payload(true).recordOnStartup(true)
        		.addAdditionalData("Another Column", DataType.INT)
        		.telemetry(true).addCustomTelemetry("Number", Unit.PLAIN, 1f);
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

	@Override
	public void fillTelemetryData(ProbeContext context, int[] arg1) {

		arg1[0] = number++;
		// TODO Auto-generated method stub

	}
}
