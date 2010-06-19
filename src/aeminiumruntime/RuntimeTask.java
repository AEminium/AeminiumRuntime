package aeminiumruntime;

import aeminium.statistics.Statistics;


public interface RuntimeTask extends Task {
    public Statistics getStatistics();
    public int getId();
    public Body getBody();
    public void execute();
    public boolean isDone();
    public boolean hasStarted();
}
