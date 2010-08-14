package aeminium.runtime.scheduler.workstealing;

public interface WorkStealingQueue<E> {
	/**
	 * Pushed the element onto the queue. If the queue is full 
	 * the method will try to grow the queue. If the maximum queue
	 * size is reached the method will throw an Error;
	 * 
	 * @param e
	 */
	public void push(E e);
	/**
	 * Pops the next element off the queue. If the queue is empty
	 * the method returns null;
	 * 
	 * @param e
	 */
	public E pop();
	/**
	 * Returns the next element in the queue without removing it. If 
	 * the queue is empty the method returns null;
	 * 
	 * @param e
	 */
	public E peek();
	/**
	 * Tries to steal a task from the of the queue. If successfull the
	 * method returns the stolen task otherwise null;
	 * 
	 * @return
	 */
	public E tryStealing();
	/**
	 * Checks whether the queue is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty();
	/**
	 * Checks whether the queue is full.
	 * 
	 * @return
	 */
	public boolean isFull();
	/**
	 * Gives an approximation of the queue length. NOTE: Because 
	 * of the concurrent updates the returned value is more supposed 
	 * be an educated guess rather than an exact value.
	 * 
	 * @return
	 */
	public int size();
}
