package aeminium.runtime.profiler.interceptorTests;

import com.jprofiler.api.agent.probe.Probe;
import com.jprofiler.api.agent.probe.ProbeProvider;

// The probe provider class is specified with as a VM parameter as
// -Djprofiler.probeProvider=aeminium.runtime.profiler.interceptorTests.TestProbeProvider
// so the JProfiler agent can initialize all interceptors at startup.
// The interceptor class and all referenced classes have to be in the boot classpath
// so they can be accessed by the profiling agent.
public class TestProbeProvider implements ProbeProvider {

    public Probe[] getProbes() {
        return new Probe[] {
            new MethodProbe()
        };
    }
}
