package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main_4_while_2_block implements aeminium.runtime.Body {
  FFT_main_4_while_2_block(  FFT_main_4_while ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_main_4_while_2_block_1_exprstmt=new FFT_main_4_while_2_block_1_exprstmt(this);
    this.ae_FFT_main_4_while_2_block_2_exprstmt=new FFT_main_4_while_2_block_2_exprstmt(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_main_4_while_2_block_1_exprstmt.ae_task,this.ae_FFT_main_4_while_2_block_2_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  FFT_main_4_while_2_block(){
  }
  public aeminium.runtime.Task ae_task;
  public FFT_main_4_while ae_parent;
  public FFT_main_4_while_2_block_1_exprstmt ae_FFT_main_4_while_2_block_1_exprstmt;
  public FFT_main_4_while_2_block_2_exprstmt ae_FFT_main_4_while_2_block_2_exprstmt;
}
