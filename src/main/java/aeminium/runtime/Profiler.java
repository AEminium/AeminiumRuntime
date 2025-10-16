package aeminium.runtime;

import java.util.LinkedList;

import aeminium.runtime.profiler.DataCollection;

public interface Profiler {

	public void shutdown();
	public LinkedList<DataCollection> getDataList();
}
