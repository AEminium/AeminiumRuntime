package aeminium.runtime;

import aeminium.runtime.implementations.Factory;
import java.util.Collection;

public class AeminiumHelper
{
	public static aeminium.runtime.Runtime rt;
	public static short NO_HINTS = aeminium.runtime.Runtime.NO_HINTS;
	public static Collection<Task> NO_DEPS = aeminium.runtime.Runtime.NO_DEPS;
	public static Task NO_PARENT = aeminium.runtime.Runtime.NO_PARENT;

	public static void init()
	{
		AeminiumHelper.rt = Factory.getRuntime();
		AeminiumHelper.rt.init();
	}

	public static void shutdown()
	{
		AeminiumHelper.rt.shutdown();
	}

    public static void schedule(Task task, Task parent, Collection<Task> deps)
	{
		AeminiumHelper.rt.schedule(task, parent, deps);
	}

    public static NonBlockingTask createNonBlockingTask(Body b, short hints)
	{
		return AeminiumHelper.rt.createNonBlockingTask(b, hints);
	}
}
