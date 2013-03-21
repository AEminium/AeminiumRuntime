package aeminium.runtime.tasktype;

import java.lang.reflect.Method;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class TaskTypeAnalyzer {
	public static int getTaskType(Body r) {
		int v = -1; // Default Value

		try {
			Class[] args = new Class[2];
			args[0] = Runtime.class;
			args[1] = Task.class;
			Method runMethod = r.getClass().getDeclaredMethod("execute", args);
			if (runMethod.isAnnotationPresent(TaskType.class)) {
				v = runMethod.getAnnotation(TaskType.class).value();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println(e);
			v = -2;
		}
		return v;
	}
}
