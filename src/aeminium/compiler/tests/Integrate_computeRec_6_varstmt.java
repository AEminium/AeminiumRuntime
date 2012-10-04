package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec_6_varstmt implements aeminium.runtime.Body {
  Integrate_computeRec_6_varstmt(  Integrate_computeRec ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_computeRec_6_varstmt_1_paren=new Integrate_computeRec_6_varstmt_1_paren(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_computeRec_6_varstmt_1_paren.ae_task,this.ae_parent.ae_Integrate_computeRec_4_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    ar=this.ae_Integrate_computeRec_6_varstmt_1_paren.ae_ret * this.ae_parent.ae_Integrate_computeRec_4_varstmt.hh;
  }
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec ae_parent;
  public Integrate_computeRec_6_varstmt_1_paren ae_Integrate_computeRec_6_varstmt_1_paren;
  public volatile double ar;
}
