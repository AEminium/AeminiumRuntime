package aeminiumruntime.simpleparallel;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;

public class ParallelBlockingTask extends ParallelTask implements BlockingTask {
    public ParallelBlockingTask(Body b, int id){
        super(b, id);
    }
}
