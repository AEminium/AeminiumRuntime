package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_compute implements aeminium.runtime.Body {
  Integrate_compute(  aeminium.runtime.CallerBodyWithReturn<Double> ae_parent,  double l,  double r){
    this.ae_parent=ae_parent;
    this.l=l;
    this.r=r;
    this.ae_finished=false;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_compute_1_invoke=new Integrate_compute_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_compute_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_Integrate_compute_1_invoke.ae_ret;
    if (this.ae_parent != null)     this.ae_parent.ae_ret=this.ae_ret;
    this.ae_finished=true;
  }
  public volatile double ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Double> ae_parent;
  public double l;
  public double r;
  public aeminium.runtime.Task ae_task;
  public volatile boolean ae_finished;
  public Integrate_compute_1_invoke ae_Integrate_compute_1_invoke;
}
