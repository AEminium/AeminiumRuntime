package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_2_varstmt implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_2_varstmt(  FFT_createRandomComplexArray ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    x=new Complex[this.ae_parent.n];
  }
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray ae_parent;
  public volatile Complex[] x;
}
