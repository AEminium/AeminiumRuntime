package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_8_if implements aeminium.runtime.Body {
  Integrate_computeRec_8_if(  Integrate_computeRec ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_computeRec_8_if_1_invoke=new Integrate_computeRec_8_if_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_computeRec_8_if_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_Integrate_computeRec_8_if_1_invoke.ae_ret <= 1.0e-14) {
      this.ae_Integrate_computeRec_8_if_2_ret=new Integrate_computeRec_8_if_2_ret(this);
    }
 else {
      this.ae_Integrate_computeRec_8_if_3_ret=new Integrate_computeRec_8_if_3_ret(this);
    }
  }
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec ae_parent;
  public Integrate_computeRec_8_if_1_invoke ae_Integrate_computeRec_8_if_1_invoke;
  public Integrate_computeRec_8_if_2_ret ae_Integrate_computeRec_8_if_2_ret;
  public Integrate_computeRec_8_if_3_ret ae_Integrate_computeRec_8_if_3_ret;
}
