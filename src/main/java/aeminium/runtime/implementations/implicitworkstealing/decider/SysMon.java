package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

public class SysMon implements ParallelizationDecider {

	protected final int parallelizeThreshold  = Configuration.getProperty(getClass(), "parallelizeThreshold", 70);
	protected final int memoryThreshold  = Configuration.getProperty(getClass(), "memoryThreshold", 70);

	static JavaSysMon mon = new JavaSysMon();
	CpuTimes last = null;

	public SysMon() {
		last = mon.cpuTimes();
	}

	@Override
	public void setRuntime(Runtime rt) {
		// Runtime is not used here
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		if (mon.physical().getFreeBytes() / mon.physical().getTotalBytes() * 100 < memoryThreshold) return false;

		CpuTimes tmp = mon.cpuTimes();
		float val = tmp.getCpuUsage(last);
		last = tmp;
		return val*100 < parallelizeThreshold;
	}

}
