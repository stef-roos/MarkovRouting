package attacksKad;


public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] cdf = (new FailureKademliaUpper(15,8,0.0)).getRoutingLength(10000); 
		double ex = 0;
		for (int i = 0; i < cdf.length; i++){
			ex = ex + 1 - cdf[i];
			System.out.println(i + " " +cdf[i]);
		}
		System.out.println("Expected path length " + ex);

	}

}
