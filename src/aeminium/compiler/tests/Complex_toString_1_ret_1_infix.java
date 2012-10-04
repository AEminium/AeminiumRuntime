package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_toString_1_ret_1_infix implements aeminium.runtime.Body {
  Complex_toString_1_ret_1_infix(  Complex_toString_1_ret ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_parent.ae_parent.ae_this.re + "";
  }
  public volatile java.lang.String ae_ret;
  public aeminium.runtime.Task ae_task;
  public Complex_toString_1_ret ae_parent;
}
