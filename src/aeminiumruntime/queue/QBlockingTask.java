package aeminiumruntime.queue;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;

public class QBlockingTask extends QAbstractTask implements BlockingTask {

	public QBlockingTask(Body body) {
		super(body);
	}

}
