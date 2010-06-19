package aeminiumruntime.simpleparallel;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.Statistics;

import java.util.Collection;
import aeminiumruntime.StatisticTask;

public class ParallelTask extends StatisticTask implements RuntimeTask {
    private int id;
    private Body body;
    private boolean done;
    private boolean started;
    

    public ParallelTask(Body b, int id) {
        this.statistics = new Statistics();
        this.body = b;
        this.id = id;
        this.started = false;
        this.done = false;
    }

    public int getId() {
        return this.id;
    }

    public Body getBody() {
        return this.body;
    }

    public void execute() {
        this.started = true;
        this.body.execute();
        this.done = true;
    }

    public Collection<Hint> getHints() {
        return null;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean hasStarted() {
        return this.started;
    }
    
    public Statistics getStatistics(){
        return statistics;
    }
}
