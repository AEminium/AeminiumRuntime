package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT_2_if_2_block_7_varstmt implements aeminium.runtime.Body {
  FFT_FFT_2_if_2_block_7_varstmt(  FFT_FFT_2_if_2_block ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    y=new Complex[this.ae_parent.ae_parent.ae_parent.ae_FFT_FFT_1_varstmt.N];
  }
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_2_if_2_block ae_parent;
  public volatile Complex[] y;
}
