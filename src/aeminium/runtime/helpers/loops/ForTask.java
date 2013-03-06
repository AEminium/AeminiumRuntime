package aeminium.runtime.helpers.loops;

import java.util.ArrayList;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class ForTask {

	public static int PARALLELISM_SIZE = 32;
	
	public static <T> Task createFor(Runtime rt, final Iterable<T> collection, final ForBody<T> forBody) {
		
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				// Initial Copy
				final ArrayList<T> objects = new ArrayList<T>();
				for (T o : collection) {
					objects.add(o);
				}
				
				int blockSize = objects.size() / PARALLELISM_SIZE;
				if (blockSize <= 0) blockSize = 1;
				for (int i=0; i < objects.size(); i+=blockSize) {
					final List<T> iterationSet = objects.subList(i, Math.min(objects.size(), i+blockSize));
					Task iterationBulk = rt.createNonBlockingTask(new Body() {

						@Override
						public void execute(Runtime rt, Task current)
								throws Exception {
							for (T obj : iterationSet) {
								forBody.iterate(obj);
							}
						}
						
					}, Runtime.NO_HINTS);
					rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
				}
			}
			
		}, Runtime.NO_HINTS);
	}

}
