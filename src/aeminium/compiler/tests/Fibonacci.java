package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Fibonacci {
  public static long f(  int n){
    if (n < 2)     return n;
    return f(n - 1) + f(n - 2);
  }
  public static void main(  String[] args){
    AeminiumHelper.init();
    new Fibonacci_main(null,args);
    AeminiumHelper.shutdown();
  }
}
