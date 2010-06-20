package aeminiumruntime;

import java.util.concurrent.Callable;

import aeminiumruntime.statistics.Statistics;



public interface RuntimeTask extends Task, Callable<Object>{
    public Statistics getStatistics();
    public int getId();
    public Body getBody();
    public Object call();
    public boolean isDone();
    public boolean hasStarted();
}
