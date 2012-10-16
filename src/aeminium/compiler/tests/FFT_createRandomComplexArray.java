package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_createRandomComplexArray implements aeminium.runtime.Body {
  FFT_createRandomComplexArray(  aeminium.runtime.CallerBodyWithReturn<Complex[]> ae_parent,  int n,  long seed){
    this.ae_parent=ae_parent;
    this.n=n;
    this.seed=seed;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_createRandomComplexArray_1_varstmt=new FFT_createRandomComplexArray_1_varstmt(this);
    this.ae_FFT_createRandomComplexArray_2_varstmt=new FFT_createRandomComplexArray_2_varstmt(this);
    this.ae_FFT_createRandomComplexArray_3_varstmt=new FFT_createRandomComplexArray_3_varstmt(this);
    this.ae_FFT_createRandomComplexArray_4_while=new FFT_createRandomComplexArray_4_while(this);
    this.ae_FFT_createRandomComplexArray_5_ret=new FFT_createRandomComplexArray_5_ret(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_createRandomComplexArray_1_varstmt.ae_task,this.ae_FFT_createRandomComplexArray_2_varstmt.ae_task,this.ae_FFT_createRandomComplexArray_3_varstmt.ae_task,this.ae_FFT_createRandomComplexArray_4_while.ae_task,this.ae_FFT_createRandomComplexArray_5_ret.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile Complex[] ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Complex[]> ae_parent;
  public int n;
  public long seed;
  public aeminium.runtime.Task ae_task;
  public FFT_createRandomComplexArray_1_varstmt ae_FFT_createRandomComplexArray_1_varstmt;
  public FFT_createRandomComplexArray_2_varstmt ae_FFT_createRandomComplexArray_2_varstmt;
  public FFT_createRandomComplexArray_3_varstmt ae_FFT_createRandomComplexArray_3_varstmt;
  public FFT_createRandomComplexArray_4_while ae_FFT_createRandomComplexArray_4_while;
  public FFT_createRandomComplexArray_5_ret ae_FFT_createRandomComplexArray_5_ret;
}
