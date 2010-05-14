package aeminiumruntime.simpleparallel;

import aeminiumruntime.Body;
import aeminiumruntime.NonBlockingTask;

public class ParallelNonBlockingTask extends ParallelTask implements NonBlockingTask {
    public ParallelNonBlockingTask(Body b, int id){
        super(b, id);
    }
}
