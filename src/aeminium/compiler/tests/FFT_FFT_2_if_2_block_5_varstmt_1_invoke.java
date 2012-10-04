package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_5_varstmt_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex[]> implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_5_varstmt_1_invoke(  FFT_FFT_2_if_2_block_5_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_4_while.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new FFT_FFT(this,this.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_1_varstmt.even);
 else     this.ae_ret=FFT.FFT(this.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_1_varstmt.even);
  }
  public FFT_FFT_2_if_2_block_5_varstmt ae_parent;
}
