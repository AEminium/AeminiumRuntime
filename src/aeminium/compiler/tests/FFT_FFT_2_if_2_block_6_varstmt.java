package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_6_varstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_6_varstmt(  FFT_FFT_2_if_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_6_varstmt_1_invoke=new FFT_FFT_2_if_2_block_6_varstmt_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_6_varstmt_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    r=this.ae_FFT_FFT_2_if_2_block_6_varstmt_1_invoke.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block ae_parent;
  public FFT_FFT_2_if_2_block_6_varstmt_1_invoke ae_FFT_FFT_2_if_2_block_6_varstmt_1_invoke;
  public volatile Complex[] r;
}
