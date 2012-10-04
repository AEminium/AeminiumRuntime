package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_times_2_varstmt implements aeminium.runtime.Body {
  Complex_times_2_varstmt(  Complex_times ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_times_2_varstmt_1_infix=new Complex_times_2_varstmt_1_infix(this);
    this.ae_Complex_times_2_varstmt_2_infix=new Complex_times_2_varstmt_2_infix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_times_2_varstmt_1_infix.ae_task,this.ae_Complex_times_2_varstmt_2_infix.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    imag=this.ae_Complex_times_2_varstmt_1_infix.ae_ret + this.ae_Complex_times_2_varstmt_2_infix.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public Complex_times ae_parent;
  public Complex_times_2_varstmt_1_infix ae_Complex_times_2_varstmt_1_infix;
  public Complex_times_2_varstmt_2_infix ae_Complex_times_2_varstmt_2_infix;
  public volatile double imag;
}
