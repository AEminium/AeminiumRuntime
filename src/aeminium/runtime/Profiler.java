package aeminium.runtime;

import java.util.LinkedList;

import profiler.DataCollection;

public interface Profiler {
	
	public void stopExecution();
	public LinkedList<DataCollection> getDataList();
}
