package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new extends aeminium.runtime.CallerBodyWithReturn<aeminium.compiler.tests.Complex> implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new(  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke=new FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    this.ae_ret=new Complex(2 * this.ae_FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke.ae_ret - 1,0);
  }
  FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new(){
  }
  public FFT_createRandomComplexArray_4_while_1_block_1_exprstmt ae_parent;
  public FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke ae_FFT_createRandomComplexArray_4_while_1_block_1_exprstmt_1_new_1_invoke;
}
