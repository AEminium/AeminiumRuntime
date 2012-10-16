package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main_4_while_1_field implements aeminium.runtime.Body {
  FFT_main_4_while_1_field(  FFT_main_4_while ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_FFT_main_2_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=(this.ae_parent.ae_parent.ae_FFT_main_2_varstmt.output).length;
  }
  FFT_main_4_while_1_field(){
  }
  public volatile int ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_main_4_while ae_parent;
}
