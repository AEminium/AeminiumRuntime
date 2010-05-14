package aeminiumruntime;

public interface AtomicTask extends Task {
    /* returns the datagroup this task is operating on */
    public DataGroup getDataGroup();
}
