package kadtype;

import kadtype.KadType.LType;

public abstract class KadTypeLower2 extends KadTypeCDFs {
	//probability to be a distinct node, depending on distance and count
	protected double[][] distinctP;
	protected double[][] distinctPMax;

	public KadTypeLower2(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower2(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	public KadTypeLower2(int b, int alpha, int beta, int k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower2(int b, int alpha, int beta, int k, int l) {
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
				for (int m = 0; m < n-c; m++){
					p = p + binom*m/(double)(m+c+1);
					binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
				}
				distinctP[i][c] = p;
			}
		}
		this.distinctPMax = new double[this.b+1][this.alpha-1];
		for (int c = 0; c < distinctPMax[0].length; c++){
			int count = (c+1)*this.beta;
			for (int i = 0; i < distinctPMax.length; i++){
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
		boolean zero = false;
		
		if (this.distinctP == null){
			this.setDistinct(n);
		}
		//find maximal distinct set
		int[][] max = new int[Math.max(d1,1)][2];
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
		//System.out.println("a= " + a + " c= " + c + "re" + returned[a][c]);
               while (a < this.alpha && ((returned[a][c] < max.length &&
						max[returned[a][c]][0] == a) || returned[a][c] == this.b)){
					contain[a][c] = true;
					int anew = (a*this.beta+c+1)/this.beta;
					c = (a*this.beta+c+1) % this.beta;
					a = anew;
					//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
				}
               //System.out.println("After while a= " + a + " c= " + c + "re" + returned[a][c]);
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
								combi[i][j] = b;
							}
						}
					}
					int[] next = this.topAlpha(combi);
					//start alternative
					if (next[next.length-1] == b){
						return this.getNextBest(next,p,old,index,t);
					} else {
					int indexN = this.getIndex(next);
					t[indexN][index] = t[indexN][index] + p;
					return p;
					}
				}
	}
	
	private double getNextBest(int[] next, double p, int[] old, int index, double[][] t){
		if (this.beta == 1){
			return this.getNextBestStart(next, p, old, index, t);
		}
		int count = 0;
		for (int i = next.length-1; i > 0; i--){
			if (next[i] == b){
				count++;
			} else {
				break;
			}
		}
		if (count == 1){
			return this.getNextBest1(next, p, old, index, t);
		} else {
			if (count == 2){
				return this.getNextBest2(next, p, old, index, t);
			} else {
				throw new IllegalArgumentException(count + " non-distinct contacts should not happen");
			}
		}
	}
	
	private double getNextBest1(int[] next, double p, int[] old, int index, double[][] t){
		double p2 = 0;
		double sum = 0;
		int digit = 1;
		int dalpha = old[old.length-1];
		if (dalpha == b || dalpha==0){
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
			return p;
		}
		if (this.ltype == LType.SIMPLE){
			digit = (int)this.l[0][0];
			double power2 = Math.pow(2,dalpha+digit-1-b);
			double power2a = power2;
			for (int j=dalpha+digit; j <= this.b; j++){
				sum = sum + (this.cdfs[j][dalpha][this.beta-2] -this.cdfs[j][dalpha-1][this.beta-2])*power2a;
				power2a = power2a*2;
			}
			//iterate over potential predecessor
			for (int j=dalpha+digit; j <= this.b; j++){
				//iterate over potential nextbest
				double pj = (this.cdfs[j][dalpha][this.beta-2] -this.cdfs[j][dalpha-1][this.beta-2])*power2/sum;
				power2 = power2*2;
				for (int dalpha1 = dalpha; dalpha1 <= j-digit; dalpha1++){
					double pc = (this.cdfs[j][dalpha1][this.beta-1] -this.cdfs[j][dalpha1-1][this.beta-1])
							/(1 - this.cdfs[j][dalpha-1][this.beta-1]);
					next[next.length-1] = dalpha1;
					int indexN = this.getIndex(next);
					t[indexN][index] = t[indexN][index] + p*pj*pc;
					p2 = p2 + pj*pc;
				}
			}
	       }
		if (this.ltype == LType.ALL){
			for (int a = 1; a <= this.b; a++){
				double power2 = Math.pow(2,dalpha+a-1-b);
				double power2a = power2;
				for (int j=dalpha+a; j <= this.b; j++){
					if (l[j][a] > 0){
					sum = sum + (this.cdfs[j][dalpha][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*power2a;
					}
					power2a = power2a*2;
				}
				//iterate over potential predecessor
				for (int j=dalpha+a; j <= this.b; j++){
					//iterate over potential nextbest
					if (l[j][a] > 0){
					double pj = (this.cdfs[j][dalpha][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*power2/sum;
					
					for (int dalpha1 = dalpha; dalpha1 <= j-digit; dalpha1++){
						double pc = (this.cdfs[j][dalpha1][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha1-1][(this.beta-2)*j+a-1])
								/(1 - this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*l[j][a];
						next[next.length-1] = dalpha1;
						int indexN = this.getIndex(next);
						t[indexN][index] = t[indexN][index] + p*pj*pc;
						p2 = p2 + pj*pc;
					}
					power2 = power2*2;
					}
				}
			}
		}
		
		return p2*p;
	}
	
	private double getNextBest2(int[] next, double p, int[] old, int index, double[][] t){
		double[] probs = new double[b+1];
		double p2 = 0;
		double sum = 0;
		int digit = 1;
		int dalpha = old[old.length-1];
		if (dalpha == b || dalpha==0){
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
			return p;
		}
		if (this.ltype == LType.SIMPLE){
			digit = (int)this.l[0][0];
			double power2 = Math.pow(2,dalpha+digit-1-b);
			double power2a = power2;
			for (int j=dalpha+digit; j <= this.b; j++){
				sum = sum + (this.cdfs[j][dalpha][this.beta-2] -this.cdfs[j][dalpha-1][this.beta-2])*power2a;
				power2a = power2a*2;
			}
			//iterate over potential predecessor
			for (int j=dalpha+digit; j <= this.b; j++){
				//iterate over potential nextbest
				double pj = (this.cdfs[j][dalpha][this.beta-2] -this.cdfs[j][dalpha-1][this.beta-2])*power2/sum;
				power2 = power2*2;
				for (int dalpha1 = dalpha; dalpha1 <= j-digit; dalpha1++){
					double pc = (this.cdfs[j][dalpha1][this.beta-1] -this.cdfs[j][dalpha1-1][this.beta-1])
							/(1 - this.cdfs[j][dalpha-1][this.beta-1]);
					probs[dalpha1] = probs[dalpha1] + pc*pj;
				}
			}
	       }
		if (this.ltype == LType.ALL){
			for (int a = 1; a <= this.b; a++){
				double power2 = Math.pow(2,dalpha+a-1-b);
				double power2a = power2;
				for (int j=dalpha+a; j <= this.b; j++){
					if (l[j][a] > 0){
					sum = sum + (this.cdfs[j][dalpha][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*power2a;
					}
					power2a = power2a*2;
				}
				//iterate over potential predecessor
				for (int j=dalpha+a; j <= this.b; j++){
					//iterate over potential nextbest
					if (l[j][a] > 0){
					double pj = (this.cdfs[j][dalpha][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*power2/sum;
					for (int dalpha1 = dalpha; dalpha1 <= j-digit; dalpha1++){
						double pc = (this.cdfs[j][dalpha1][(this.beta-2)*j+a-1] -this.cdfs[j][dalpha1-1][(this.beta-2)*j+a-1])
								/(1 - this.cdfs[j][dalpha-1][(this.beta-2)*j+a-1])*l[j][a];
						probs[dalpha1] = probs[dalpha1] + pc*pj;
					}
					}
					power2 = power2*2;
					
				}
			}
		}	
		
		for (int i= dalpha; i <= this.b; i++){
			for (int j = dalpha; j <= this.b; j++){
				next[next.length-1] = Math.max(i, j);
				next[next.length-2] = Math.min(i, j);
				int indexN = this.getIndex(next);
				t[indexN][index] = t[indexN][index] + p*probs[i]*probs[j];
			}
		}
		return p;
	}
	
	private double getNextBestStart(int[] next, double p, int[] old, int index, double[][] t){
		if (this.ltype == LType.SIMPLE){
			for (int i = 0; i < next.length; i++){
				if (next[i] == b){
					next[i] = b - (int)l[0][0];
				}
			}
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
		}
		if (this.ltype == LType.ALL){
			int max =1;
			for (int j = 2; j < b+1; j++){
				if (l[0][j] > 0){
					max = j;
				}
			}
			for (int i = 0; i < next.length; i++){
				if (next[i] == b){
					next[i] = b - max;
				}
			}
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
		}
		return p;
	}

}
