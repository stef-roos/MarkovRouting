package eclipse;

import java.math.BigDecimal;

public class AttackChord {
	
	public static double failure(int nodes, int bits, double p, boolean file){
		double[] cdf;
		if (file){
			cdf = ExactChordRouting.getRoutingCDFFile(bits, nodes);
		} else {
			cdf = ExactChordRouting.getRoutingCDFNode(bits, nodes);
		}
		
		double fail = 0;
		for (int i = 1; i < cdf.length; i++){
			fail = fail + (cdf[i]-cdf[i-1])*(1 - Math.pow(1-p, i));
		}
		return fail;
	}
	
	public static double randomAttack(int nodes, int bits, int a, boolean file){
		double[] cdf;
		if (file){
			cdf = ExactChordRouting.getRoutingCDFFile(bits, nodes+a);
		} else {
			cdf = ExactChordRouting.getRoutingCDFNode(bits, nodes+a);
		}
		
		double fail = 0;
		double p = 1;
		for (int i = 1; i < cdf.length; i++){
			p = p * (double)(nodes-i)/(nodes+a-i);
			fail = fail + (cdf[i]-cdf[i-1])*(1 - p);
		}
		return fail;
	}
	
	public static BigDecimal targetedAttack(int nodes, int M){
		BigDecimal m = new BigDecimal((Math.pow(2, M)));
		BigDecimal[] r = new BigDecimal[M+1];
		for (int i = 0; i < r.length; i++){
			r[i] = binom(M,i).divide(m);
		}
		BigDecimal n = new BigDecimal((double)nodes);
		BigDecimal p = BigDecimal.ONE;
		BigDecimal fail = BigDecimal.ONE;
	    for (int i = 1; i < r.length; i++){
	    	p = BigDecimal.ONE.subtract(new BigDecimal(i).divide(m));
	    	p = p.pow(nodes);
	    	fail = fail.multiply(p).multiply(r[i]);
	    }
	    return fail;
	}
	 /**
	   * Computes the binomial coefficient "n over k".
	   * 
	   * @param n
	   * @param k
	   * @return the binomial coefficient
	   */
	  public static BigDecimal binom(int n, final int k) {
	    final int min = (k < n - k ? k : n - k);
	    BigDecimal n1 = new BigDecimal(n);
	    BigDecimal bin = BigDecimal.ONE;
	    for (int i = 1; i <= min; i++) {
	      bin = bin.multiply(n1);
	      bin = bin.divide(new BigDecimal(i));
	      n1 = n1.subtract(BigDecimal.ONE);
	    }
	    return bin;
	  }

}
