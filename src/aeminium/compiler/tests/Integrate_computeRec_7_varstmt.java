package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_7_varstmt implements aeminium.runtime.Body {
  Integrate_computeRec_7_varstmt(  Integrate_computeRec ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_Integrate_computeRec_6_varstmt.ae_task,this.ae_parent.ae_Integrate_computeRec_5_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    alr=this.ae_parent.ae_Integrate_computeRec_5_varstmt.al + this.ae_parent.ae_Integrate_computeRec_6_varstmt.ar;
  }
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec ae_parent;
  public volatile double alr;
}
