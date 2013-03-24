package kadtype;

/**
 * class for lower bounds
 * @author stefanie
 *
 */

public abstract class KadTypeLower extends KadTypeCDFs {
	//probability to be a distinct node, depending on distance and count
	protected double[][] distinctP;

	public KadTypeLower(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	public KadTypeLower(int b, int alpha, int beta, int k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower(int b, int alpha, int beta, int k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	/**
	 * setting distinctP
	 * @param n
	 */
	private void setDistinct(int n){
		distinctP = new double[this.b+1][(this.alpha-1)*this.beta];
		for (int c = 0; c < distinctP[0].length; c++){
			for (int i = 0; i < distinctP.length; i++){
				//iterate over possible number of nodes in region
				double prob = Math.pow(2, i-this.b-1);
				double binom = Math.pow(1-prob, n);
				double p = 0;
				for (int m = 0; m < n; m++){
					p = p + binom*m/(double)(m+c+1);
					binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
				}
				distinctP[i][c] = p;
			}
		}
		
	}
	
	/**
	 * consider distinct possibilities
	 * @param returned
	 * @param t
	 * @param index
	 * @param d1
	 * @param n
	 * @param p
	 * @return
	 */
	protected double makeDistinct(int[][] returned, double[][] t, int index, int d1, int n, double p){
		if (this.distinctP == null){
			this.setDistinct(n);
		}
		//find maximal distinct set
		int[][] max = new int[d1][2];
		for (int i=0; i < d1; i++){
			for (int j=0; j < this.alpha; j++){
				int count = 0;
				for (int m=0; m < this.beta; m++){
					if (returned[j][m] == i){
						count++;
					}
				}
				if (count > max[i][1]){
					max[i][0] = j;
					max[i][1] = count;
				}
			}
		}
		boolean[][] contain = new boolean[this.alpha][this.beta];
		return this.recusivecombine(returned, t, index, 0, 0, n, max, contain, p);
	}
	
	/**
	 * recursively find new state + probability
	 * @param returned
	 * @param t
	 * @param index
	 * @param a
	 * @param c
	 * @param n
	 * @param max
	 * @param contain
	 * @param p
	 * @return
	 */
	protected double recusivecombine(int[][] returned, double[][] t, int index, 
			int a, int c, int n, int[][] max, boolean[][] contain, double p){
		//set if unique
		while (a < this.alpha && 
				(returned[a][c] > max.length-1 
						|| max[returned[a][c]][0] == a)){
			if (returned[a][c] < max.length){
				contain[a][c] = true;
			}else {
				contain[a][c] = false;
			}
			int anew = (a*this.beta+c+1)/this.beta;
			c = (a*this.beta+c+1) % this.beta;
			a = anew;
			//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
		}
		if (a < this.alpha){
			//compute p'
			double pdash;
			int count = max[returned[a][c]][1];
			for (int i = 0; i < a; i++){
				if (max[returned[a][c]][0] != i){
					for (int j = 0; j < this.beta; j++){
						if (returned[a][c] == returned[i][j] && contain[i][j]){
							count++;
						}
					}
				}
			}
			for (int j = 0; j < c; j++){
				if (returned[a][c] == returned[a][j] && contain[a][j]){
					count--;
				}
			}
			if (count == 0){
				pdash = 1;
			} else {
				pdash = this.distinctP[returned[a][c]][count-1];
			}
			int adash = (a*this.beta+c+1)/this.beta;
			int cdash = (a*this.beta+c+1) % this.beta;
			//case: distinct
			contain[a][c] = true;
			double rep = this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*pdash);
			//case: not distinct
			contain[a][c] = false;
			rep = rep +this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*(1-pdash));
			return rep;
		}else {
			//Evaluation
			int[][] combi = new int[this.alpha][this.beta];
			for (int i = 0; i < this.alpha; i++){
				for (int j = 0; j < this.beta; j++){
					if (contain[i][j]){
						combi[i][j] = returned[i][j]; 
					} else {
						combi[i][j] = b;
					}
				}
			}
			int[] next = this.topAlpha(combi);
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
			return p;
		}
		
	}

}
