package eclipse.old;


public class AttackPossibility {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int M = 128;
		int n = 10000;
		int l = 200;
		double[] p = getProb(l,n,M);
        for (int j = 0; j < p.length; j++){
         	System.out.println(p[j]);
        }
	}
	
	/**
	 * 
	 * @param l: length of result 
	 * @param n: number of nodes
	 * @param M: number of bits
	 * @return
	 * position i = P(at least pos empty slots around target)
	 */
	private static double[] getProb(int l, int n, int M){
		double[] p = new double[l];
		p[0] = 1;
		
		double pos = Math.pow(2, M);
		System.out.println((double)n/(pos));
		for (int i = 1; i < p.length; i++){
			p[i] = p[i-1]-(double)n/(pos-i)*p[i-1]; 
		}
		return p;
	}

}
