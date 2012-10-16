package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_toString_2_if_2_if_1_ret implements aeminium.runtime.Body {
  Complex_toString_2_if_2_if_1_ret(  Complex_toString_2_if_2_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_toString_2_if_2_if_1_ret_1_field=new Complex_toString_2_if_2_if_1_ret_1_field(this);
    this.ae_Complex_toString_2_if_2_if_1_ret_2_paren=new Complex_toString_2_if_2_if_1_ret_2_paren(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_toString_2_if_2_if_1_ret_1_field.ae_task,this.ae_Complex_toString_2_if_2_if_1_ret_2_paren.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_parent.ae_parent.ae_ret=this.ae_Complex_toString_2_if_2_if_1_ret_1_field.ae_ret + " - " + this.ae_Complex_toString_2_if_2_if_1_ret_2_paren.ae_ret+ "i";
    if (this.ae_parent.ae_parent.ae_parent.ae_parent != null)     this.ae_parent.ae_parent.ae_parent.ae_parent.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public Complex_toString_2_if_2_if ae_parent;
  public Complex_toString_2_if_2_if_1_ret_1_field ae_Complex_toString_2_if_2_if_1_ret_1_field;
  public Complex_toString_2_if_2_if_1_ret_2_paren ae_Complex_toString_2_if_2_if_1_ret_2_paren;
}
