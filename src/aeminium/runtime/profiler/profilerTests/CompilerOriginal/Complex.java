package aeminium.runtime.profiler.profilerTests.CompilerOriginal;

public class Complex
{
	private final double re;   // the real part
	private final double im;   // the imaginary part

	// create a new object with the given real and imaginary parts
	public Complex(double real, double imag)
	{
	    this.re = real;
	    this.im = imag;
	}

	// return a string representation of the invoking Complex object
	public String toString()
	{
	    if (this.im == 0)
			return this.re + "";
	    else if (this.re == 0)
			return this.im + "i";
	    else if (this.im <  0)
			return this.re + " - " + (-this.im) + "i";
	    else
			return this.re + " + " + this.im + "i";
	}

	// return abs/modulus/magnitude and angle/phase/argument
	public double abs()   { return Math.hypot(this.re, this.im); }  // Math.sqrt(re*re + im*im)
	public double phase() { return Math.atan2(this.im, this.re); }  // between -pi and pi

	// return a new Complex object whose value is (this + b)
	public Complex plus(Complex b)
	{
	    double real = this.re + (b).re;
	    double imag = this.im + (b).im;
	    return new Complex(real, imag);
	}

	// return a new Complex object whose value is (this - b)
	public Complex minus(Complex b)
	{
	    double real = this.re - (b).re;
	    double imag = this.im - (b).im;
	    return new Complex(real, imag);
	}

	// return a new Complex object whose value is (this * b)
	public Complex times(Complex b)
	{
	    double real = this.re * (b).re - this.im * (b).im;
	    double imag = this.re * (b).im + this.im * (b).re;
	    return new Complex(real, imag);
	}

	// return a new Complex object whose value is the conjugate of this
	public Complex conjugate() {  return new Complex(this.re, -this.im); }

	// return a new Complex object whose value is the reciprocal of this
	public Complex reciprocal()
	{
	    double scale = this.re*this.re + this.im*this.im;
	    return new Complex(this.re / scale, -this.im / scale);
	}

	// return the real or imaginary part
	public double re() { return this.re; }
	public double im() { return this.im; }

	// return a / b
	public Complex divides(Complex b)
	{
	    return this.times((b).reciprocal());
	}

	// return a new Complex object whose value is the complex exponential of this
	public Complex exp()
	{
	    return new Complex(Math.exp(this.re) * Math.cos(this.im), Math.exp(this.re) * Math.sin(this.im));
	}

	// return a new Complex object whose value is the complex sine of this
	public Complex sin()
	{
	    return new Complex(Math.sin(this.re) * Math.cosh(this.im), Math.cos(this.re) * Math.sinh(this.im));
	}

	// return a new Complex object whose value is the complex cosine of this
	public Complex cos() {
	    return new Complex(Math.cos(this.re) * Math.cosh(this.im), -Math.sin(this.re) * Math.sinh(this.im));
	}

	// return a new Complex object whose value is the complex tangent of this
	public Complex tan()
	{
	    return this.sin().divides(this.cos());
	}
}
