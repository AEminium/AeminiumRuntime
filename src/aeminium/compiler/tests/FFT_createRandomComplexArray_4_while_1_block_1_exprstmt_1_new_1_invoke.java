package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke(  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_createRandomComplexArray_1_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=this.ae_parent.ae_parent.ae_parent.ae_parent.ae_parent.ae_FFT_createRandomComplexArray_1_varstmt.r.nextDouble();
  }
  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke(){
  }
  public volatile double ae_ret;
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new ae_parent;
}
