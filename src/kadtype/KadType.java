package kadtype;

import java.util.Arrays;

public abstract class KadType {
	protected int b;
	protected int alpha;
	protected int beta;
	protected int[] k;
	protected double[][] l;
	//type of L: only one case matrix or a special case e.g. kad
	public static enum LType{
		SIMPLE, SPECIAL, ALL
	}
	protected LType ltype;
	protected double[] success;
	double[][][] cdfs;
	
	/**
	 * 
	 * @param b: bits
	 * @param alpha
	 * @param beta
	 * @param k: bucket sizes
	 * @param l: further resolved bits
	 * @param ltype
	 */
	public KadType(int b, int alpha, int beta, int[] k, double[][] l, LType ltype){
		this.b = b;
		this.alpha = alpha;
		this.beta = beta;
		this.k = k;
		this.l = l;
		this.ltype = ltype;
		this.setCDFs();
	}
	
	/**
	 * 
	 * @param b: bits
	 * @param alpha
	 * @param beta
	 * @param k: bucket sizes
	 * @param l: further resolved bits
	 * @param ltype
	 */
	public KadType(int b, int alpha, int beta, int k1, double[][] l, LType ltype){
		this(b,alpha,beta,makeArray(k1,b+1),l,ltype);
	}
	
	private static int[] makeArray(int k1, int length){
		int[] karray = new int[length];
		for (int i = 0; i < karray.length; i++){
			karray[i] = k1;
		}
		return karray;
	}
	
	/**
	 * constructor with constant l
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 */
	public KadType(int b, int alpha, int beta, int[] k, int l){
		this(b,alpha,beta,k,new double[][]{{l}},LType.SIMPLE);
	} 
	
	public KadType(int b, int alpha, int beta, int k, int l){
		this(b,alpha,beta,k,new double[][]{{l}},LType.SIMPLE);
	} 
	
	/**
	 * compute routing length distribution
	 * @param n
	 * @return
	 */
	public double[] getRoutingLength(int n){
		this.setSuccess(n);
		double[] cdf = new double[b+1];
		double[] dist = getI();
		cdf[0] = 0;
		double[][]  m = getT1(n);
//		for (int j = 0; j < m[0].length; j++){
//			double sum = 0;
//			for (int i = 0; i < m.length; i++){
//				sum = sum + m[i][j];
//			}
//			System.out.println(sum);
//		}
		dist = matrixMulti(m,dist);
		cdf[1] = dist[0];
		m = getT2(n);
		
		for (int i = 2; i < cdf.length; i++){
			dist = matrixMulti(m,dist);
			cdf[i] = dist[0];
		}
		return cdf;
	}
	

	//COMPONENT COMPUTATION
	/**
	 * initial distribution over distances
	 * @return
	 */
    public double[] getI(){
    	//chance for each bit-diff is fraction of ids in section
		double[] init = new double[b+1];
		double p = 1;
		for (int i = b; i > 0; i--){
			p = p*0.5;
			init[i] = p;
		}
		init[0] = p;
		return init;
	}
	
    /**
     * compute transition matrix in first step
     * @param n
     * @return
     */
	public double[][] getT1(int n){
		int[] lookup = new int[alpha];
		lookup[this.alpha-1] = b+1;
		double[][] t = new double[getIndex(lookup)][b+1];
		for (int d= 0; d <= this.b; d++){
			t[0][d] = this.success[d];
			//distributions over the other distances
			if (t[0][d] < 1){
			double[][] fd = this.getCDFs(d,alpha);
			//compute other entries of t_1
			processCDFsT1(fd,t,d,d);
			}
		}
		
		return t;
	}
	
    /**
     * transition matrix for all further steps
     * @param n
     * @return
     */
	public double[][] getT2(int n){
		int[] lookup = new int[alpha];
		lookup[alpha-1] = b+1;
		int index = getIndex(lookup);
		double[][] t = new double[index][index];
		t[0][0] = 1;
		int[] old = new int[this.alpha];
		constructT2(n,t,old,0);
		return t;
	}
	
	//SUBFUNCTIONS
	/**
	 * index for matrix
	 * @param state (s_1,...,s_alpha)
	 * @return
	 */
	protected abstract int getIndex(int[] lookups);
	
	/**
	 * compute a vector of the probabilities that a node at dist d
	 * has link to target
	 * NEEDS TO BE OVERRIDEN IN CASE ltype==SPECIAL
	 * @param n
	 */
	protected void setSuccess(int n) {
		this.success = new double[this.b+1];
		this.success[0] = 1;
		if (this.ltype == LType.SIMPLE){
			//case: always resolve by constant l more steps
			int m = (int)l[0][0];
			double[] p = new double[b+1];
			p[0] = 0;
			//probability to be in fraction in id space
			double q = Math.pow(2, -m);
			for (int d=b; d > 0;d--){
				p[d] = q;
				q = q*0.5;
			}
			for (int d=b; d > 0;d--){
				double binom = Math.pow(1-p[d],n-2);
			  for (int i = 0; i < n-1; i++){
				  //prob to be successful if there are i nodes besides target in region 
				  if (i < k[d]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i+1);
				  }
				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
			  }
			}
		}
		if (this.ltype == LType.ALL){
			//case: variable l: iterate over all l
			for (int m = 0; m < l.length; m++){
				double[] p = new double[b+1];
				p[0] = 0;
				double q = Math.pow(2, -m);
				for (int d=b; d > 0;d--){
					p[d] = q;
					q = q*0.5;
				}
				for (int d=b; d > 0;d--){
					double binom = Math.pow(1-p[d],n-2);
				  for (int i = 0; i < n-1; i++){
					  if (i < k[d]){
					         this.success[d] = this.success[d] + l[d][m]*binom;
					  } else{
							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i+1);
					  }
					  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
				  }
				}
			}
		}
	}
	
	/**
	 * sets cdfs for i=0..b
	 * 
	 */
	protected void setCDFs(){
		this.cdfs = new double[b+1][][];
		for (int i = 0; i < cdfs.length; i++){
			this.cdfs[i] = this.getCDFs(i, this.beta);
		}
	}
	 
	/**
	 * computes the cdfs over c returned contacts (dxc matrix)
	 * @param d
	 * @param c
	 * 
	 * @return
	 */
   protected abstract double[][] getCDFs(int d, int c);
   
   /**
    * fill matrix t according to cdfs
    * @param cdfs: cumulative distribution functions over next values
    * @param t: transition matrix
    * @param indexOld: index of the previous state
    * @param mindist: closest contact in previous step
    */
   protected abstract void processCDFsT1(double[][] cdfs, double[][] t, int indexOld, int mindist);
	
   /**
    * construct the transition matrix for the second step
    * @param n: #nodes
    * @param t2: transition matrix
    */
  protected void constructT2(int n, double[][] t2, int[] old, int tofill){
	  if (tofill < this.alpha){
	  int start=(tofill==0?0:old[tofill-1]);
	     for (int i = start; i < this.b+1; i++){
		   old[tofill] = i;
		   this.constructT2(n, t2, old, tofill+1);
	     }
	  } else {
		 double nsucc = 1;
		 //TO DO
		 for (int i = 0; i < old.length; i++){
			 nsucc = nsucc*(1-this.success[old[i]]);
		 }
		 int oldindex = this.getIndex(old);
		 t2[0][oldindex] = 1-nsucc;
		 if (nsucc > 0)
		 this.processCDFsT2(n, t2, old, oldindex,nsucc);
	  }
  }
  
  /**
   * work from known index
   * @param n
   * @param t2
   * @param old
   * @param oldindex
   */
  protected abstract void processCDFsT2(int n, double[][] t2, int[] old, int oldindex,double nsucc);
  
  
	 
  /**
   * get lowest alpha values in vals
   * @param vals
   * @return
   */
  protected int[] topAlpha(int[][] vals){
	  int[] top = new int[this.alpha];
	  int[] all = new int[this.alpha*beta];
	  for (int a = 0; a < vals.length; a++){
		  for (int b = 0; b < beta; b++){
			  all[a*beta+b] = vals[a][b];
		  }
	  }
	  Arrays.sort(all);
	  for (int i = 0; i < top.length; i++){
		  top[i] = all[i];
	  }
	  return top;
  }
  
  protected double getProb(int[] returned, double[][] cdf){
	  double p = returned[0]==0?cdf[0][0]:cdf[returned[0]][0]-cdf[returned[0]-1][0];
	  for (int i = 1; i < returned.length; i++){
		  double q = returned[i]==0?cdf[0][i]:cdf[returned[i]][i]-cdf[returned[i]-1][i];
		  p = returned[i-1]==0?(p * q):(p*q/(1-cdf[returned[i-1]-1][i]));
		  if (!(p <= 1)){
			 System.out.println(p + " i="+i+": "+returned[i] + " cdf " +cdf[returned[i]][i]);
		  }
	  }
	  
	  return p;
  }
  
//  protected double getProb(int[][] returned, int[] old){
//	  double pall = 1;
//	  for (int j = 0; j < returned.length; j++){
//	  double p = returned[j][0]==0?this.cdfs[old[j]][0][0]:
//		  this.cdfs[old[j]][0][returned[j][0]]-this.cdfs[old[j]][0][returned[j][0]-1];
//	  for (int i = 1; i < returned[j].length; i++){
//		  double q = returned[j][i]==0?this.cdfs[old[j]][i][0]:
//			  this.cdfs[old[j]][i][returned[j][i]]-this.cdfs[old[j]][i][returned[j][i]-1];
//		  p = returned[j][i-1]==0?(p * q):(p*q/(1-this.cdfs[old[j]][i][returned[j][i-1]]));
//	  }
//	  pall = pall*p;
//	  }
//	  return pall;
//  }
  
  protected double getProb(int[] returned, double[][] cdf, int l){
	  int d = cdf.length;
	  double p = returned[0]==0?cdf[0][l-1]:cdf[returned[0]][l-1]-cdf[returned[0]-1][l-1];
	  for (int i = 1; i < returned.length; i++){
		  double q = returned[i]==0?cdf[0][i*d+l-1]:cdf[returned[i]][i*d+l-1]-cdf[returned[i]-1][i*d+l-1];
		  p = returned[i-1]==0?(p * q):(p*q/(1-cdf[returned[i-1]-1][i*d+l-1]));
	  }
	  return p;
  }
  
  protected double getProb(int[] returned, int nr){
	  double p = returned[0]==0?this.cdfs[nr][0][0]:
		  this.cdfs[nr][returned[0]][0]-this.cdfs[nr][returned[0]-1][0];
	  for (int i = 1; i < returned.length; i++){
		  double q = returned[i]==0?this.cdfs[nr][0][i]:
			  this.cdfs[nr][returned[i]][i]-this.cdfs[nr][returned[i]-1][i];
		  p = returned[i-1]==0?(p * q):(p*q/(1-this.cdfs[nr][returned[i-1]-1][i]));
	  }
	  return p;
  }
  
  protected double getProb(int[] returned, int nr, int l){
	  int d = this.cdfs[nr].length;
	  double p = returned[0]==0?this.cdfs[nr][0][l-1]:
		  this.cdfs[nr][returned[0]][l-1]-this.cdfs[nr][returned[0]-1][l-1];
	  for (int i = 1; i < returned.length; i++){
		  double q = returned[i]==0?this.cdfs[nr][0][i*d+l-1]:
			  this.cdfs[nr][returned[i]][i*d+l-1]-this.cdfs[nr][returned[i]-1][i*d+l-1];
		  p = returned[i-1]==0?(p * q):(p*q/(1-this.cdfs[nr][returned[i-1]-1][i*d+l-1]));
	  }
	  return p;
  }
	
	//STATIC methods for matrix computations
    
    public static double[] matrixMulti(double[][] matrix, double[] vector){
    	double[] res = new double[matrix.length];
    	for (int i = 0; i < res.length; i++){
    		for (int j = 0; j < matrix[i].length; j++){
    			res[i] = res[i] + matrix[i][j]*vector[j];
    		}
    	}
    	return res;
    }

    
}
