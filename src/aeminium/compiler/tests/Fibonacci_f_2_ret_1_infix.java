package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci_f_2_ret_1_infix implements aeminium.runtime.Body {
  Fibonacci_f_2_ret_1_infix(  Fibonacci_f_2_ret ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Fibonacci_f_2_ret_1_infix_1_invoke=new Fibonacci_f_2_ret_1_infix_1_invoke(this);
    this.ae_Fibonacci_f_2_ret_1_infix_2_invoke=new Fibonacci_f_2_ret_1_infix_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Fibonacci_f_2_ret_1_infix_1_invoke.ae_task,this.ae_Fibonacci_f_2_ret_1_infix_2_invoke.ae_task,this.ae_parent.ae_parent.ae_Fibonacci_f_1_if.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (!this.ae_parent.ae_parent.ae_finished) {
      this.ae_ret=this.ae_Fibonacci_f_2_ret_1_infix_1_invoke.ae_ret + this.ae_Fibonacci_f_2_ret_1_infix_2_invoke.ae_ret;
    }
  }
  public volatile long ae_ret;
  public aeminium.runtime.Task ae_task;
  public Fibonacci_f_2_ret ae_parent;
  public Fibonacci_f_2_ret_1_infix_1_invoke ae_Fibonacci_f_2_ret_1_infix_1_invoke;
  public Fibonacci_f_2_ret_1_infix_2_invoke ae_Fibonacci_f_2_ret_1_infix_2_invoke;
}
