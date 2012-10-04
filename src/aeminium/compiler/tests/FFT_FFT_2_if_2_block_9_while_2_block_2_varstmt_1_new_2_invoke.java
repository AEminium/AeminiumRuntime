package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke(  FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=Math.sin(this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt.kth);
  }
  FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke(){
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new ae_parent;
}
