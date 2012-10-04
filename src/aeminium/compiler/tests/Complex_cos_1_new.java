package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_cos_1_new extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_cos_1_new(  Complex_cos ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_cos_1_new_1_infix=new Complex_cos_1_new_1_infix(this);
    this.ae_Complex_cos_1_new_2_infix=new Complex_cos_1_new_2_infix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_cos_1_new_1_infix.ae_task,this.ae_Complex_cos_1_new_2_infix.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=new Complex(this.ae_Complex_cos_1_new_1_infix.ae_ret,this.ae_Complex_cos_1_new_2_infix.ae_ret);
  }
  public Complex_cos ae_parent;
  public Complex_cos_1_new_1_infix ae_Complex_cos_1_new_1_infix;
  public Complex_cos_1_new_2_infix ae_Complex_cos_1_new_2_infix;
}
