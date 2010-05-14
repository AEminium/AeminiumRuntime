package aeminiumruntime.linear;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;

public class LinearBlockingTask extends LinearTask implements BlockingTask {
    public LinearBlockingTask(Body b, int id){
        super(b, id);
    }
}
