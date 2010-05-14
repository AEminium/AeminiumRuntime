package aeminiumruntime.linear;

import aeminiumruntime.Body;
import aeminiumruntime.NonBlockingTask;

public class LinearNonBlockingTask extends LinearTask implements NonBlockingTask {
    public LinearNonBlockingTask(Body b, int id){
        super(b, id);
    }
}
