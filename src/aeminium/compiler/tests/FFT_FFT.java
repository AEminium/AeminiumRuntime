package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_FFT implements aeminium.runtime.Body {
  FFT_FFT(  aeminium.runtime.CallerBodyWithReturn<Complex[]> ae_parent,  Complex[] x){
    this.ae_parent=ae_parent;
    this.x=x;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_FFT_1_varstmt=new FFT_FFT_1_varstmt(this);
    this.ae_FFT_FFT_2_if=new FFT_FFT_2_if(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_FFT_1_varstmt.ae_task,this.ae_FFT_FFT_2_if.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public volatile Complex[] ae_ret;
  public aeminium.runtime.CallerBodyWithReturn<Complex[]> ae_parent;
  public Complex[] x;
  public aeminium.runtime.Task ae_task;
  public FFT_FFT_1_varstmt ae_FFT_FFT_1_varstmt;
  public FFT_FFT_2_if ae_FFT_FFT_2_if;
}
