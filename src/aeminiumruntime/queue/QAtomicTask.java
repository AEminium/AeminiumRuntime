package aeminiumruntime.queue;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;

public class QAtomicTask extends QAbstractTask implements AtomicTask {
	private final QDataGroup datagroup;
	
	public QAtomicTask(Body body, DataGroup datagroup) {
		super(body);
		this.datagroup = (QDataGroup)datagroup;
	}

	@Override
	public DataGroup getDataGroup() {
		return datagroup;
	}

	@Override
	public Object call() throws Exception {
		boolean locked = datagroup.trylock(this);
		if ( locked ) {
			getBody().execute(this);
			getGraph().taskFinshed(this);
		}
		return null;
	}

	@Override 
	public void taskCompleted() {
		datagroup.unlock();
	}

}
