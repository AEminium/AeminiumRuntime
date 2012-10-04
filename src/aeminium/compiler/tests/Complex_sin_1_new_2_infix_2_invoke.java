package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_sin_1_new_2_infix_2_invoke implements aeminium.runtime.Body {
  Complex_sin_1_new_2_infix_2_invoke(  Complex_sin_1_new_2_infix ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=Math.sinh(this.ae_parent.ae_parent.ae_parent.ae_this.im);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Complex_sin_1_new_2_infix ae_parent;
}
