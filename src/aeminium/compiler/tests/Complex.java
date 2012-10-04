package aeminium.compiler.tests;
import aeminium.runtime.AeminiumHelper;
import java.util.ArrayList;
class Complex {
  public double re;
  public double im;
  public Complex(  double real,  double imag){
    this.re=real;
    this.im=imag;
  }
  public String toString(){
    if (this.im == 0)     return this.re + "";
 else     if (this.re == 0)     return this.im + "i";
 else     if (this.im < 0)     return this.re + " - " + (-this.im)+ "i";
 else     return this.re + " + " + this.im+ "i";
  }
  public double abs(){
    return Math.hypot(this.re,this.im);
  }
  public double phase(){
    return Math.atan2(this.im,this.re);
  }
  public Complex plus(  Complex b){
    double real=this.re + (b).re;
    double imag=this.im + (b).im;
    return new Complex(real,imag);
  }
  public Complex minus(  Complex b){
    double real=this.re - (b).re;
    double imag=this.im - (b).im;
    return new Complex(real,imag);
  }
  public Complex times(  Complex b){
    double real=this.re * (b).re - this.im * (b).im;
    double imag=this.re * (b).im + this.im * (b).re;
    return new Complex(real,imag);
  }
  public Complex conjugate(){
    return new Complex(this.re,-this.im);
  }
  public Complex reciprocal(){
    double scale=this.re * this.re + this.im * this.im;
    return new Complex(this.re / scale,-this.im / scale);
  }
  public double re(){
    return this.re;
  }
  public double im(){
    return this.im;
  }
  public Complex divides(  Complex b){
    return this.times((b).reciprocal());
  }
  public Complex exp(){
    return new Complex(Math.exp(this.re) * Math.cos(this.im),Math.exp(this.re) * Math.sin(this.im));
  }
  public Complex sin(){
    return new Complex(Math.sin(this.re) * Math.cosh(this.im),Math.cos(this.re) * Math.sinh(this.im));
  }
  public Complex cos(){
    return new Complex(Math.cos(this.re) * Math.cosh(this.im),-Math.sin(this.re) * Math.sinh(this.im));
  }
  public Complex tan(){
    return this.sin().divides(this.cos());
  }
}
