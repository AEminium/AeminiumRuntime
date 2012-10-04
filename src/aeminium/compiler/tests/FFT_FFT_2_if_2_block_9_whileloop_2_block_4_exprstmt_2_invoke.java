package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_whileloop_2_block_4_exprstmt_2_invoke extends FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_whileloop_2_block_4_exprstmt_2_invoke(  FFT_FFT_2_if_2_block_9_whileloop_2_block_4_exprstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx=new FFT_FFT_2_if_2_block_9_whileloop_2_block_4_exprstmt_2_invoke_1_arrayidx(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_2_invoke=new FFT_FFT_2_if_2_block_9_whileloop_2_block_4_exprstmt_2_invoke_2_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_2_invoke.ae_task,this.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_FFT_2_if_2_block_5_varstmt.ae_FFT_FFT_2_if_2_block_5_varstmt_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_minus(this,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx.ae_ret,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_2_invoke.ae_ret);
 else     this.ae_ret=this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_1_arrayidx.ae_ret.minus(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt_2_invoke_2_invoke.ae_ret);
  }
}
