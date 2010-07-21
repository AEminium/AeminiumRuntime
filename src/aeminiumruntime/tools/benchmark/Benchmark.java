package aeminiumruntime.tools.benchmark;

import java.util.EnumSet;

import aeminiumruntime.implementations.Flag;

public interface Benchmark {
	public void run(String version, EnumSet<Flag> flags, Reporter reporter);
	public String getName();
	
}
