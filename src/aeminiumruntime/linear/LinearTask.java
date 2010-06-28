package aeminiumruntime.linear;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.statistics.StatisticTask;
import aeminiumruntime.statistics.Statistics;

import java.util.Collection;


public class LinearTask extends StatisticTask implements RuntimeTask {

    private int id;
    private Body body;
    private boolean done;
    private boolean started;
    private Object result;

    public LinearTask(Body b, int id) {
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

    public Object call() {
        this.started = true;
        this.body.execute(this);
        this.done = true;
        return null;
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

    @Override
    public void setResult(Object value) {
    	this.result = value;
    }
    
    @Override
    public Object getResult() {
    	return result;
    }
}
