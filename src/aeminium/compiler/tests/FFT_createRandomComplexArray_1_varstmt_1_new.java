package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_1_varstmt_1_new implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_1_varstmt_1_new(  FFT_createRandomComplexArray_1_varstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,AeminiumHelper.NO_DEPS);
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_ret=new Random(this.ae_parent.ae_parent.seed);
  }
  public volatile java.util.Random ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray_1_varstmt ae_parent;
}
