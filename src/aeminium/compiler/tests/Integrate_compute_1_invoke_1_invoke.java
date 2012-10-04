package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_compute_1_invoke_1_invoke extends aeminium.runtime.CallerBodyWithReturn<Double> implements aeminium.runtime.Body {
  Integrate_compute_1_invoke_1_invoke(  Integrate_compute_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Integrate_f(this,this.ae_parent.ae_parent.l);
 else     this.ae_ret=Integrate.f(this.ae_parent.ae_parent.l);
  }
  public Integrate_compute_1_invoke ae_parent;
}
