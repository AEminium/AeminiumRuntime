package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex_tan_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  Complex_tan_1_invoke(  Complex_tan ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Complex_tan_1_invoke_1_invoke=new Complex_tan_1_invoke_1_invoke(this);
    this.ae_Complex_tan_1_invoke_2_invoke=new Complex_tan_1_invoke_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Complex_tan_1_invoke_1_invoke.ae_task,this.ae_Complex_tan_1_invoke_2_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_divides(this,this.ae_Complex_tan_1_invoke_1_invoke.ae_ret,this.ae_Complex_tan_1_invoke_2_invoke.ae_ret);
 else     this.ae_ret=this.ae_Complex_tan_1_invoke_1_invoke.ae_ret.divides(this.ae_Complex_tan_1_invoke_2_invoke.ae_ret);
  }
  public Complex_tan ae_parent;
  public Complex_tan_1_invoke_1_invoke ae_Complex_tan_1_invoke_1_invoke;
  public Complex_tan_1_invoke_2_invoke ae_Complex_tan_1_invoke_2_invoke;
}
