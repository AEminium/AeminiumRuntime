package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_8_if_1_invoke implements aeminium.runtime.Body {
  Integrate_computeRec_8_if_1_invoke(  Integrate_computeRec_8_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_Integrate_computeRec_7_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=Math.abs(this.ae_parent.ae_parent.ae_Integrate_computeRec_7_varstmt.alr - this.ae_parent.ae_parent.a);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec_8_if ae_parent;
}