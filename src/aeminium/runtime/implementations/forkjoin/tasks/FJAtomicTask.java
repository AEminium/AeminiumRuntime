package aeminium.runtime.implementations.forkjoin.tasks;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.forkjoin.FJRuntime;

@SuppressWarnings("serial")
public class FJAtomicTask extends FJTask implements AtomicTask {

	FJDataGroup dg;
	
	public FJAtomicTask(Body b, short hints, FJRuntime rt, DataGroup dg) {
		super(b, hints, rt);
		this.dg = (FJDataGroup) dg;
	}
	
	protected void perform() throws Exception {
		dg.lock();
		super.perform();
		dg.unlock();
	}	
}
