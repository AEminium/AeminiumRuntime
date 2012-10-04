package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke(  FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx=new FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_6_varstmt.ae_FFT_FFT_2_if_2_block_6_varstmt_1_invoke.ae_task,this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_6_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new Complex_times(this,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.wk,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx.ae_ret);
 else     this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.wk.times(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx.ae_ret);
  }
  FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke(){
  }
  public FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke ae_parent;
  public FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke_2_invoke_1_arrayidx;
}
