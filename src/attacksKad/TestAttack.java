package attacksKad;


public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] cdf = (new EclipseKademliaUpper(8,8,5)).getRoutingLength(1000); 
		for (int i = 0; i < cdf.length; i++){
			System.out.println(i + " " +cdf[i]);
		}

	}

}
