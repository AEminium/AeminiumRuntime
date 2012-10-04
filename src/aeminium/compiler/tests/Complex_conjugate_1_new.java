package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_conjugate_1_new extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_conjugate_1_new(  Complex_conjugate ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_conjugate_1_new_1_field=new Complex_conjugate_1_new_1_field(this);
    this.ae_Complex_conjugate_1_new_2_prefix=new Complex_conjugate_1_new_2_prefix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_conjugate_1_new_1_field.ae_task,this.ae_Complex_conjugate_1_new_2_prefix.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=new Complex(this.ae_Complex_conjugate_1_new_1_field.ae_ret,this.ae_Complex_conjugate_1_new_2_prefix.ae_ret);
  }
  public Complex_conjugate ae_parent;
  public Complex_conjugate_1_new_1_field ae_Complex_conjugate_1_new_1_field;
  public Complex_conjugate_1_new_2_prefix ae_Complex_conjugate_1_new_2_prefix;
}
