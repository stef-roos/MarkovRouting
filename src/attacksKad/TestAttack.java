package attacksKad;

import kadtype.KADLower2;

public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] cdf = (new FailureKademliaLower(10,8,0.5)).getRoutingLength(1000); 
		for (int i = 0; i < cdf.length; i++){
			System.out.println(i + " " +cdf[i]);
		}

	}

}
