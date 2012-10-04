package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_4_whileloop extends FFT_createRandomComplexArray_4_while implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_4_whileloop(  FFT_createRandomComplexArray_4_while ae_parent){
    this.ae_previous=ae_parent;
    this.ae_parent=ae_parent.ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_previous.ae_FFT_createRandomComplexArray_4_while_1_block.ae_FFT_createRandomComplexArray_4_while_1_block_2_exprstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_previous.ae_parent.ae_FFT_createRandomComplexArray_3_varstmt.i < this.ae_previous.ae_parent.n) {
      this.ae_FFT_createRandomComplexArray_4_while_1_block=new FFT_createRandomComplexArray_4_whileloop_1_block(this);
      this.ae_FFT_createRandomComplexArray_4_while=new FFT_createRandomComplexArray_4_whileloop(this);
    }
  }
}
