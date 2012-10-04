package aeminium.compiler.tests;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class FFT_main_4_whileloop_2_block_1_exprstmt_1_invoke extends FFT_main_4_while_2_block_1_exprstmt_1_invoke implements aeminium.runtime.Body {
  FFT_main_4_whileloop_2_block_1_exprstmt_1_invoke(  FFT_main_4_whileloop_2_block_1_exprstmt ae_parent){
    this.ae_parent=ae_parent;
    this.ae_task=AeminiumHelper.createNonBlockingTask(this,AeminiumHelper.NO_HINTS);
    this.ae_FFT_main_4_while_2_block_1_exprstmt_1_invoke_1_arrayidx=new FFT_main_4_whileloop_2_block_1_exprstmt_1_invoke_1_arrayidx(this);
    AeminiumHelper.schedule(this.ae_task,ae_parent == null ? AeminiumHelper.NO_PARENT : ae_parent.ae_task,java.util.Arrays.asList(this.ae_FFT_main_4_while_2_block_1_exprstmt_1_invoke_1_arrayidx.ae_task,this.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_main_2_varstmt.ae_task,this.ae_parent.ae_parent.ae_parent.ae_previous.ae_FFT_main_4_while_1_field.ae_FFT_main_4_while_1_field_1_paren.ae_task,this.ae_parent.ae_parent.ae_parent.ae_previous.ae_parent.ae_FFT_main_2_varstmt.ae_FFT_main_2_varstmt_1_invoke.ae_task,this.ae_parent.ae_parent.ae_parent.ae_FFT_main_4_while_1_field.ae_FFT_main_4_while_1_field_1_paren.ae_task));
  }
  public void execute(  aeminium.runtime.Runtime rt,  aeminium.runtime.Task task) throws Exception {
    System.out.println(this.ae_FFT_main_4_while_2_block_1_exprstmt_1_invoke_1_arrayidx.ae_ret);
  }
}
