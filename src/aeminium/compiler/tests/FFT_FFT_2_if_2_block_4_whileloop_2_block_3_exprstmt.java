package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_4_whileloop_2_block_3_exprstmt extends FFT_FFT_2_if_2_block_4_while_2_block_3_exprstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_4_whileloop_2_block_3_exprstmt(  FFT_FFT_2_if_2_block_4_whileloop_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_FFT_FFT_2_if_2_block_4_while_2_block_1_exprstmt.ae_task,this.ae_parent.ae_FFT_FFT_2_if_2_block_4_while_2_block_2_exprstmt.ae_FFT_FFT_2_if_2_block_4_while_2_block_2_exprstmt_1_arrayidx.ae_FFT_FFT_2_if_2_block_4_while_2_block_2_exprstmt_1_arrayidx_1_infix.ae_task,this.ae_parent.ae_FFT_FFT_2_if_2_block_4_while_2_block_2_exprstmt.ae_task,this.ae_parent.ae_FFT_FFT_2_if_2_block_4_while_2_block_1_exprstmt.ae_FFT_FFT_2_if_2_block_4_while_2_block_1_exprstmt_1_arrayidx.ae_FFT_FFT_2_if_2_block_4_while_2_block_1_exprstmt_1_arrayidx_1_infix.ae_task,this.ae_parent.ae_parent.ae_previous.ae_FFT_FFT_2_if_2_block_4_while_2_block.ae_FFT_FFT_2_if_2_block_4_while_2_block_3_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    ++this.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k;
  }
}
