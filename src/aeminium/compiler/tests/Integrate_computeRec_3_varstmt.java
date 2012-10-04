package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_3_varstmt implements aeminium.runtime.Body {
  Integrate_computeRec_3_varstmt(  Integrate_computeRec ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_computeRec_3_varstmt_1_invoke=new Integrate_computeRec_3_varstmt_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_computeRec_3_varstmt_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    fc=this.ae_Integrate_computeRec_3_varstmt_1_invoke.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec ae_parent;
  public Integrate_computeRec_3_varstmt_1_invoke ae_Integrate_computeRec_3_varstmt_1_invoke;
  public volatile double fc;
}
