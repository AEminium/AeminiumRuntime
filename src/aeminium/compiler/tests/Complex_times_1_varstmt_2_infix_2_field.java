package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_times_1_varstmt_2_infix_2_field implements aeminium.runtime.Body {
  Complex_times_1_varstmt_2_infix_2_field(  Complex_times_1_varstmt_2_infix ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_Complex_times_1_varstmt_1_infix.ae_Complex_times_1_varstmt_1_infix_2_field.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.ae_parent.ae_parent.ae_parent.b).im;
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Complex_times_1_varstmt_2_infix ae_parent;
}
