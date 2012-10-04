package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci_main_1_invoke_1_invoke extends aeminium.runtime.CallerBodyWithReturn<Long> implements aeminium.runtime.Body {
  Fibonacci_main_1_invoke_1_invoke(  Fibonacci_main_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Fibonacci_f(this,50);
 else     this.ae_ret=Fibonacci.f(50);
  }
  public Fibonacci_main_1_invoke ae_parent;
}
