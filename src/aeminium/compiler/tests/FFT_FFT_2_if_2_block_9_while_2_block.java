package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_9_while_2_block implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_9_while_2_block(  FFT_FFT_2_if_2_block_9_while ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt=new FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt=new FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt=new FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt=new FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt(this);
    this.ae_FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt=new FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  FFT_FFT_2_if_2_block_9_while_2_block(){
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block_9_while ae_parent;
  public FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt ae_FFT_FFT_2_if_2_block_9_while_2_block_1_varstmt;
  public FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt ae_FFT_FFT_2_if_2_block_9_while_2_block_2_varstmt;
  public FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt ae_FFT_FFT_2_if_2_block_9_while_2_block_3_exprstmt;
  public FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt ae_FFT_FFT_2_if_2_block_9_while_2_block_4_exprstmt;
  public FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt ae_FFT_FFT_2_if_2_block_9_while_2_block_5_exprstmt;
}
