package aeminium.runtime.tools.benchmark;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;

public interface Benchmark {
	public void run(String version, EnumSet<Flags> flags, Reporter reporter);
	public String getName();
	
}
