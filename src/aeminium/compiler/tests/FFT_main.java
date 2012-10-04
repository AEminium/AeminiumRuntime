package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main implements aeminium.runtime.Body {
  FFT_main(  aeminium.runtime.CallerBody ae_parent,  String[] args){
    this.ae_parent=ae_parent;
    this.args=args;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_main_1_varstmt=new FFT_main_1_varstmt(this);
    this.ae_FFT_main_2_varstmt=new FFT_main_2_varstmt(this);
    this.ae_FFT_main_3_varstmt=new FFT_main_3_varstmt(this);
    this.ae_FFT_main_4_while=new FFT_main_4_while(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_main_1_varstmt.ae_task,this.ae_FFT_main_2_varstmt.ae_task,this.ae_FFT_main_3_varstmt.ae_task,this.ae_FFT_main_4_while.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
  }
  public aeminium.runtime.CallerBody ae_parent;
  public String[] args;
  public aeminium.runtime.Task ae_task;
  public FFT_main_1_varstmt ae_FFT_main_1_varstmt;
  public FFT_main_2_varstmt ae_FFT_main_2_varstmt;
  public FFT_main_3_varstmt ae_FFT_main_3_varstmt;
  public FFT_main_4_while ae_FFT_main_4_while;
}
