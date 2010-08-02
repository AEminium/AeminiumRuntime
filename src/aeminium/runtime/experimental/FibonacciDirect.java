package aeminium.runtime.experimental;

import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.implicit.ImplicitNonBlockingTask;

public class FibonacciDirect<T extends ImplicitNonBlockingTask> extends ImplicitNonBlockingTask<T> implements Body {
	protected static final EnumSet<Flags> NO_FLAGS =  EnumSet.noneOf(Flags.class);
	protected int n;
	protected final Runtime rt;
	public FibonacciDirect f1;
	public FibonacciDirect f2;
	public static Integer ONE = new Integer(1); 
	public static final int N = 31;
	public volatile int value = 0;
	
	public FibonacciDirect(int n, Runtime rt) {
		super(null, Runtime.NO_HINTS, NO_FLAGS);
		body=this;
		this.n = n;
		this.rt = rt;
	}

	@Override
	public void execute(Task current) {
//		StringBuilder sb = new StringBuilder();
//		for ( int i =  0; i < (N- ((FibonacciDirect)current).n) ; i++) {
//			sb.append(" ");
//		}
//		sb.append(this);
//		System.out.println(sb.toString());
		
		if ( 2 < n ) {			
			f1 = new FibonacciDirect<ImplicitNonBlockingTask>(n-1, rt);
			rt.schedule(f1, this, Runtime.NO_DEPS);
			f2 = new FibonacciDirect<ImplicitNonBlockingTask>(n-2, rt);
			rt.schedule(f2, this, Runtime.NO_DEPS);
		} else {
			//setResult(ONE);
			value = 1;
		}
	}

	@Override
	public void taskCompleted() {
		super.taskCompleted();
		if (f1 != null && f2 != null ) {
			value = f1.value + f2.value;
			f1 = null;
			f2 = null;
		}
	}

	public static void main(String[] args) {
		
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		long start = System.nanoTime();
		
		FibonacciDirect<ImplicitNonBlockingTask> root = new FibonacciDirect<ImplicitNonBlockingTask>(N, rt);
		rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();

		long time = System.nanoTime() - start;
		
		System.out.println("fib("+N+") = " + root.value + "  in " + time +" ns.");
	}
	
	public String toString() {
		return "FibonacciDirect("+n+")";
	}
}
