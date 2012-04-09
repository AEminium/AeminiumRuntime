package aeminium.runtime.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_f_1_if_1_ret implements aeminium.runtime.Body {
  Fibonacci_f_1_if_1_ret(  Fibonacci_f_1_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_parent.ae_ret=this.ae_parent.ae_parent.n;
  }
  public aeminium.runtime.Task ae_task;
  public Fibonacci_f_1_if ae_parent;
}
