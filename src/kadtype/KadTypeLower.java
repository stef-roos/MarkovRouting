package kadtype;

import util.Binom;
import util.Calc;

/**
 * class for lower bounds
 * @author stefanie
 *
 */

public abstract class KadTypeLower extends KadTypeCDFs {
	//probability to be a distinct node, depending on distance and count
	protected double[][] distinctP;
	protected double[][] distinctPMax;

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
	protected void setDistinct(int n){
		distinctP = new double[this.b+1][(this.alpha-1)*this.beta];
		this.distinctPMax = new double[this.b+1][this.alpha-1];
			for (int i = 0; i < distinctP.length; i++){
				//iterate over possible number of nodes in region
				double prob = Math.pow(2, i-this.b-1);
				double p = 0;
//				for (int m = 0; m < n-c; m++){
//					p = p + binom*m/(double)(m+c+1);
//					binom = Calc.binomDist(n, m+1, prob);
//					//binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
//				}
//				for (int m = 0; m < n; m++){
//					for (int c = 0; c < distinctP[0].length; c++){
//						if (m >= n-c) continue;
//					p =  binom*m/(double)(m+c+1);
//					distinctP[i][c] = distinctP[i][c] +p;
//					}
//					for (int c = 0; c < distinctPMax[0].length; c++){
//						int count = (c+1)*this.beta;
//						if (m >= n- count) continue;
//						p = binom*m/(double)(m+count+1);
//						distinctPMax[i][c] = distinctPMax[i][c]+ p;
//					}
//					binom = bi.getNext();
//					//binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
//				}
				int exp = (int) ((n-2)*prob);
				Binom bi = new Binom(n,prob,exp);
				double binom;
				for (int m = exp; m < n-1; m++){
					binom = bi.getNext();
					for (int c = 0; c < distinctP[0].length; c++){
						if (m >= n-c) continue;
					p =  binom*m/(double)(m+c+1);
					distinctP[i][c] = distinctP[i][c] +p;
					}
					for (int c = 0; c < distinctPMax[0].length; c++){
						int count = (c+1)*this.beta;
						if (m >= n- count) continue;
						p = binom*m/(double)(m+count+1);
						distinctPMax[i][c] = distinctPMax[i][c]+ p;
					} 
				}
				bi.recompute(exp);
				for (int m = exp; m > -1; m--){
					binom = bi.getBefore();
					for (int c = 0; c < distinctP[0].length; c++){
						if (m >= n-c) continue;
					p =  binom*m/(double)(m+c+1);
					distinctP[i][c] = distinctP[i][c] +p;
					}
					for (int c = 0; c < distinctPMax[0].length; c++){
						int count = (c+1)*this.beta;
						if (m >= n- count) continue;
						p = binom*m/(double)(m+count+1);
						distinctPMax[i][c] = distinctPMax[i][c]+ p;
					} 
				}
			}
		
		
//		for (int c = 0; c < distinctPMax[0].length; c++){
//			int count = (c+1)*this.beta;
//			for (int i = 0; i < distinctPMax.length; i++){
//				//iterate over possible number of nodes in region
//				double prob = Math.pow(2, i-this.b-1);
//				double binom = Math.pow(1-prob, n);
//				double p = 0;
//				for (int m = 0; m < n-count; m++){
//					p = p + binom*m/(double)(m+count+1);
//					binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
//				}
//				distinctPMax[i][c] = p;
//			}
//		}
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
		return this.recusivecombine(returned, t, index, 0, 0, max, contain, p);
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
			int a, int c, int[][] max, boolean[][] contain, double p){
//		//set if unique
//		while (a < this.alpha && 
//				(returned[a][c] > max.length-1 
//						|| max[returned[a][c]][0] == a)){
//			if (returned[a][c] < max.length){
//				contain[a][c] = true;
//			}else {
//				contain[a][c] = false;
//			}
//			int anew = (a*this.beta+c+1)/this.beta;
//			c = (a*this.beta+c+1) % this.beta;
//			a = anew;
//			//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
//		}
//		if (a < this.alpha){
//			//compute p'
//			double pdash;
//			int count = max[returned[a][c]][1];
//			for (int i = 0; i < a; i++){
//				if (max[returned[a][c]][0] != i){
//					for (int j = 0; j < this.beta; j++){
//						if (returned[a][c] == returned[i][j] && contain[i][j]){
//							count++;
//						}
//					}
//				}
//			}
//			for (int j = 0; j < c; j++){
//				if (returned[a][c] == returned[a][j] && contain[a][j]){
//					count--;
//				}
//			}
//			if (count == 0){
//				pdash = 1;
//			} else {
//				pdash = this.distinctP[returned[a][c]][count-1];
//			}
//			int adash = (a*this.beta+c+1)/this.beta;
//			int cdash = (a*this.beta+c+1) % this.beta;
//			//case: distinct
//			contain[a][c] = true;
//			double rep = this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*pdash);
//			//case: not distinct
//			contain[a][c] = false;
//			rep = rep +this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*(1-pdash));
//			return rep;
//		}else {
//			//Evaluation
//			int[][] combi = new int[this.alpha][this.beta];
//			for (int i = 0; i < this.alpha; i++){
//				for (int j = 0; j < this.beta; j++){
//					if (contain[i][j]){
//						combi[i][j] = returned[i][j]; 
//					} else {
//						combi[i][j] = b;
//					}
//				}
//			}
//			int[] next = this.topAlpha(combi);
//			int indexN = this.getIndex(next);
//			t[indexN][index] = t[indexN][index] + p;
//			return p;
//		}
		
		//set if unique
		while (a < this.alpha && ((returned[a][c] < max.length &&
				max[returned[a][c]][0] == a) || returned[a][c] == this.b+1 || returned[a][c] == 0)){
					contain[a][c] = true;
					int anew = (a*this.beta+c+1)/this.beta;
					c = (a*this.beta+c+1) % this.beta;
					a = anew;
					//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
				}
				if (a < this.alpha){
					//compute p'
					double pdash;
					
					if (returned[a][c] < max.length){
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
					} else {
						pdash = this.distinctPMax[returned[a][c]][Math.max(a-1,0)];
					}
//					if (pdash < 0 || pdash > 1){
//						System.out.println("recursive " + pdash);
//					}
					int adash = (a*this.beta+c+1)/this.beta;
					int cdash = (a*this.beta+c+1) % this.beta;
					//case: distinct
					contain[a][c] = true;
					double rep = this.recusivecombine(returned, t, index, adash, cdash, max, contain, p*pdash);
					//case: not distinct
					contain[a][c] = false;
					rep = rep +this.recusivecombine(returned, t, index, adash, cdash, max, contain, p*(1-pdash));
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
