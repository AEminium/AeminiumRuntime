package aeminium.runtime.profiler.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;
class Fibonacci_f_1_if_2_ret implements aeminium.runtime.Body {
  Fibonacci_f_1_if_2_ret(  Fibonacci_f_1_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Fibonacci_f_1_if_2_ret_1_invoke=new Fibonacci_f_1_if_2_ret_1_invoke(this);
    this.ae_Fibonacci_f_1_if_2_ret_2_invoke=new Fibonacci_f_1_if_2_ret_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent.ae_task,java.util.Arrays.asList(this.ae_Fibonacci_f_1_if_2_ret_1_invoke.ae_task,this.ae_Fibonacci_f_1_if_2_ret_2_invoke.ae_task,this.ae_Fibonacci_f_1_if_2_ret_1_invoke.ae_deferred.ae_task,this.ae_Fibonacci_f_1_if_2_ret_2_invoke.ae_deferred.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_parent.ae_ret=this.ae_Fibonacci_f_1_if_2_ret_1_invoke.ae_deferred.ae_ret + this.ae_Fibonacci_f_1_if_2_ret_2_invoke.ae_deferred.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public Fibonacci_f_1_if ae_parent;
  public Fibonacci_f_1_if_2_ret_1_invoke ae_Fibonacci_f_1_if_2_ret_1_invoke;
  public Fibonacci_f_1_if_2_ret_2_invoke ae_Fibonacci_f_1_if_2_ret_2_invoke;
}
