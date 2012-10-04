package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt_1_new extends FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt_1_new(  FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_1_invoke=new FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt_1_new_1_invoke(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke=new FFT_FFT_2_if_2_block_9_whileloop_2_block_2_varstmt_1_new_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_1_invoke.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=new Complex(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_1_invoke.ae_ret,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt_1_new_2_invoke.ae_ret);
  }
}
