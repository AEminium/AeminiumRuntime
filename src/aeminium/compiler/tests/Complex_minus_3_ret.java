package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_minus_3_ret implements aeminium.runtime.Body {
  Complex_minus_3_ret(  Complex_minus ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_minus_3_ret_1_new=new Complex_minus_3_ret_1_new(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_minus_3_ret_1_new.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_ret=this.ae_Complex_minus_3_ret_1_new.ae_ret;
    if (this.ae_parent.ae_parent != null)     this.ae_parent.ae_parent.ae_ret=this.ae_parent.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public Complex_minus ae_parent;
  public Complex_minus_3_ret_1_new ae_Complex_minus_3_ret_1_new;
}
