package kadtype;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KadType kad = new KademliaLower(10,8); 
        double[] cdf = kad.getRoutingLength(100);
        for (int i = 0; i < cdf.length; i++){
        	System.out.println(i + " " + cdf[i]);
        }
	}

}
