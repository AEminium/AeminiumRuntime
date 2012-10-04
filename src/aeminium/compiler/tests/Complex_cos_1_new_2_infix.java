package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_cos_1_new_2_infix implements aeminium.runtime.Body {
  Complex_cos_1_new_2_infix(  Complex_cos_1_new ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_cos_1_new_2_infix_1_prefix=new Complex_cos_1_new_2_infix_1_prefix(this);
    this.ae_Complex_cos_1_new_2_infix_2_invoke=new Complex_cos_1_new_2_infix_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_cos_1_new_2_infix_1_prefix.ae_task,this.ae_Complex_cos_1_new_2_infix_2_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_Complex_cos_1_new_2_infix_1_prefix.ae_ret * this.ae_Complex_cos_1_new_2_infix_2_invoke.ae_ret;
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Complex_cos_1_new ae_parent;
  public Complex_cos_1_new_2_infix_1_prefix ae_Complex_cos_1_new_2_infix_1_prefix;
  public Complex_cos_1_new_2_infix_2_invoke ae_Complex_cos_1_new_2_infix_2_invoke;
}
