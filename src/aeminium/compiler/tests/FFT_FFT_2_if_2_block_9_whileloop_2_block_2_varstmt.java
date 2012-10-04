package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt extends FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt(  FFT_FFT_2_if_2_block_9_whileloop_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new=new FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt_1_new(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new.ae_task,this.ae_parent.ae_parent.ae_previous.ae_FFT_FFT_2_if_2_block_9_while_2_block.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_2_invoke.ae_task,this.ae_parent.ae_parent.ae_previous.ae_FFT_FFT_2_if_2_block_9_while_2_block.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    wk=this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new.ae_ret;
  }
}
