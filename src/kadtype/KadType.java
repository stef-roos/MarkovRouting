package kadtype;

public abstract class KadType {
	private int b;
	private int alpha;
	private int beta;
	private int[] k;
	private double[][] l;
	//type of L: only one case matrix or a special case e.g. kad
	public static enum LType{
		SIMPLE, SPECIAL, ALL
	}
	private LType ltype;
	private double[] success;
	
	public KadType(int b, int alpha, int beta, int[] k, double[][] l, LType ltype){
		this.b = b;
		this.alpha = alpha;
		this.beta = beta;
		this.k = k;
		this.l = l;
		this.ltype = ltype;
	}
	
	public KadType(int b, int alpha, int beta, int[] k, int l){
		this(b,alpha,beta,k,new double[][]{{l}},LType.SIMPLE);
	} 
	
	public double[] getRoutingLength(int n){
		this.setSuccess(n);
		double[] cdf = new double[b+1];
		double[] dist = getI();
		cdf[0] = dist[0];
		double[][]  m = getT1(n);
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
	
    public double[] getI(){
		double[] init = new double[b+1];
		double p = 1;
		for (int i = b; i > 0; i--){
			p = p*0.5;
			init[i] = p;
		}
		init[0] = p;
		return init;
	}
	
	public double[][] getT1(int n){
		double[] lookup = new double[alpha];
		lookup[0] = b+1;
		double[][] t = new double[getIndex(lookup)][b+1];
		for (int d= 0; d <= this.b; d++){
			t[0][d] = this.success[d];
			//distributions over the other distances
			double[][] fd = this.getCDFs(d,alpha);
			//compute other entries of t_1
			processCDFs(fd,t,d,d);
		}
		
		return t;
	}
	


	public double[][] getT2(int n){
		double[] lookup = new double[alpha];
		lookup[0] = b+1;
		int index = getIndex(lookup);
		double[][] t = new double[index][index];
		constructT2(n,t);
		return t;
	}
	
	//SUBFUNCTIONS
	/**
	 * index for matrix
	 * @param state (s_1,...,s_alpha)
	 * @return
	 */
	protected abstract int getIndex(double[] lookups);
	
	/**
	 * compute a vector of the probabilities that a node at dist d
	 * has link to target
	 * NEEDS TO BE OVERRIDEN IN CASE ltype==SPECIAL
	 * @param n
	 */
	protected void setSuccess(int n) {
		this.success = new double[this.b+1];
		if (this.ltype == LType.SIMPLE){
			int m = (int)l[0][0];
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
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i+1);
				  }
				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
			  }
			}
		}
		if (this.ltype == LType.ALL){
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
	 * computes the cdfs over c returned contacts
	 * @param d
	 * @param c
	 * @return
	 */
   protected abstract double[][] getCDFs(int d, int c);
   
   protected abstract void processCDFs(double[][] cdfs, double[][] t, int indexOld, int mindist);
	
  protected abstract void constructT2(int n, double[][] t2);	
	 
	
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
