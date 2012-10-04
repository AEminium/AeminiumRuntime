package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci_f implements aeminium.runtime.Body {
  Fibonacci_f(  aeminium.runtime.CallerBodyWithReturn<Long> ae_parent,  int n){
    this.ae_parent=ae_parent;
    this.n=n;
    this.ae_finished=false;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_Fibonacci_f_1_if=new Fibonacci_f_1_if(this);
    this.ae_Fibonacci_f_2_ret=new Fibonacci_f_2_ret(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_Fibonacci_f_1_if.ae_task,this.ae_Fibonacci_f_2_ret.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile long ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Long> ae_parent;
  public int n;
  public aeminium.runtime.Task ae_task;
  public volatile boolean ae_finished;
  public Fibonacci_f_1_if ae_Fibonacci_f_1_if;
  public Fibonacci_f_2_ret ae_Fibonacci_f_2_ret;
}
