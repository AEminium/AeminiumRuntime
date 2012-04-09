package aeminium.runtime.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_main implements aeminium.runtime.Body {
  Fibonacci_main(){
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    new Fibonacci_main_1_exprstmt(this);
  }
  public String[] args;
  void schedule(  java.util.Collection<aeminium.runtime.Task> ae_deps,  String[] args){
    this.args=args;
    AeminiumHelper.schedule(this.ae_task,AeminiumHelper.NO_PARENT,ae_deps);
  }
  public aeminium.runtime.Task ae_task;
}
