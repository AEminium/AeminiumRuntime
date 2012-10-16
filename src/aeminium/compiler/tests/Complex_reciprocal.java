package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_reciprocal implements aeminium.runtime.Body {
  Complex_reciprocal(  aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent,  Complex ae_this){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_reciprocal_1_varstmt=new Complex_reciprocal_1_varstmt(this);
    this.ae_Complex_reciprocal_2_ret=new Complex_reciprocal_2_ret(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_reciprocal_1_varstmt.ae_task,this.ae_Complex_reciprocal_2_ret.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile Complex ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent;
  public Complex ae_this;
  public aeminium.runtime.Task ae_task;
  public Complex_reciprocal_1_varstmt ae_Complex_reciprocal_1_varstmt;
  public Complex_reciprocal_2_ret ae_Complex_reciprocal_2_ret;
}
