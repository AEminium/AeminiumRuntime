package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_Complex implements aeminium.runtime.Body {
  Complex_Complex(  aeminium.runtime.CallerBody ae_parent,  Complex ae_this,  double real,  double imag){
    this.ae_parent=ae_parent;
    this.ae_this=ae_this;
    this.real=real;
    this.imag=imag;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_Complex_1_exprstmt=new Complex_Complex_1_exprstmt(this);
    this.ae_Complex_Complex_2_exprstmt=new Complex_Complex_2_exprstmt(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_Complex_1_exprstmt.ae_task,this.ae_Complex_Complex_2_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public aeminium.runtime.CallerBody ae_parent;
  public Complex ae_this;
  public double real;
  public double imag;
  public aeminium.runtime.Task ae_task;
  public Complex_Complex_1_exprstmt ae_Complex_Complex_1_exprstmt;
  public Complex_Complex_2_exprstmt ae_Complex_Complex_2_exprstmt;
}
