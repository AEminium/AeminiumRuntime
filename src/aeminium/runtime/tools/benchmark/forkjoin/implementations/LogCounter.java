package aeminium.runtime.tools.benchmark.forkjoin.implementations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import aeminium.runtime.implementations.Factory;
import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

import jsr166y.ForkJoinPool;

public class LogCounter {
	
	public static void main(String[] args) throws Exception {
		
		String path = "/Users/alcides/Desktop/logs/apache2/";
		long start, end;
		
		cleanFiles(path);
		System.out.println("Seq ::::::::");
		start = System.nanoTime();
		System.out.println("n=" + sequentialCounter(path));
		end = System.nanoTime();
		System.out.println(String.format("%d", (end-start)));
		
		cleanFiles(path);
		System.out.println("FJ :::::::::");
		ForkJoinPool pool = new ForkJoinPool();
		
		start = System.nanoTime();
		System.out.println("n=" + forkjoinCounter(path, pool));
		end = System.nanoTime();
		System.out.println(String.format("%d", (end-start)));
		
		cleanFiles(path);
		System.out.println("AE :::::::::");
		aeminium.runtime.Runtime rt = Factory.getRuntime();
		
		
		start = System.nanoTime();
		System.out.println("n=" + aeminiumCounter(path, rt));
		end = System.nanoTime();
		System.out.println(String.format("%d", (end-start)));
		
	}

	public static void cleanFiles(String path) throws IOException,
			InterruptedException {
		Process p;
		p = java.lang.Runtime.getRuntime().exec("cd " + path + ".. && python restore.py");
		p.waitFor();
	}
	
	public static int sequentialCounter(String dirpath) {
		int n = 0;
		File logdir = new File(dirpath);
		for (File logfile : logdir.listFiles()) {
			String d;
			try {
				d = uncompressGZip(logfile);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				n += countAccesses(d);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return n;
	}

	private static int countAccesses(String d)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(d));
		int count = 0;
		String line;
		while (true) {
			line = reader.readLine();
			//System.out.println(line);
			if (line == null) break;
			if (line.startsWith("wiki.alcidesfonseca.com")) {
				count++;
			}
 		} 
		
		return count;
	}

	private static String uncompressGZip(File source) throws IOException {
		if (!source.getAbsolutePath().contains(".gz")) return source.getAbsolutePath();
		
		String dest = source.getAbsolutePath().replace(".gz", "");
		File d = new File(dest);
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(d);
		try {
			in = new GZIPInputStream(in);
			byte[] buffer = new byte[65536];
			int noRead;
			while ((noRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, noRead);
			}
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
			}
		}
		return dest;
	}
	
	
	static class FJCounter implements Callable<Integer> {

		private static final long serialVersionUID = 2879359934394010878L;

		File f;

		public FJCounter(File f) {
			this.f = f;
		}
		
		@Override
		public Integer call() {
			int result = 0;
			String d;
			try {
				d = uncompressGZip(f);
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
			
			try {
				result = countAccesses(d);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public static int forkjoinCounter(String dirpath, ForkJoinPool pool) {
		int n = 0;
		Collection<FJCounter> futures = new ArrayList<FJCounter>();
		
		File logdir = new File(dirpath);
		for (File logfile : logdir.listFiles()) {
			futures.add(new FJCounter(logfile));
		}
		List<Future<Integer>> results = pool.invokeAll(futures);
		
		for (Future<Integer> result : results) {
			try {
				if (result.get() == null) {
					System.out.println("null!");
				}
				n += result.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return n;
	}

	
	public static int aeminiumCounter(String path, aeminium.runtime.Runtime rt) {
		rt.init();
		File logdir = new File(path);
		final ArrayList<Task> counterTasks = new ArrayList<Task>();
		
		for (final File logfile : logdir.listFiles()) {
			final Task uncompress = rt.createBlockingTask(new Body() {
		
				@Override
				public void execute(Task current) {
					try {
						current.setResult(uncompressGZip(logfile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		
			}, aeminium.runtime.Runtime.NO_HINTS);
			rt.schedule(uncompress, Runtime.NO_PARENT, Runtime.NO_DEPS);
			
			Task count = rt.createBlockingTask(new Body() {
				
				@Override
				public void execute(Task current) {
					try {
						current.setResult(countAccesses((String) uncompress.getResult()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		
			}, aeminium.runtime.Runtime.NO_HINTS);
			rt.schedule(count, Runtime.NO_PARENT, Arrays.asList(uncompress));
			counterTasks.add(count);
		}
		
		Task merge = rt.createBlockingTask(new Body() {
			
			@Override
			public void execute(Task current) {
				int n = 0;
				for (Task t : counterTasks) {
					Integer r = (Integer) t.getResult();
					if (r != null) {
						n += r;
					}
				}
				current.setResult(n);
				
			}
	
		}, aeminium.runtime.Runtime.NO_HINTS);
		rt.schedule(merge, Runtime.NO_PARENT, counterTasks);
		
		rt.shutdown();
		return (Integer) merge.getResult();
	}
	
}
