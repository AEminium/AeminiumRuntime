package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main_2_varstmt_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex[]> implements aeminium.runtime.Body {
  FFT_main_2_varstmt_1_invoke(  FFT_main_2_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_FFT_main_1_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new FFT_FFT(this,this.ae_parent.ae_parent.ae_FFT_main_1_varstmt.input);
 else     this.ae_ret=FFT.FFT(this.ae_parent.ae_parent.ae_FFT_main_1_varstmt.input);
  }
  public FFT_main_2_varstmt ae_parent;
}
