package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jprofiler.api.agent.Controller;
class Integrate {
  static double f(  double x){
    return (x * x + 1.0) * x;
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
    new Integrate_main(null,args);
    AeminiumHelper.shutdown();
  }
  public static double compute(  double l,  double r){
    return computeRec(l,r,f(l),f(r),0);
  }
  static final double computeRec(  double l,  double r,  double fl,  double fr,  double a){
    double h=(r - l) * 0.5;
    double c=l + h;
    double fc=f(c);
    double hh=h * 0.5;
    double al=(fl + fc) * hh;
    double ar=(fr + fc) * hh;
    double alr=al + ar;
    if (Math.abs(alr - a) <= 1.0e-14)     return alr;
 else     return computeRec(c,r,fc,fr,ar) + computeRec(l,c,fl,fc,al);
  }
}
