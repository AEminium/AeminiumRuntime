package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt(  FFT_FFT_2_if_2_block_9_while_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix=new FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix.ae_task,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_8_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    kth=this.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix.ae_ret * this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k * Math.PI / this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_1_varstmt.N;
  }
  FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt(){
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while_2_block ae_parent;
  public FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt_1_prefix;
  public volatile double kth;
}
