package aeminium.runtime.tasktype;

import java.lang.reflect.Method;

public class TaskTypeAnalyzer {
	public static int getTaskType(Runnable r) {
		int v = -1; // Default Value
		
		try {
			Method runMethod = r.getClass().getDeclaredMethod("run");
			if (runMethod.isAnnotationPresent(TaskType.class)) {
				v = runMethod.getAnnotation(TaskType.class).value();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			v = -2;
		}
		return v;
	}
}
