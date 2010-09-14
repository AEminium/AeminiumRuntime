package aeminium.runtime.examples.fjtests;

/*
 * Parallel Version of the Cooley-Tukey FFT for series of size N, being N a power of two.
 */

import java.util.Random;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumFFT {

	public static class FFTBody implements Body {
		/* It reuses the result array for both input and result */
		public Complex[] result;
		private Complex[] odd;
		private Complex[] even;
		private int n;
		private int threshold;
		
		public FFTBody(Complex[] input, int thre) {
			this.threshold = thre;
			this.result = input;
			n = input.length;
			if (n != 1 && n % 2 != 0) { throw new RuntimeException("Size of array is not a power of 2."); }
			
			odd = new Complex[n/2];
			even = new Complex[n/2];
		}
		
		@Override
		public void execute(Runtime rt, Task current) {
			if (n == 1) {
				return;
			}
			if (n <= threshold) {
				result = sequentialFFT(result);
				return;
			}
			
			for (int k=0; k < n/2; k++) {
				even[k] = result[2*k];
				odd[k] = result[2*k+1];
			}
			
			FFTBody b1 = new FFTBody(even, threshold);
			Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
			rt.schedule(t1, current, Runtime.NO_DEPS);
			
			FFTBody b2 = new FFTBody(odd, threshold);
			Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
			rt.schedule(t2, current, Runtime.NO_DEPS);
			
			t1.getResult();
			t2.getResult();
			
			for (int k = 0; k < n/2; k++) {
	            double kth = -2 * k * Math.PI / n;
	            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
	            result[k]       = b1.result[k].plus(wk.times(b2.result[k]));
	            result[k + n/2] = b1.result[k].minus(wk.times(b2.result[k]));
			}
		}
	}

	public static FFTBody createFFTBody(final Runtime rt, final Complex[] input, int thre) {
		Complex[] in = new Complex[input.length];
		System.arraycopy(input, 0, in, 0, input.length);
		return new FFTBody(in, thre);
	}

	public static void main(String[] args) {
		Complex[] input = createRandomComplexArray(524288);
		
		
		
		Runtime rt = Factory.getRuntime();
		rt.init();
		FFTBody body = createFFTBody(rt, input, 1024);
		Task t1 = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
		
		show(body.result, "Result");
		show(sequentialFFT(input), "Result Linear");
		
	}
	
	public static Complex[] createRandomComplexArray(int n) {
		Complex[] x = new Complex[n];
        for (int i = 0; i < n; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
        return x;
	}
	
	public static Complex[] createRandomComplexArray(int n, long seed) {
		Random r = new Random(seed);
		Complex[] x = new Complex[n];
        for (int i = 0; i < n; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*r.nextDouble() + 1, 0);
        }
        return x;
	}
	

	public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }
	
	
	/* Linear Version */
    public static Complex[] sequentialFFT(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = sequentialFFT(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = sequentialFFT(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
}
