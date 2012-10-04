package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci_f_1_if implements aeminium.runtime.Body {
  Fibonacci_f_1_if(  Fibonacci_f ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_parent.n < 2) {
      this.ae_Fibonacci_f_1_if_1_ret=new Fibonacci_f_1_if_1_ret(this);
    }
  }
  public aeminium.runtime.Task ae_task;
  public Fibonacci_f ae_parent;
  public Fibonacci_f_1_if_1_ret ae_Fibonacci_f_1_if_1_ret;
}
