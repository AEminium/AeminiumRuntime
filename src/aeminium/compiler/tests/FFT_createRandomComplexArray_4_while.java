package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray_4_while implements aeminium.runtime.Body {
  FFT_createRandomComplexArray_4_while(  FFT_createRandomComplexArray ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_parent.ae_FFT_createRandomComplexArray_3_varstmt.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    if (this.ae_parent.ae_FFT_createRandomComplexArray_3_varstmt.i < this.ae_parent.n) {
      this.ae_FFT_createRandomComplexArray_4_while_1_block=new FFT_createRandomComplexArray_4_while_1_block(this);
      this.ae_FFT_createRandomComplexArray_4_while=new FFT_createRandomComplexArray_4_whileloop(this);
    }
  }
  FFT_createRandomComplexArray_4_while(){
  }
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray ae_parent;
  public FFT_createRandomComplexArray_4_while_1_block ae_FFT_createRandomComplexArray_4_while_1_block;
  public FFT_createRandomComplexArray_4_whileloop ae_FFT_createRandomComplexArray_4_while;
  public FFT_createRandomComplexArray_4_while ae_previous;
}
