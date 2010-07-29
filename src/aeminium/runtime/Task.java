package aeminium.runtime;


public interface Task {
	
	public Body getBody();
	
    public void setResult(Object value);
    
    public Object getResult();
}

