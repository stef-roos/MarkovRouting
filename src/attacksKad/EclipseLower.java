package attacksKad;

import kadtype.KadType.LType;


public abstract class EclipseLower extends Eclipse {

	//probability to be a distinct node, depending on distance and count
		protected double[][] distinctP;
		protected double[][] distinctPMax;

		public EclipseLower(int b, int alpha, int beta, int[] k, double[][] l,
				LType ltype, int att) {
			super(b, alpha, beta, k, l, ltype,att);
		}

		public EclipseLower(int b, int alpha, int beta, int[] k, int l, int att) {
			super(b, alpha, beta, k, l,att);
		}
		
		public EclipseLower(int b, int alpha, int beta, int k, double[][] l,
				LType ltype, int att) {
			super(b, alpha, beta, k, l, ltype, att);
		}

		public EclipseLower(int b, int alpha, int beta, int k, int l, int att) {
			super(b, alpha, beta, k, l,att);
		}
		
		/**
		 * setting distinctP
		 * @param n
		 */
		private void setDistinct(int n){
			distinctP = new double[this.b+2][(this.alpha-1)*this.beta];
			for (int c = 0; c < distinctP[0].length; c++){
				for (int i = 1; i < distinctP.length; i++){
					//iterate over possible number of nodes in region
					double prob = Math.pow(2, i-this.b-1);
					double binom = Math.pow(1-prob, n);
					double p = 0;
					for (int m = 0; m < n-c; m++){
						p = p + binom*m/(double)(m+c+1);
						binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
					}
					distinctP[i][c] = p;
				}
				distinctP[0][c] = 1-c/(double)this.attackers;;
			}
			this.distinctPMax = new double[this.b+1][this.alpha-1];
			for (int c = 0; c < distinctPMax[0].length; c++){
				int count = (c+1)*this.beta;
				for (int i = 1; i < distinctPMax.length; i++){
					//iterate over possible number of nodes in region
					double prob = Math.pow(2, i-this.b-1);
					double binom = Math.pow(1-prob, n);
					double p = 0;
					for (int m = 0; m < n-count; m++){
						p = p + binom*m/(double)(m+count+1);
						binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
					}
					distinctPMax[i][c] = p;
				}
				distinctPMax[0][c] = 1-c/(double)this.attackers;
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
		protected double makeDistinct(int[][] returned, double[][] t, int index, int d1, int n, double p, int[] old){
			if (this.distinctP == null){
				this.setDistinct(n);
			}
			//find maximal distinct set
			int[][] max = new int[Math.max(d1+1,1)][2];
			for (int i=0; i < max.length; i++){
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
			return this.recusivecombine(returned, t, index, 0, 0, max, contain, p,old);
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
				int a, int c, int[][] max, boolean[][] contain, double p, int[] old){
			//System.out.println("a= " + a + " c= " + c);// + "re" + returned[a][c] + " max" + max.length);
	               while (a < this.alpha && ((returned[a][c] < max.length &&
							max[returned[a][c]][0] == a) || returned[a][c] == this.b+1 || returned[a][c] == 0)){
						contain[a][c] = true;
						int anew = (a*this.beta+c+1)/this.beta;
						c = (a*this.beta+c+1) % this.beta;
						a = anew;
						//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
					}
	               //System.out.println("After while a= " + a + " c= " + c );//+ "re" + returned[a][c]);
					if (a < this.alpha){
						//compute p'
						double pdash;
						
						if (returned[a][c] < max.length){
						int count = max[returned[a][c]][1];
						//System.out.println("Count start: " + count);
						for (int i = 0; i < a; i++){
							if (max[returned[a][c]][0] != i){
								for (int j = 0; j < this.beta; j++){
									if (returned[a][c] == returned[i][j] && contain[i][j]){
										count++;
									}
								}
							}
						}
						//System.out.println("Count <a: " + count);
						for (int j = 0; j < c; j++){
							if (returned[a][c] == returned[a][j] && contain[a][j]){
								count--;
							}
						}
						//System.out.println("Count <b: " + count);
						if (count == 0){
							pdash = 1;
						} else {
							pdash = this.distinctP[returned[a][c]][count-1];
						}
						} else {
							if (a == 0){
								System.out.println("a=0!! re: " + returned[a][c] + " max: " + max.length + " old0= " + old[0]);
							}
							pdash = this.distinctPMax[returned[a][c]][a-1];
						}
						int adash = (a*this.beta+c+1)/this.beta;
						int cdash = (a*this.beta+c+1) % this.beta;
						//System.out.println("Adash a= " + adash + " c= " + cdash + "re" + returned[a][c]);
						//case: distinct
						contain[a][c] = true;
						double rep = this.recusivecombine(returned, t, index, adash, cdash, max, contain, p*pdash, old);
						//case: not distinct
						contain[a][c] = false;
						rep = rep +this.recusivecombine(returned, t, index, adash, cdash, max, contain, p*(1-pdash), old);
						return rep;
					}else {
						//Evaluation
						int[][] combi = new int[this.alpha][this.beta];
						for (int i = 0; i < this.alpha; i++){
							for (int j = 0; j < this.beta; j++){
								if (contain[i][j]){
									combi[i][j] = returned[i][j]; 
								} else {
									combi[i][j] = b+1;
								}
							}
						}
						int[] next = this.topAlpha(combi);
						//start alternative
//						if (next[next.length-1] == b){
//							return this.getNextBest(next,p,old,index,t);
//						} else {
						int indexN = this.getIndex(next);
						t[indexN][index] = t[indexN][index] + p;
						return p;
						//}
					}
		}
		
		

}
