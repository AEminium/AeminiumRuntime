package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_toString_2_if implements aeminium.runtime.Body {
  Complex_toString_2_if(  Complex_toString ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_parent.ae_this.re == 0) {
      this.ae_Complex_toString_2_if_1_ret=new Complex_toString_2_if_1_ret(this);
    }
 else {
      this.ae_Complex_toString_2_if_2_if=new Complex_toString_2_if_2_if(this);
    }
  }
  public aeminium.runtime.Task ae_task;
  public Complex_toString ae_parent;
  public Complex_toString_2_if_2_if ae_Complex_toString_2_if_2_if;
  public Complex_toString_2_if_1_ret ae_Complex_toString_2_if_1_ret;
}
