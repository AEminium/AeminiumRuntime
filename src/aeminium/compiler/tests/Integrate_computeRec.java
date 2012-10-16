package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_computeRec implements aeminium.runtime.Body {
  Integrate_computeRec(  aeminium.runtime.CallerBodyWithReturn<Double> ae_parent,  double l,  double r,  double fl,  double fr,  double a){
    this.ae_parent=ae_parent;
    this.l=l;
    this.r=r;
    this.fl=fl;
    this.fr=fr;
    this.a=a;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_computeRec_1_varstmt=new Integrate_computeRec_1_varstmt(this);
    this.ae_Integrate_computeRec_2_varstmt=new Integrate_computeRec_2_varstmt(this);
    this.ae_Integrate_computeRec_3_varstmt=new Integrate_computeRec_3_varstmt(this);
    this.ae_Integrate_computeRec_4_varstmt=new Integrate_computeRec_4_varstmt(this);
    this.ae_Integrate_computeRec_5_varstmt=new Integrate_computeRec_5_varstmt(this);
    this.ae_Integrate_computeRec_6_varstmt=new Integrate_computeRec_6_varstmt(this);
    this.ae_Integrate_computeRec_7_varstmt=new Integrate_computeRec_7_varstmt(this);
    this.ae_Integrate_computeRec_8_if=new Integrate_computeRec_8_if(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_computeRec_1_varstmt.ae_task,this.ae_Integrate_computeRec_2_varstmt.ae_task,this.ae_Integrate_computeRec_3_varstmt.ae_task,this.ae_Integrate_computeRec_4_varstmt.ae_task,this.ae_Integrate_computeRec_5_varstmt.ae_task,this.ae_Integrate_computeRec_6_varstmt.ae_task,this.ae_Integrate_computeRec_7_varstmt.ae_task,this.ae_Integrate_computeRec_8_if.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile double ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Double> ae_parent;
  public double l;
  public double r;
  public double fl;
  public double fr;
  public double a;
  public aeminium.runtime.Task ae_task;
  public Integrate_computeRec_1_varstmt ae_Integrate_computeRec_1_varstmt;
  public Integrate_computeRec_2_varstmt ae_Integrate_computeRec_2_varstmt;
  public Integrate_computeRec_3_varstmt ae_Integrate_computeRec_3_varstmt;
  public Integrate_computeRec_4_varstmt ae_Integrate_computeRec_4_varstmt;
  public Integrate_computeRec_5_varstmt ae_Integrate_computeRec_5_varstmt;
  public Integrate_computeRec_6_varstmt ae_Integrate_computeRec_6_varstmt;
  public Integrate_computeRec_7_varstmt ae_Integrate_computeRec_7_varstmt;
  public Integrate_computeRec_8_if ae_Integrate_computeRec_8_if;
}
