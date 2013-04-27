package attacksKad;


public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] cdf = (new EclipseKademliaUpper(10,8,1)).getRoutingLength(1000); 
		for (int i = 0; i < cdf.length; i++){
			System.out.println(i + " " +cdf[i]);
		}

	}

}
