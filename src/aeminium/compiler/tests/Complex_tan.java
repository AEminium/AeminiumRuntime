package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_tan implements aeminium.runtime.Body {
  Complex_tan(  aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent,  Complex ae_this){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_tan_1_invoke=new Complex_tan_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_tan_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_Complex_tan_1_invoke.ae_ret;
    if (this.ae_parent != null)     this.ae_parent.ae_ret=this.ae_ret;
  }
  public volatile Complex ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent;
  public Complex ae_this;
  public aeminium.runtime.Task ae_task;
  public Complex_tan_1_invoke ae_Complex_tan_1_invoke;
}
