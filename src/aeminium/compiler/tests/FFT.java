package aeminium.compiler.tests;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;

import com.jprofiler.api.agent.Controller;
class FFT {
  public static Complex[] FFT(  Complex[] x){
    int N=(x).length;
    if (N == 1)     return new Complex[]{x[0]};
 else {
      Complex[] even=new Complex[N / 2];
      Complex[] odd=new Complex[N / 2];
      int k=0;
      while (k < N / 2) {
        even[k]=x[2 * k];
        odd[k]=x[2 * k + 1];
        ++k;
      }
      Complex[] q=FFT(even);
      Complex[] r=FFT(odd);
      Complex[] y=new Complex[N];
      k=0;
      while (k < N / 2) {
        double kth=-2 * k * Math.PI / N;
        Complex wk=new Complex(Math.cos(kth),Math.sin(kth));
        y[k]=q[k].plus(wk.times(r[k]));
        y[k + N / 2]=q[k].minus(wk.times(r[k]));
        ++k;
      }
      return y;
    }
  }
  public static Complex[] createRandomComplexArray(  int n,  long seed){
    Random r=new Random(seed);
    Complex[] x=new Complex[n];
    int i=0;
    while (i < n) {
      x[i]=new Complex(2 * r.nextDouble() - 1,0);
      ++i;
    }
    return x;
  }
  public static void main(  String[] args){
	  
	  /* Activation of profiling options according to the parameters given. */
		Controller.startVMTelemetryRecording();
      Controller.startThreadProfiling();
      Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
      
      try 
      {
      	File file = new File(args[0]);
			file.createNewFile();
			Controller.saveSnapshotOnExit(file);
			
		} catch (IOException e)
		{
			System.out.println("File error: " + e.getMessage());
			System.exit(-1);
		}
	  
    AeminiumHelper.init();
    new FFT_main(null,args);
    AeminiumHelper.shutdown();
  }
}
