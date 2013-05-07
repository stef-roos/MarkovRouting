package attacksKad;

import util.Calc;


public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] cdf = (new EclipseKademliaUpper(10,3,5)).getRoutingLength(100); 
		double ex = 0;
		for (int i = 0; i < cdf.length; i++){
			ex = ex + 1 - cdf[i];
			System.out.println(i + " " +cdf[i]);
		}
		
//		double attProb = 0;
//		for (int i = 0; i < 10; i++){
//			double p = Math.pow(2, -i-1);
//			for (int j = 0; j < 99; j++){
//				double binom = Calc.binomDist(98, j, p)*p;
//				attProb = attProb + binom*10/(double)Calc.binom(5+j+1, 3);
//			}
//		}
//		System.out.println(attProb);

	}

}
