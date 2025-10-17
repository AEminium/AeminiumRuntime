package aeminium.runtime.implementations.implicitworkstealing.decider;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;


public class SysMon2 implements ParallelizationDecider  {
	static OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
	static com.sun.management.OperatingSystemMXBean so;

	protected final int parallelizeThreshold  = Configuration.getProperty(getClass(), "parallelizeThreshold", 70);
	protected final int memoryThreshold  = Configuration.getProperty(getClass(), "memoryThreshold", 70);

	static {
		if (o instanceof com.sun.management.OperatingSystemMXBean) {
			 so = (com.sun.management.OperatingSystemMXBean) o;
		}
	}

	@Override
	public void setRuntime(Runtime rt) {
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		if (so != null) {
			return so.getSystemCpuLoad() * 100 < parallelizeThreshold && so.getFreePhysicalMemorySize() * 100 / so.getTotalPhysicalMemorySize() < memoryThreshold;
		}
		double load = o.getSystemLoadAverage();
		if (load < 0) return true;
		return load * 100 < parallelizeThreshold;

	}

}
