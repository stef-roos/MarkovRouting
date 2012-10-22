package eclipse;

import java.math.BigInteger;

public class Calc {
	
	 /**
	   * Computes the binomial coefficient "n over k".
	   * 
	   * @param n
	   * @param k
	   * @return the binomial coefficient
	   */
	  public static double binomDist(int n, final int k, double p) {
		  double r = 1;
		  int min;
		  if (k < n -k){
			  min = k;
			  p = 1-p;
		  } else {
			  min = n-k;
		  }
		// System.out.println("min= " + min);
		  for (int i = 1; i <= min; i++) {
		      r = r*n*p*(1-p)/(double)i;
		    //  System.out.println("r =  " + r + " at iteration " + i);
		     n--;
		    }
		  for (int i = 0 ; i < n -min; i++){
			  r = r*p;
		  }
		  return r;
	  }
	  
	  
	  /**
	   * Computes the binomial coefficient "n over k".
	   * 
	   * @param n
	   * @param k
	   * @return the binomial coefficient
	   */
	  public static long binom(int n, final int k) {
	    final int min = (k < n - k ? k : n - k);
	    long bin = 1;
	    for (int i = 1; i <= min; i++) {
	      bin *= n;
	      // geht immer genau, da n * (n-1) * ... immer durch das
	      // entsprechende i teilbar ist
	      bin /= i;
	      n--;
	    }
	    return bin;
	  }
	  
	  /**
	   * Computes the binomial coefficient "n over k".
	   * 
	   * @param n
	   * @param k
	   * @return the binomial coefficient
	   */
	  public static BigInteger binomBig(int n, final int k) {
	    final int min = (k < n - k ? k : n - k);
	    BigInteger n1 = new BigInteger(n+"");
	    BigInteger bin = BigInteger.ONE;
	    BigInteger index = BigInteger.ONE; 
	    for (int i = 1; i <= min; i++) {
	      bin = bin.multiply(n1);
	      // geht immer genau, da n * (n-1) * ... immer durch das
	      // entsprechende i teilbar ist
	      bin = bin.divide(index);
	      n1 = n1.subtract(BigInteger.ONE);
	      index = index.add(BigInteger.ONE);
	    }
	    return bin;
	  }
	  
	  /**
	   * compute the expectation from a CDF
	   * of a discrete random variables with values in N_0
	   * @param cdf
	   * @return
	   */
	  public static double getEx(double[] cdf){
		  double res = 0;
		  for (int i = 1; i < cdf.length; i++){
			  res = res + (1-cdf[i]);
		  }
		  return res;
	  }

}
