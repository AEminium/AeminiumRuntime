package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_1_varstmt_1_paren implements aeminium.runtime.Body {
  Integrate_computeRec_1_varstmt_1_paren(  Integrate_computeRec_1_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.ae_parent.ae_parent.r - this.ae_parent.ae_parent.l);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec_1_varstmt ae_parent;
}
