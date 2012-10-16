package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_f implements aeminium.runtime.Body {
  Integrate_f(  aeminium.runtime.CallerBodyWithReturn<Double> ae_parent,  double x){
    this.ae_parent=ae_parent;
    this.x=x;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.x * this.x + 1.0) * this.x;
    if (this.ae_parent != null)     this.ae_parent.ae_ret=this.ae_ret;
  }
  public volatile double ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Double> ae_parent;
  public double x;
  public aeminium.runtime.Task ae_task;
}
