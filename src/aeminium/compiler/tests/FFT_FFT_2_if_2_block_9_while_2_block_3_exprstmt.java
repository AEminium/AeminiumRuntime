package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt(  FFT_FFT_2_if_2_block_9_while_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke=new FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke.ae_task,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_7_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_8_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_7_varstmt.y[this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_2_if_2_block_3_varstmt.k]=this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke.ae_ret;
  }
  FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt(){
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while_2_block ae_parent;
  public FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt_1_invoke;
}
