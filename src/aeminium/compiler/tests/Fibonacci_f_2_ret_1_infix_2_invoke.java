package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci_f_2_ret_1_infix_2_invoke extends aeminium.runtime.CallerBodyWithReturn<Long> implements aeminium.runtime.Body {
  Fibonacci_f_2_ret_1_infix_2_invoke(  Fibonacci_f_2_ret_1_infix ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Fibonacci_f_2_ret_1_infix_2_invoke_1_infix=new Fibonacci_f_2_ret_1_infix_2_invoke_1_infix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Fibonacci_f_2_ret_1_infix_2_invoke_1_infix.ae_task,this.ae_parent.ae_parent.ae_parent.ae_Fibonacci_f_1_if.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (!this.ae_parent.ae_parent.ae_parent.ae_finished) {
      if (rt.parallelize())       new Fibonacci_f(this,this.ae_Fibonacci_f_2_ret_1_infix_2_invoke_1_infix.ae_ret);
 else       this.ae_ret=Fibonacci.f(this.ae_Fibonacci_f_2_ret_1_infix_2_invoke_1_infix.ae_ret);
    }
  }
  public Fibonacci_f_2_ret_1_infix ae_parent;
  public Fibonacci_f_2_ret_1_infix_2_invoke_1_infix ae_Fibonacci_f_2_ret_1_infix_2_invoke_1_infix;
}
