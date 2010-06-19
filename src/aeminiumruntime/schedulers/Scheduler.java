package aeminiumruntime.schedulers;

public interface Scheduler {
    public void turnOff();
    public void run();
    
    // Thread stuff
    public void start();
    public void join() throws InterruptedException;
}
