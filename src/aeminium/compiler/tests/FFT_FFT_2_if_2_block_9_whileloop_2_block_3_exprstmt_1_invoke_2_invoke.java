package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_whileloop_2_block_3_exprstmt_1_invoke_2_invoke extends FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_whileloop_2_block_3_exprstmt_1_invoke_2_invoke(  FFT_FFT_2_if_2_block_9_whileloop_2_block_3_exprstmt_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_previous.ae_FFT_FFT_2_if_2_block_9_while_2_block.ae_FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_times(this,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.wk,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_6_varstmt.r[this.ae_parent.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k]);
 else     this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.wk.times(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_6_varstmt.r[this.ae_parent.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k]);
  }
}
