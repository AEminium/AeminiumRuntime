package tests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_main_1_exprstmt implements aeminium.runtime.Body {
  Fibonacci_main_1_exprstmt(  Fibonacci_main ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Fibonacci_main_1_exprstmt_1_invoke=new Fibonacci_main_1_exprstmt_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent.ae_task,java.util.Arrays.asList(this.ae_Fibonacci_main_1_exprstmt_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public aeminium.runtime.Task ae_task;
  public Fibonacci_main ae_parent;
  public Fibonacci_main_1_exprstmt_1_invoke ae_Fibonacci_main_1_exprstmt_1_invoke;
}
