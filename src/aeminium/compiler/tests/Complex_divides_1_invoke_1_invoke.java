package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_divides_1_invoke_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_divides_1_invoke_1_invoke(  Complex_divides_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_reciprocal(this,(this.ae_parent.ae_parent.b));
 else     this.ae_ret=(this.ae_parent.ae_parent.b).reciprocal();
  }
  public Complex_divides_1_invoke ae_parent;
}
