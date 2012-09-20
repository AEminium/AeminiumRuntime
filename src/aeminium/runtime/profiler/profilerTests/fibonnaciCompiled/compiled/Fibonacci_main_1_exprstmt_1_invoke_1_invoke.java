package aeminium.runtime.profiler.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_main_1_exprstmt_1_invoke_1_invoke implements aeminium.runtime.Body {
  Fibonacci_main_1_exprstmt_1_invoke_1_invoke(  Fibonacci_main_1_exprstmt_1_invoke ae_parent){
    this.ae_deferred=new Fibonacci_f();
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_deferred.schedule(java.util.Arrays.asList(this.ae_task),35);
  }
  public Fibonacci_f ae_deferred;
  public volatile int ae_ret;
  public aeminium.runtime.Task ae_task;
  public Fibonacci_main_1_exprstmt_1_invoke ae_parent;
}
