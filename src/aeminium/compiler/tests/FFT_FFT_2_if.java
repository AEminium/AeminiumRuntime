package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if implements aeminium.runtime.Body {
  FFT_FFT_2_if(  FFT_FFT ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_FFT_FFT_1_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_parent.ae_FFT_FFT_1_varstmt.N == 1) {
      this.ae_FFT_FFT_2_if_1_ret=new FFT_FFT_2_if_1_ret(this);
    }
 else {
      this.ae_FFT_FFT_2_if_2_block=new FFT_FFT_2_if_2_block(this);
    }
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT ae_parent;
  public FFT_FFT_2_if_2_block ae_FFT_FFT_2_if_2_block;
  public FFT_FFT_2_if_1_ret ae_FFT_FFT_2_if_1_ret;
}
