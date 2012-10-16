package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_toString implements aeminium.runtime.Body {
  Complex_toString(  aeminium.runtime.CallerBodyWithReturn<String> ae_parent,  Complex ae_this){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_this.im == 0) {
      this.ae_Complex_toString_1_ret=new Complex_toString_1_ret(this);
    }
 else {
      this.ae_Complex_toString_2_if=new Complex_toString_2_if(this);
    }
  }
  public volatile String ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<String> ae_parent;
  public Complex ae_this;
  public aeminium.runtime.Task ae_task;
  public Complex_toString_2_if ae_Complex_toString_2_if;
  public Complex_toString_1_ret ae_Complex_toString_1_ret;
}
