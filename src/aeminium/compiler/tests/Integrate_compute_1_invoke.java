package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_compute_1_invoke extends aeminium.runtime.CallerBodyWithReturn<Double> implements aeminium.runtime.Body {
  Integrate_compute_1_invoke(  Integrate_compute ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Integrate_compute_1_invoke_1_invoke=new Integrate_compute_1_invoke_1_invoke(this);
    this.ae_Integrate_compute_1_invoke_2_invoke=new Integrate_compute_1_invoke_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Integrate_compute_1_invoke_1_invoke.ae_task,this.ae_Integrate_compute_1_invoke_2_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Integrate_computeRec(this,this.ae_parent.l,this.ae_parent.r,this.ae_Integrate_compute_1_invoke_1_invoke.ae_ret,this.ae_Integrate_compute_1_invoke_2_invoke.ae_ret,0);
 else     this.ae_ret=Integrate.computeRec(this.ae_parent.l,this.ae_parent.r,this.ae_Integrate_compute_1_invoke_1_invoke.ae_ret,this.ae_Integrate_compute_1_invoke_2_invoke.ae_ret,0);
  }
  public Integrate_compute ae_parent;
  public Integrate_compute_1_invoke_1_invoke ae_Integrate_compute_1_invoke_1_invoke;
  public Integrate_compute_1_invoke_2_invoke ae_Integrate_compute_1_invoke_2_invoke;
}
