package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_4_whileloop extends FFT_FFT_2_if_2_block_4_while implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_4_whileloop(  FFT_FFT_2_if_2_block_4_while ae_parent){
    this.ae_previous=ae_parent;
    this.ae_parent=ae_parent.ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_4_while_1_infix=new FFT_FFT_2_if_2_block_4_whileloop_1_infix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_4_while_1_infix.ae_task,this.ae_previous.ae_FFT_FFT_2_if_2_block_4_while_2_block.ae_FFT_FFT_2_if_2_block_4_while_2_block_3_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k < this.ae_FFT_FFT_2_if_2_block_4_while_1_infix.ae_ret) {
      this.ae_FFT_FFT_2_if_2_block_4_while_2_block=new FFT_FFT_2_if_2_block_4_whileloop_2_block(this);
      this.ae_FFT_FFT_2_if_2_block_4_while=new FFT_FFT_2_if_2_block_4_whileloop(this);
    }
  }
}
