package aeminiumruntime;

public class RuntimeError extends Error {
	private static final long serialVersionUID = -1893143434666296990L;
	
	public RuntimeError(String msg) {
		super(msg);
	}
}
