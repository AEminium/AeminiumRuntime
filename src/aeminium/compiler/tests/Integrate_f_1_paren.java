package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate_f_1_paren implements aeminium.runtime.Body {
  Integrate_f_1_paren(  Integrate_f ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.ae_parent.x * this.ae_parent.x + 1.0);
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public Integrate_f ae_parent;
}
