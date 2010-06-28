package aeminiumruntime;

import aeminiumruntime.statistics.Statistics;



public interface RuntimeTask extends Task {
    public Statistics getStatistics();
    public int getId();
    public Body getBody();
    public Object call();
    public boolean isDone();
    public boolean hasStarted();
}
