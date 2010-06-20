package aeminiumruntime.simpleparallel;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;

public class ParallelAtomicTask extends ParallelTask implements AtomicTask {
	
	ParallelDataGroup owner;
	
    public ParallelAtomicTask(Body b, int id, ParallelDataGroup owner){
        super(b, id);
        this.owner = owner;
    }

	@Override
	public DataGroup getDataGroup() {
		return this.owner;
	}

	@Override
	public Object call() {
		Object result;
		this.owner.lock();
		result = super.call();
		this.owner.unlock();
		return result;
	}
	
	
}

