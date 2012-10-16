package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main_1_varstmt_1_invoke extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex[]> implements aeminium.runtime.Body {
  FFT_main_1_varstmt_1_invoke(  FFT_main_1_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (rt.parallelize())     new FFT_createRandomComplexArray(this,4194304,524288);
 else     this.ae_ret=FFT.createRandomComplexArray(4194304,524288);
  }
  public FFT_main_1_varstmt ae_parent;
}
