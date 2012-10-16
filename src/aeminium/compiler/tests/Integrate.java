package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Integrate {
  static double f(  double x){
    return (x * x + 1.0) * x;
  }
  public static void main(  String[] args){
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
