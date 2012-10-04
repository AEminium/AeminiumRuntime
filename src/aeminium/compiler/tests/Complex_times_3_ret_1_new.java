package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_times_3_ret_1_new extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_times_3_ret_1_new(  Complex_times_3_ret ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_Complex_times_2_varstmt.ae_task,this.ae_parent.ae_parent.ae_Complex_times_1_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=new Complex(this.ae_parent.ae_parent.ae_Complex_times_1_varstmt.real,this.ae_parent.ae_parent.ae_Complex_times_2_varstmt.imag);
  }
  public Complex_times_3_ret ae_parent;
}
