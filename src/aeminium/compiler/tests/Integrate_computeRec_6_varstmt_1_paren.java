package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_6_varstmt_1_paren implements aeminium.runtime.Body {
  Integrate_computeRec_6_varstmt_1_paren(  Integrate_computeRec_6_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.ae_parent.ae_parent.fr + this.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.fc);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec_6_varstmt ae_parent;
}
