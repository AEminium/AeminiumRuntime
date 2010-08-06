package aeminium.runtime;


public interface Task {
	
	public Body getBody();
	
	@Deprecated
    public void setResult(Object value);
    
	@Deprecated
    public Object getResult();
}

