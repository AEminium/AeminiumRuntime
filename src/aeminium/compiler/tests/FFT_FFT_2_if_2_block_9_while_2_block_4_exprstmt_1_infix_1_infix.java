package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_1_infix_1_infix implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_1_infix_1_infix(  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_1_infix ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_4_while.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_1_varstmt.N / 2;
  }
  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_1_infix_1_infix(){
  }
  public volatile int ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_1_infix ae_parent;
}
