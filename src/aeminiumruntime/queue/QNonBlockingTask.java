package aeminiumruntime.queue;

import aeminiumruntime.Body;
import aeminiumruntime.NonBlockingTask;

public class QNonBlockingTask extends QAbstractTask implements NonBlockingTask {

	public QNonBlockingTask(Body body) {
		super(body);
	}

}
