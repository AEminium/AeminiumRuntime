package aeminium.runtime.profiler.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_f_1_if implements aeminium.runtime.Body {
  Fibonacci_f_1_if(  Fibonacci_f ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_parent.n < 2) {
      new Fibonacci_f_1_if_1_ret(this);
    }
 else {
      new Fibonacci_f_1_if_2_ret(this);
    }
  }
  public aeminium.runtime.Task ae_task;
  public Fibonacci_f ae_parent;
}
