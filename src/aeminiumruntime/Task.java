package aeminiumruntime;

import java.util.Collection;

public interface Task {
    public Collection<Hint> getHints();
    
    public void setResult(Object value);
    
    public Object getResult();
}

