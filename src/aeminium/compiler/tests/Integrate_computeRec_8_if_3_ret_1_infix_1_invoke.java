package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_8_if_3_ret_1_infix_1_invoke extends aeminium.runtime.CallerBodyWithReturn<Double> implements aeminium.runtime.Body {
  Integrate_computeRec_8_if_3_ret_1_infix_1_invoke(  Integrate_computeRec_8_if_3_ret_1_infix ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.ae_Integrate_computeRec_3_varstmt_1_invoke.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_2_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_6_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Integrate_computeRec(this,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_2_varstmt.c,this.ae_parent.ae_parent.ae_parent.ae_parent.r,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.fc,this.ae_parent.ae_parent.ae_parent.ae_parent.fr,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_6_varstmt.ar);
 else     this.ae_ret=Integrate.computeRec(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_2_varstmt.c,this.ae_parent.ae_parent.ae_parent.ae_parent.r,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_3_varstmt.fc,this.ae_parent.ae_parent.ae_parent.ae_parent.fr,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_Integrate_computeRec_6_varstmt.ar);
  }
  public Integrate_computeRec_8_if_3_ret_1_infix ae_parent;
}
