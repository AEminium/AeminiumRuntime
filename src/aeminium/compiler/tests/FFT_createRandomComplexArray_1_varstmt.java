package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_1_varstmt implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_1_varstmt(  FFT_createRandomComplexArray ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_createRandomComplexArray_1_varstmt_1_new=new FFT_createRandomComplexArray_1_varstmt_1_new(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_createRandomComplexArray_1_varstmt_1_new.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    r=this.ae_FFT_createRandomComplexArray_1_varstmt_1_new.ae_ret;
  }
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray ae_parent;
  public FFT_createRandomComplexArray_1_varstmt_1_new ae_FFT_createRandomComplexArray_1_varstmt_1_new;
  public volatile Random r;
}
