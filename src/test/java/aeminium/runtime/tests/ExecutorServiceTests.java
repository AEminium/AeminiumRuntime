/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	@Test
	public void timeoutTest1() {
		final String DATA = "Te$t$tring";
		final Runtime rt = getRuntime();
		rt.init();
		ExecutorService es = rt.getExecutorService();

		Future<String> f = es.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(300);
				return DATA;
			}
		});

		try {
			@SuppressWarnings("unused")
			String result = f.get(100, TimeUnit.MILLISECONDS);
			fail();
		} catch (InterruptedException e) {
			assertTrue(true);
		} catch (ExecutionException e) {
			assertTrue(  e.getCause() instanceof InterruptedException );
		} catch (TimeoutException e) {
			assertTrue(true);
		}


		rt.shutdown();
	}

	@Test
	public void timeoutTest2() {
		final String DATA = "Te$t$tring";
		final Runtime rt = getRuntime();
		rt.init();
		ExecutorService es = rt.getExecutorService();

		Future<String> f = es.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(100);
				return DATA;
			}
		});

		try {
			String result = f.get(300, TimeUnit.MILLISECONDS);
			assertTrue(result.equals(DATA));
		} catch (InterruptedException e) {
			assertTrue(true);
		} catch (ExecutionException e) {
			assertTrue(  e.getCause() instanceof InterruptedException );
		} catch (TimeoutException e) {
			fail();
		}


		rt.shutdown();
	}
}
