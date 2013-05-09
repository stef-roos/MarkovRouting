package attacksKad;

import kadtype.KadType.LType;

public class FailureAlpha3Beta2Lower extends Failure{
	private int ttl;
	
	public FailureAlpha3Beta2Lower(int b, int[] k, double[][] l,
			LType ltype, double fprob, int ttl) {
		super(b, 3, 2, k, l, ltype,fprob);
		this.ttl = ttl;
		
	}
	
	public FailureAlpha3Beta2Lower(int b, int[] k, int l, double fprob, int ttl) {
		super(b, 3, 2, k, l,fprob);
		this.ttl = ttl;
	}
	
	public FailureAlpha3Beta2Lower(int b, int k, double[][] l,
			LType ltype, double fprob, int ttl) {
		super(b, 3, 2, k, l, ltype,fprob);
		this.ttl = ttl;
	}
	
	public FailureAlpha3Beta2Lower(int b, int k, int l, double fprob, int ttl) {
		super(b, 3, 2, k, l,fprob);
		this.ttl = ttl;
	}

	@Override
	protected void processCDFsT1(double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i < cdfs[mindist].length; i++){
			for (int j = i; j < cdfs[mindist].length; j++){
				for (int k = j; k < cdfs[mindist].length; k++){
					
					int[] re = new int[]{i,j,k};
					int indexnew = this.getIndex(re);
					if (this.ltype == LType.SIMPLE){
					   t[indexnew][indexOld] =  t[indexnew][indexOld] 
							+(1-this.success[mindist])*this.getProb(re, mindist,0);
					}   
					if (this.ltype == LType.ALL){
						for (int a = 1; a < mindist+1; a++){
							if (this.l[mindist][a] > 0){
								t[indexnew][indexOld] =  t[indexnew][indexOld] 
										+(1-this.success[mindist])*this.getProb(re, mindist,a-1)*this.l[mindist][a];
							}
						}
					}
				}
			}
		}
		
	}

	@Override
	protected void processCDFsT2(int n, double[][] t2, int[] old, int oldindex,
			double nsucc) {
		int[][] returned = new int[3][2];
		if (this.ltype == LType.SIMPLE){
		for (int i1 = 0; i1 < this.cdfs[old[0]].length+1; i1++){
			returned[0][0] = i1 < this.cdfs[old[0]].length?i1:this.b;
			for (int i2 = i1; i2 <= Math.max(this.cdfs[old[0]].length-1,i1); i2++){
				returned[0][1] = i1 < this.cdfs[old[0]].length?i2:this.b;
				double p1 = i1 < this.cdfs[old[0]].length?this.getProb(returned[0], old[0],0)*(1-this.fprob):this.fprob;
				for (int j1 = 0; j1 < this.cdfs[old[1]].length+1; j1++){
					returned[1][0] = j1<this.cdfs[old[1]].length?j1:this.b;
					for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-1,j1); j2++){
						returned[1][1] = j1<this.cdfs[old[1]].length?j2:this.b;
						double p2 = j1<this.cdfs[old[1]].length?this.getProb(returned[1], old[1],0)*(1-this.fprob):this.fprob;
						for (int k1 = 0; k1 < this.cdfs[old[2]].length+1; k1++){
							returned[2][0] = k1<this.cdfs[old[2]].length?k1:this.b;
							for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-1,k1); k2++){
								returned[2][1] = k1<this.cdfs[old[2]].length?k2:this.b;
								double p3 = k1<this.cdfs[old[2]].length?this.getProb(returned[2], old[2],0)*(1-this.fprob):this.fprob;
								this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3);
							}
						}
					}
				}
			}
			
		}
		}
		if (this.ltype == LType.ALL){
			for (int a1 = 1; a1 <= old[0]; a1++){
				if (l[old[0]][a1] == 0){
					continue;
				}
				for (int i1 = 0; i1 < this.cdfs[old[0]].length+1; i1++){
					returned[0][0] = i1 < this.cdfs[old[0]].length?i1:this.b;
					for (int i2 = i1; i2 <= Math.max(this.cdfs[old[0]].length-1,i1); i2++){
						returned[0][1] = i1 < this.cdfs[old[0]].length?i2:this.b;
						double p1 = i1 < this.cdfs[old[0]].length?this.getProb(returned[0], old[0],a1-1)*(1-this.fprob):this.fprob;
								p1 = p1*l[old[0]][a1];
							   for (int a2= 1; a2 <= old[1]; a2++){
								   if (l[old[1]][a2] == 0){
										continue;
									}
								   for (int j1 = 0; j1 < this.cdfs[old[1]].length+1; j1++){
										returned[1][0] = j1<this.cdfs[old[1]].length?j1:this.b;
										for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-1,j1); j2++){
											returned[1][1] = j1<this.cdfs[old[1]].length?j2:this.b;
											double p2 = j1<this.cdfs[old[1]].length?this.getProb(returned[1], old[1],a2-1)*(1-this.fprob):this.fprob;
											p2 = p2*l[old[1]][a2];
									for (int a3 = 1; a3 <= old[2]; a3++){
										if (l[old[2]][a3] == 0){
											continue;
										}
										for (int k1 = 0; k1 < this.cdfs[old[2]].length+1; k1++){
											returned[2][0] = k1<this.cdfs[old[2]].length?k1:this.b;;
											for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-1,k1); k2++){
												returned[2][1] = k1<this.cdfs[old[2]].length?k2:this.b;
												double p3 = k1<this.cdfs[old[2]].length?this.getProb(returned[2], old[2],a3-1)*(1-this.fprob):this.fprob;
												p3 = p3*l[old[2]][a3];;
											this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3);
										}
										}
										}
										}
									}
								}
							}
						}
					}
				}
			
		
	}
	
	/**
	 * setting distinctP
	 * @param n
	 */
	protected void setDistinct(int n){
		distinctP = new double[this.b+1][(this.alpha-1)*this.beta];
		this.distinctPMax = new double[this.b+1][this.alpha-1];
		int c = this.alpha*this.ttl;
			for (int i = 0; i < distinctP.length; i++){
				//iterate over possible number of nodes in region
				double prob = Math.pow(2, i-this.b-1);
				double binom = Math.pow(1-prob, n);
				double p = 0;
//				for (int m = 0; m < n-c; m++){
//					p = p + binom*m/(double)(m+c+1);
//					binom = Calc.binomDist(n, m+1, prob);
//					//binom = binom*(n-m-1)/(double)(m+1)*prob/(1-prob); 
//				}
				for (int m = 0; m < n; m++){
					if (m >= n-c) continue;
					p =  binom*m/(double)(m+c+1);
					distinctP[i][0] = distinctP[i][0] +p;
				}
				for (int j = 1; j < this.distinctP[i].length; j++){
					this.distinctP[i][j] = this.distinctP[i][0];
				}
				for (int j = 0; j < this.distinctPMax[i].length; j++){
					this.distinctPMax[i][j] = this.distinctP[i][0];
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
		int[][] max = new int[this.b+1][2];
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
		return this.recusivecombine(returned, t, index, 0, 0, max, contain, p);
	}

}
