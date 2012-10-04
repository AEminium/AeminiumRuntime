package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_phase_1_invoke implements aeminium.runtime.Body {
  Complex_phase_1_invoke(  Complex_phase ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_phase_1_invoke_1_field=new Complex_phase_1_invoke_1_field(this);
    this.ae_Complex_phase_1_invoke_2_field=new Complex_phase_1_invoke_2_field(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_phase_1_invoke_1_field.ae_task,this.ae_Complex_phase_1_invoke_2_field.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=Math.atan2(this.ae_Complex_phase_1_invoke_1_field.ae_ret,this.ae_Complex_phase_1_invoke_2_field.ae_ret);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Complex_phase ae_parent;
  public Complex_phase_1_invoke_1_field ae_Complex_phase_1_invoke_1_field;
  public Complex_phase_1_invoke_2_field ae_Complex_phase_1_invoke_2_field;
}
