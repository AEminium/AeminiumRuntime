package aeminium.runtime;

public interface ResultBody<T> extends Body {
	public void completed();
	public T getResult();
}
