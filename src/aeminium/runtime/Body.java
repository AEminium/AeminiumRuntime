package aeminium.runtime;

/**
 *  Represents the task body that is executed by the runtime 
 */
public interface Body {
	/**
	 * This method contains the code of the body, that is executed by the task. 
	 * 
	 * @param current Reference to the associated task object.
	 */
    public void execute(Task current);
}