package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block(  FFT_FFT_2_if ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_2_if_2_block_1_varstmt=new FFT_FFT_2_if_2_block_1_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_2_varstmt=new FFT_FFT_2_if_2_block_2_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_3_varstmt=new FFT_FFT_2_if_2_block_3_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_4_while=new FFT_FFT_2_if_2_block_4_while(this);
    this.ae_FFT_FFT_2_if_2_block_5_varstmt=new FFT_FFT_2_if_2_block_5_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_6_varstmt=new FFT_FFT_2_if_2_block_6_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_7_varstmt=new FFT_FFT_2_if_2_block_7_varstmt(this);
    this.ae_FFT_FFT_2_if_2_block_8_exprstmt=new FFT_FFT_2_if_2_block_8_exprstmt(this);
    this.ae_FFT_FFT_2_if_2_block_9_while=new FFT_FFT_2_if_2_block_9_while(this);
    this.ae_FFT_FFT_2_if_2_block_10_ret=new FFT_FFT_2_if_2_block_10_ret(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_2_if_2_block_1_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_2_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_3_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_4_while.ae_task,this.ae_FFT_FFT_2_if_2_block_5_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_6_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_7_varstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_8_exprstmt.ae_task,this.ae_FFT_FFT_2_if_2_block_9_while.ae_task,this.ae_FFT_FFT_2_if_2_block_10_ret.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if ae_parent;
  public FFT_FFT_2_if_2_block_1_varstmt ae_FFT_FFT_2_if_2_block_1_varstmt;
  public FFT_FFT_2_if_2_block_2_varstmt ae_FFT_FFT_2_if_2_block_2_varstmt;
  public FFT_FFT_2_if_2_block_3_varstmt ae_FFT_FFT_2_if_2_block_3_varstmt;
  public FFT_FFT_2_if_2_block_4_while ae_FFT_FFT_2_if_2_block_4_while;
  public FFT_FFT_2_if_2_block_5_varstmt ae_FFT_FFT_2_if_2_block_5_varstmt;
  public FFT_FFT_2_if_2_block_6_varstmt ae_FFT_FFT_2_if_2_block_6_varstmt;
  public FFT_FFT_2_if_2_block_7_varstmt ae_FFT_FFT_2_if_2_block_7_varstmt;
  public FFT_FFT_2_if_2_block_8_exprstmt ae_FFT_FFT_2_if_2_block_8_exprstmt;
  public FFT_FFT_2_if_2_block_9_while ae_FFT_FFT_2_if_2_block_9_while;
  public FFT_FFT_2_if_2_block_10_ret ae_FFT_FFT_2_if_2_block_10_ret;
}
