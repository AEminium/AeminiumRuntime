package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_1_ret implements aeminium.runtime.Body {
  FFT_FFT_2_if_1_ret(  FFT_FFT_2_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_FFT_FFT_1_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_parent.ae_ret=new Complex[]{this.ae_parent.ae_parent.x[0]};
    if (this.ae_parent.ae_parent.ae_parent != null)     this.ae_parent.ae_parent.ae_parent.ae_ret=this.ae_parent.ae_parent.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if ae_parent;
}
