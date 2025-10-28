package aeminium.runtime.profiler;

import com.jprofiler.api.agent.probe.Probe;
import com.jprofiler.api.agent.probe.ProbeProvider;

/* The probe provider class is specified with as a VM parameter as
 * -Djprofiler.probeProvider=aeminium.runtime.profiler.AeminiumProbeProvider
 * so the JProfiler agent can initialize all interceptors at startup.
 * The interceptor class and all referenced classes have to be in the boot classpath
 * so they can be accessed by the profiling agent.
 */
public class AeminiumProbeProvider implements ProbeProvider {

    public Probe[] getProbes() {
    	/* We will have two probes. One that monitors the execution of methods,
    	 * (e.g. time related to a specific task, like how long was it in a queue,
    	 * how long did it take to complete,...) and another that monitors the
    	 * telemetries, so we can have data related to how many tasks we have at
    	 * the moment and so on.
    	 */
    	Probe[] probes = new Probe[2];

    	probes[0] = new CountersProbe();
    	probes[1] = new TaskDetailsProbe();


        return probes;
    }
}
