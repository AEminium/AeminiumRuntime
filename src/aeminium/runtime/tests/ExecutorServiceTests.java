package aeminium.runtime.tests;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;

import aeminium.runtime.Runtime;

public class ExecutorServiceTests extends BaseTest {
	@Test
	public void simpleShutdown() {
		Runtime rt = getRuntime();
		rt.init();
		
		ExecutorService es = rt.getExecutorService();
		
		es.shutdown();
		
		rt.shutdown();
	}
	
	@Test 
	public void simpleTask() {
		Runtime rt = getRuntime();
		rt.init();
		
		ExecutorService es = rt.getExecutorService();
		es.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("SimpleTask");
			}
		});
		es.shutdown();
		
		rt.shutdown();
	}
	
	@Test 
	public void waitTask() {
		final String RESULT = "I have run";
		Runtime rt = getRuntime();
		rt.init();
		
		ExecutorService es = rt.getExecutorService();
		Future<String> f = es.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				Thread.sleep(200);
				return RESULT;
			}

		});
		
		try {
			assertTrue( RESULT.equals(f.get()));
		} catch (Exception e) {
		}
		
		es.shutdown();
		
		rt.shutdown();
	}
	
	@Test
	public void invokeAll() {
		final String[] data = {"FIR$T", "$ECOND"};
		Runtime rt = getRuntime();
		rt.init();
		ExecutorService es = rt.getExecutorService();

		List<Callable<String>> tasks = new LinkedList<Callable<String>>();
		tasks.add(null);tasks.add(null);
		tasks.set(0, new Callable<String>(){
					@Override
					public String call() throws Exception {
						return data[0];
					}
				});
		tasks.set(1, new Callable<String>() {
					@Override
					public String call() throws Exception {
						return data[1];
					}
				});

		try {
			List<Future<String>> futures = es.invokeAll(tasks);
			
			for (int i = 0; i < futures.size(); i++ ) {
				assertTrue( data[i].equals(futures.get(i).get()));
			}
		} catch (InterruptedException e) {
			org.junit.Assert.fail();
		} catch (ExecutionException e) {
			org.junit.Assert.fail();
		}
		
		rt.shutdown();
		
	}
}
