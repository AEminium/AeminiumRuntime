package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_plus implements aeminium.runtime.Body {
  Complex_plus(  aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent,  Complex ae_this,  Complex b){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.b=b;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_plus_1_varstmt=new Complex_plus_1_varstmt(this);
    this.ae_Complex_plus_2_varstmt=new Complex_plus_2_varstmt(this);
    this.ae_Complex_plus_3_ret=new Complex_plus_3_ret(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_plus_1_varstmt.ae_task,this.ae_Complex_plus_2_varstmt.ae_task,this.ae_Complex_plus_3_ret.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile Complex ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Complex> ae_parent;
  public Complex ae_this;
  public Complex b;
  public aeminium.runtime.Task ae_task;
  public Complex_plus_1_varstmt ae_Complex_plus_1_varstmt;
  public Complex_plus_2_varstmt ae_Complex_plus_2_varstmt;
  public Complex_plus_3_ret ae_Complex_plus_3_ret;
}
