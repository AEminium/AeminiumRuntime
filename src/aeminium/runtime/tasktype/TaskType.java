package aeminium.runtime.tasktype;
import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
public @interface TaskType {
	int value() default 0;
}
