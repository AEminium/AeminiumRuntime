package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx(  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_8_exprstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.ae_FFT_FFT_2_if_2_block_5_varstmt_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.q[this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k];
    this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.q[this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k];
  }
  FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx(){
  }
  public volatile aeminium.compiler.tests.Complex ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke ae_parent;
}
