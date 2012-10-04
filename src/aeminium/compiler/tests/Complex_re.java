package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_re implements aeminium.runtime.Body {
  Complex_re(  aeminium.runtime.CallerBodyWithReturn<Double> ae_parent,  Complex ae_this){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.ae_finished=false;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_this.re;
    if (this.ae_parent != null)     this.ae_parent.ae_ret=this.ae_ret;
    this.ae_finished=true;
  }
  public volatile double ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Double> ae_parent;
  public Complex ae_this;
  public aeminium.runtime.Task ae_task;
  public volatile boolean ae_finished;
}
