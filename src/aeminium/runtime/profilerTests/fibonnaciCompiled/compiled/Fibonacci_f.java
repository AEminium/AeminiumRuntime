package aeminium.runtime.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_f implements aeminium.runtime.Body {
  Fibonacci_f(){
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    new Fibonacci_f_1_if(this);
  }
  public int n;
  void schedule(  java.util.Collection<aeminium.runtime.Task> ae_deps,  int n){
    this.n=n;
    AeminiumHelper.schedule(this.ae_task,AeminiumHelper.NO_PARENT,ae_deps);
  }
  public volatile int ae_ret;
  public aeminium.runtime.Task ae_task;
}
