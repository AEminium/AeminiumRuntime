package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_tan_1_invoke_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_tan_1_invoke_1_invoke(  Complex_tan_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_sin(this,this.ae_parent.ae_parent.ae_this);
 else     this.ae_ret=this.ae_parent.ae_parent.ae_this.sin();
    if (rt.parallelize())     new Complex_sin(this,this.ae_parent.ae_parent.ae_this);
 else     this.ae_ret=this.ae_parent.ae_parent.ae_this.sin();
  }
  public Complex_tan_1_invoke ae_parent;
}
