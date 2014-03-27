package kadtype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import util.Binom;
import util.DivideUpon;

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
	protected double[][][] cdfs;
	protected double[][] biCoeff; 
	boolean subbuckets=false;
	boolean local = false;
	boolean randomID = false;
	double n;
	
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
		this.setBinom();
	}
	
	private void setBinom() {
		int max = 0;
		for (int i = 0; i < k.length; i++){
			if (k[i] > max){
				max = k[i];
			}
		}
		int max2 = Math.max(this.alpha, this.beta); 
		this.biCoeff = new double[max+1][max2];
		for (int i = 0; i < max+1; i++){
			double binom = 1;
			for (int j = 0; j < Math.min(max2, i+1); j++){
				this.biCoeff[i][j] = binom;
				binom = binom*(i-j)/(double)(j+1);
			}
		}
		
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
		this.n = n;
		this.setSuccess(n);
		double[] cdf = new double[b+1];
		double[] dist = getI();
		cdf[0] = 0;
		double[][]  m = getT1(n);
//		for (int i = 0; i < m[0].length; i++){
//		double sum=0;
//		for (int j = 0; j < m.length; j++){
//			sum = sum + m[j][i];
//			if (m[j][i] < 0 || m[j][i] > 1){
//				System.out.println("oh " + m[j][i]);
//			}
//		}
//		System.out.println(i+ " sum: " + sum );
//	}
		dist = matrixMulti(m,dist);
//		for (int i = 0; i < dist.length; i++){
//			if (dist[i] < 0){
//				//System.out.println(i + " " + dist[i]);
//			}
//		}
		cdf[1] = dist[0];
		m = getT2(n);
		int count = 0;
//		HashMap<Integer,String> map = this.getMap();
//		for (int i = 0; i < m[0].length; i++){
//			double sum=0;
//			for (int j = 0; j < m.length; j++){
//				sum = sum + m[j][i];
//				if (m[j][i] < 0 && i < 10){
//					System.out.println("i= " + map.get(i) + " j= " + map.get(j) + " entry= "+ m[j][i]);
//					count++;
//				}
//			}
//			System.out.println(sum);
//		}	
//			
////			if (sum < 0.99 || sum > 1.01)
////			System.out.println(i+ " sum: " + sum + " " );//+ map.get(i));
//		}
		//System.out.println(count + " " + m.length);
		for (int i = 2; i < cdf.length; i++){
			//System.out.println("step " + i);
			dist = matrixMulti(m,dist);
			cdf[i] = dist[0];
		}
		return cdf;
	}
	
	private HashMap<Integer,int[]> getMap(){
		if (this.alpha == 3){
		HashMap<Integer,int[]> map = new HashMap<Integer,int[]>();
		for (int i = 0; i < this.b+2; i++){
			for (int j = i; j < this.b+2; j++){
				for (int z = j; z < this.b+2; z++){
					int[] c = new int[]{i,j,z};
					map.put(this.getIndex(c), c);
				}
			}
		}
		return map;
		} else {
			throw new IllegalArgumentException("Only implemented for alpha=3");
		}
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
		//if (!this.randomID){
		if (this.success == null)
		this.setSuccess(n);
		int[] lookup = new int[alpha];
		lookup[this.alpha-1] = b+1;
		double[][] t = new double[getIndex(lookup)][b+1];
		for (int d= 0; d <= this.b; d++){
			t[0][d] = this.success[d];
			//distributions over the other distances
			if (t[0][d] < 1){
			//double[][] fd = this.getCDFs(d);
			//compute other entries of t_1
			processCDFsT1(t,d,d,1-t[0][d]);
			}
		}
//		for (int j = 0; j < t[0].length; j++){
//			double s = 0;
//			for (int i = 0; i < t.length; i++){
//				s = s + t[i][j];
//			}
//			System.out.println(s);
//		}
		
		if (this.local){
			this.local = false;
			this.setSuccess(n);
		}
		return t;
//		} else {
//			return this.getT1ID(n);
//		}
	}
	
	public double[][] getT1ID(int n){
		int[] lookup = new int[alpha];
		lookup[this.alpha-1] = b+1;
		double[][] t = new double[getIndex(lookup)][b+1];
		t[0][0] = 1;
		for (int d= 1; d <= this.b; d++){
			processCDFsT1(t,d,d,1);
			for (int j = 1; j < t.length; j++){
				t[0][d] = t[0][d] + this.success[j]*t[j][d];
				t[j][d] = (1-success[j])*t[j][d];
			}
		}
		
		if (this.local){
			this.local = false;
			this.setSuccess(n);
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
	public void setSuccess(int n) {
		if (this.subbuckets || this.local){
			this.setSuccessSubbuckets(n);
			return;
		}
		if (this.randomID) {
			this.setSuccessRandomID(n);
		} else {
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
				int exp = (int) ((n-2)*p[d]);
				Binom bi = new Binom(n-2,p[d],exp);
				double binom;
			  for (int i = exp; i < n-1; i++){
				  binom = bi.getNext();
				  if (i < k[d]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i+1);
				  }
			  }
			  bi.recompute(exp);
			  for (int i = exp-1; i > -1; i--){
				  binom = bi.getBefore();
				  if (i < k[d]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i+1);
				  }
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
					int exp = (int) ((n-2)*p[d]);
					Binom bi = new Binom(n-2,p[d],exp);
					double binom;
				  for (int i = exp; i < n-1; i++){
					  binom = bi.getNext();
					  if (i < k[d]){
					         this.success[d] = this.success[d] +l[d][m]* binom;
					  } else{
							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i+1);
					  }
				  }
				  bi.recompute(exp);
				  for (int i = exp-1; i > -1; i--){
					  binom = bi.getBefore();
					  if (i < k[d]){
					         this.success[d] = this.success[d] + l[d][m]*binom;
					  } else{
							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i+1);
					  }
				  }
					
				}
			}
		}
		}
//		for (int i = 0; i < this.success.length; i++){
//			System.out.println(success[i]);
//		}
	}
	
	private void setSuccessSubbuckets(int n){
		if (this.randomID){
			this.setSuccessSubbucketsRandom(n);
			return;
		}
        this.setN(n);
        this.success = new double[this.b+1];
        this.success[0] = 1;
        int[] addBits = new int[k.length];
        int[] remainder = new int[k.length];
        for (int j = 0; j < addBits.length; j++){
                addBits[j] = (int)Math.floor(Math.log(k[j])/Math.log(2));
                remainder[j] = k[j]-(int)Math.pow(2,addBits[j]);
        }
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
                        //iterate over possible empty regions -> add links
                        int regions = (int)Math.pow(2,addBits[d]);
                        double regionEm = Math.pow(1-p[d]/(double)regions, n-2);
                        int index = Math.max(0, d-addBits[d]);
                        Binom re = new Binom(regions-1,regionEm);
                        for (int r = 0; r < regions; r++){
                                double pr = re.getNext();
                                int start;
                                Binom ra;
                                if (r == regions-1){
                                	ra = new Binom(1, 1,1);
                                	start = r+remainder[d];
                                } else{
                                   ra = new Binom(r+remainder[d], 1/(double)(regions-r));
                                   start = 0;
                                }
                                for (int a = start; a <= r+remainder[d]; a++){
                                  double pa = pr*ra.getNext();
//                                 if (!(pa <= 1)){
//                                	 System.out.println(pa + " " + r + " " + a);
//                                 }
                                 int links = 1 + a;
                        int exp = (int) ((n-2)*p[index]);
                        Binom bi = new Binom(n-2,p[index],exp);
                        double binom;
                 for (int i = exp; i < n-1; i++){
                         binom = pa*bi.getNext();
                         if (i < links){
                         this.success[d] = this.success[d] + binom;
                         } else{
                                         this.success[d] = this.success[d] + binom*(double)(links)/(double)(i+1);
                         }
                 }
                 bi.recompute(exp);
                 for (int i = exp-1; i > -1; i--){
                         binom = pa*bi.getBefore();
                         if (i < links){
                         this.success[d] = this.success[d] + binom;
                         } else{
                                         this.success[d] = this.success[d] + binom*(double)(links)/(double)(i+1);
                         }
                 }
                        }
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
                                int regions = (int)Math.pow(2,addBits[d]);
                                double regionEm = Math.pow(1-p[d]/(double)regions, n-2);
                                int index = Math.max(0, d-addBits[d]);
                                Binom re = new Binom(regions-1,regionEm);
                                for (int r = 0; r < regions; r++){
                                        double pr = re.getNext();
                                        int start;
                                        Binom ra;
                                        if (r == regions-1){
                                        	ra = new Binom(1, 1,1);
                                        	start = r+remainder[d];
                                        } else{
                                           ra = new Binom(r+remainder[d], 1/(double)(regions-r));
                                           start = 0;
                                        }
                                        for (int a = start; a <= r+remainder[d]; a++){
                                          double pa = pr*ra.getNext();
                                         int links = 1 + a;
                                int exp = (int) ((n-2)*p[index]);
                                Binom bi = new Binom(n-2,p[index],exp);
                                double binom;
                         for (int i = exp; i < n-1; i++){
                                 binom = pa*bi.getNext();
                                 if (i < links){
                                 this.success[d] = this.success[d] +l[d][m]* binom;
                                 } else{
                                                 this.success[d] = this.success[d] + l[d][m]*binom*(double)(links)/(double)(i+1);
                                 }
                         }
                         bi.recompute(exp);
                         for (int i = exp-1; i > -1; i--){
                                 binom = pa*bi.getBefore();
                                 if (i < links){
                                 this.success[d] = this.success[d] + l[d][m]*binom;
                                 } else{
                                                 this.success[d] = this.success[d] + l[d][m]*binom*(double)(links)/(double)(i+1);
                                 }
                         }
                                        }
                                }
                        }
                }
        }
//        for (int i = 0; i < this.success.length; i++){
//                System.out.println(success[i]);
//        }
}
	
	private void setSuccessSubbucketsRandom(int n) {
		this.setN(n);
        this.success = new double[this.b+1];
        this.success[0] = 1;
        int[] addBits = new int[k.length];
        int[] remainder = new int[k.length];
        for (int j = 0; j < addBits.length; j++){
                addBits[j] = (int)Math.floor(Math.log(k[j])/Math.log(2));
                remainder[j] = k[j]-(int)Math.pow(2,addBits[j]);
        }
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
                	int c = (int)Math.pow(2,addBits[d]);
                	double factor = Math.pow(2,-addBits[d]);
                	int exp = (int) ((n-1)*p[d]);
    				Binom bi = new Binom(n-1,p[d],exp);
    				double binom;
    				//if (exp > 0){
    			  for (int i = exp; i < n; i++){
    				  binom = bi.getNext();
    				  Binom biSub = new Binom(i,factor);
    				  double succi = 0;
    				  //case 0
    				  double zero = biSub.getNext();
    				  succi = succi + zero*Math.min(k[d]/(double)i, 1);
    				  //non-extremal case
    				  for (int j = 1; j < i; j++){
    					  double binomSub = biSub.getNext();
    					 double succ1 = 1/(double)j;
    					  double succ2 = 0; 
    					  DivideUpon di = new DivideUpon(i-j,c);
    					  for (int e = 1; e < c; e++){
    						  double em = di.getNext();
    						  if (i-e-1 > 0){
    						  succ2 = succ2 +em*Math.min(1, (remainder[d]+c-1-e)*1/(double)(i-e-1));
    						  } else {
    							  succ2 = succ2+em;
    						  }
    					  }
    					  //System.out.println(succ1 + " " + succ2 + " " +);
    					  succi = succi + binomSub*(succ1+(1-succ1)*succ2);
    				  }
    				  //case i
    				  double all = biSub.getNext();
    				  succi = succi + all*Math.min(k[d]/(double)i, 1);
//    				  if (succi > 1){
//    					  System.out.println(succi + " d= " + d + " i="+i);
//    				  }
    				  success[d] = success[d] + binom*succi;
    			  }
    				//}
    			  bi.recompute(exp);
    			  for (int i = exp-1; i > 0; i--){
    				  binom = bi.getBefore();
    				  Binom biSub = new Binom(i,factor);
    				  double succi = 0;
    				  //case 0
    				  double zero = biSub.getNext();
    				  succi = succi + zero*Math.min(k[d]/(double)i, 1);
    				  //non-extremal case
    				  for (int j = 1; j < i; j++){
    					  double binomSub = biSub.getNext();
    					  double succ1 = 1/(double)j;
    					  double succ2 = 0; 
    					  DivideUpon di = new DivideUpon(i-j,c);
    					  for (int e = 1; e < c; e++){
    						  double em = di.getNext();
    						  succ2 = succ2 +em*Math.min(1, (remainder[d]+c-1-e)*1/(double)i);
    					  }
    					  succi = succi + binomSub*(succ1+(1-succ1)*succ2);
    				  }
    				  //case i
    				  double all = biSub.getNext();
    				  succi = succi + all*Math.min(k[d]/(double)i, 1);
    				  success[d] = success[d] + binom*succi;
    				  
    			  }
    			  success[d] = success[d] + bi.getBefore();
                }        
        }
        for (int i = 0; i < this.success.length; i++){
        	System.out.println(this.success[i]);
        }
        //System.exit(0);
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
                        	int c = (int)Math.pow(2,addBits[d]);
                        	double factor = Math.pow(2,-addBits[d]);
                        	int exp = (int) ((n-1)*p[d]);
            				Binom bi = new Binom(n-1,p[d],exp);
            				double binom;
            				for (int i = exp; i < n; i++){
              				  binom = bi.getNext();
              				  Binom biSub = new Binom(i,factor);
              				  double succi = 0;
              				  //case 0
              				  double zero = biSub.getNext();
              				  succi = succi + zero*Math.min(k[d]/(double)i, 1);
              				  //non-extremal case
              				  for (int j = 1; j < i; j++){
              					  double binomSub = biSub.getNext();
              					 double succ1 = 1/(double)j;
              					  double succ2 = 0; 
              					  DivideUpon di = new DivideUpon(i-j,c);
              					  for (int e = 1; e < c; e++){
              						  double em = di.getNext();
              						  succ2 = succ2 +em*Math.min(1, (remainder[d]+c-1-e)*1/(double)i);
              					  }
              					  //System.out.println(succ1 + " " + succ2 + " " +);
              					  succi = succi + binomSub*(succ1+(1-succ1)*succ2);
              				  }
              				  //case i
              				  double all = biSub.getNext();
              				  succi = succi + all*Math.min(k[d]/(double)i, 1);
//              				  if (succi > 1){
//              					  System.out.println(succi + " d= " + d + " i="+i);
//              				  }
              				  success[d] = success[d] + binom*succi*l[d][m];
              			  }
              				//}
              			  bi.recompute(exp);
              			  for (int i = exp-1; i > 0; i--){
              				  binom = bi.getBefore();
              				  Binom biSub = new Binom(i,factor);
              				  double succi = 0;
              				  //case 0
              				  double zero = biSub.getNext();
              				  succi = succi + zero*Math.min(k[d]/(double)i, 1);
              				  //non-extremal case
              				  for (int j = 1; j < i; j++){
              					  double binomSub = biSub.getNext();
              					  double succ1 = 1/(double)j;
              					  double succ2 = 0; 
              					  DivideUpon di = new DivideUpon(i-j,c);
              					  for (int e = 1; e < c; e++){
              						  double em = di.getNext();
              						  succ2 = succ2 +em*Math.min(1, (remainder[d]+c-1-e)*1/(double)i);
              					  }
              					  succi = succi + binomSub*(succ1+(1-succ1)*succ2);
              				  }
              				  //case i
              				  double all = biSub.getNext();
              				  succi = succi + all*Math.min(k[d]/(double)i, 1);
              				  success[d] = success[d] + binom*succi*l[d][m];
              				  
              			  }
              			  success[d] = success[d] + bi.getBefore()*l[d][m];
                        }        
        }
//		
        }
	}

	private void setSuccessRandomID(int n){
		this.setN(n);
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
				int exp = (int) ((n-1)*p[d]);
				Binom bi = new Binom(n-1,p[d],exp);
				double binom;
			  for (int i = exp; i < n; i++){
				  binom = bi.getNext();
				  if (i < k[d]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i);
				  }
			  }
			  bi.recompute(exp);
			  for (int i = exp-1; i > -1; i--){
				  binom = bi.getBefore();
				  if (i < k[d]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d])/(double)(i);
				  }
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
					int exp = (int) ((n-1)*p[d]);
					Binom bi = new Binom(n-1,p[d],exp);
					double binom;
				  for (int i = exp; i < n; i++){
					  binom = bi.getNext();
					  if (i < k[d]){
					         this.success[d] = this.success[d] +l[d][m]* binom;
					  } else{
							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i);
					  }
				  }
				  bi.recompute(exp);
				  for (int i = exp-1; i > -1; i--){
					  binom = bi.getBefore();
					  if (i < k[d]){
					         this.success[d] = this.success[d] + l[d][m]*binom;
					  } else{
							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i);
					  }
				  }
					
				}
			}
		}
//		double[][] succ = new double[this.b+1][this.alpha];
//		for (int i = 0; i < succ[0].length; i++){
//			succ[0][i] = 1;
//		}
//		double p = 1;
//		for (int i = b; i > 0; i--){
//			p=p*0.5;
//			double[] pA = new double[this.alpha];
//			double[] pB = new double[this.alpha];
//			for (int j = 0; j < pA.length; j++){
//				pA[j] = Math.pow(1-p, n-1-j);
//			}
//			if (pA[0] > 0){
//				double pdash = p/(1-p);
//				int exp = (int) ((n-1)*pdash);
//				Binom bi = new Binom(n-1,pdash,exp);
//				double binom;
//			  for (int j = exp; j < n; j++){
//				  binom = bi.getNext();
//				  for (int h = 0; h < pB.length; h++){
//				   pB[h] = pB[h] + binom*(h+1)/(double)(j+h+1);
//				  }
//			  }
//			  bi.recompute(exp);
//			  for (int j = exp-1; j > -1; j--){
//				  binom = bi.getBefore();
//				  for (int h = 0; h < pB.length; h++){
//					   pB[h] = pB[h] + binom*(h+1)/(double)(j+h+1);
//					  }
//			  }
//			}
//			for (int h = 0; h < pA.length; h++){
//			   succ[i][h] = pA[h]*pB[h];
//			}   
//			//System.out.println(i + " " + success[i]);
//		}
//		int[] lookup = new int[alpha];
//		lookup[this.alpha-1] = b+1;
//		this.success = new double[this.getIndex(lookup)];
//		HashMap<Integer,int[]> map = this.getMap();
//		this.success[0] = 1;
//		for (int i = 1; i < this.success.length; i++){
//			int[] c = map.get(i);
//			int min = c[0];
//			int count = 0;
//			for (int j = 1; j < c.length; j++){
//				if (c[j] == min){
//					count++;
//				} else {
//					break;
//				}
//			}
//			this.success[i] = succ[min][count];
//		}
	}
	
	

	
	/**
//	 * sets cdfs for i=0..b
	 * 
	 */
	protected void setCDFs(){
		this.cdfs = new double[b+1][][];
		for (int i = 0; i < cdfs.length; i++){
			this.cdfs[i] = this.getCDFs(i);
//			for (int j = 0; j < this.cdfs[i].length-1; j++){
//				for (int k = 0; k < this.cdfs[i][j].length; k++){
//				if (!(this.cdfs[i][j][k] < 1)){
//					System.out.println(this.cdfs[i][j][k]  + " i="+i+" j="+j+" k="+k);
//				}
//				}
//			}
		}
	}
	 
	/**
	 * computes the cdfs over c returned contacts (dxc matrix)
	 * @param d
	 * @param c
	 * 
	 * @return
	 */
   protected abstract double[][] getCDFs(int d);
   
   /**
    * fill matrix t according to cdfs
    * @param cdfs: cumulative distribution functions over next values
    * @param t: transition matrix
    * @param indexOld: index of the previous state
    * @param mindist: closest contact in previous step
    */
   protected abstract void processCDFsT1(double[][] t, int indexOld, int mindist, double nsucc);
	
   /**
    * construct the transition matrix for the second step
    * @param n: #nodes
    * @param t2: transition matrix
    */
  protected void constructT2(int n, double[][] t2, int[] old, int tofill){
	 // if (!this.randomID){
	  if (tofill < this.alpha){
		  //set next entry
	  int start=(tofill==0?0:old[tofill-1]);
	     for (int i = start; i < this.b+1; i++){
		   old[tofill] = i;
		   this.constructT2(n, t2, old, tofill+1);
	     }
	  } else {
		  //compute possibilities for next states
		  //prob to be successful in next step
		 double nsucc = 1;
		 for (int i = 0; i < old.length; i++){
			 nsucc = nsucc*(1-this.success[old[i]]);
		 }
		 int oldindex = this.getIndex(old);
		 t2[0][oldindex] = 1-nsucc;
		 //not successful => system-specific
		 if (nsucc > 0)
		 this.processCDFsT2(n, t2, old, oldindex,nsucc);
	  }
//	  } else {
//		  if (tofill < this.alpha){
//			  //set next entry
//		  int start=(tofill==0?0:old[tofill-1]);
//		     for (int i = start; i < this.b+1; i++){
//			   old[tofill] = i;
//			   this.constructT2(n, t2, old, tofill+1);
//		     }
//		  } else {
//			  int oldindex = this.getIndex(old);
//			 this.processCDFsT2(n, t2, old, oldindex,1);
//			 for (int j = 1; j < t2.length; j++){
//				 t2[0][oldindex] = t2[0][oldindex] + this.success[j]*t2[j][oldindex];
//				 t2[j][oldindex] = t2[j][oldindex]*(1-this.success[j]); 
//			 }
//		  }  
//	  }
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
  
//  /**
//   * probability that returned is next step given the cdf
//   * => use for T1
//   * @param returned
//   * @param cdf
//   * @return
//   */
//  protected double getProb(int[] returned, double[][] cdf){
//	  double p = returned[0]==0?cdf[0][0]:cdf[returned[0]][0]-cdf[returned[0]-1][0];
//	  for (int i = 1; i < returned.length; i++){
//		   double q = returned[i]==0?cdf[0][i]:cdf[returned[i]][i]-cdf[returned[i]-1][i];
//		  if (returned[i-1] == 0){
//			  p = p*q;
//		  } else {
//			  if (cdf[returned[i-1]-1][i] >= 1){
//				  return 0;
//			  } else {
//				  p = p*q/(1-cdf[returned[i-1]-1][i]);
//			  }
//		  }
//	  }
//	  return p;
//  }
//  
//
//  /**
//   * probability that returned is next step given the cdf and resolved bits
//   * => use for T1, LType.ALL
//   * @param returned
//   * @param cdf
//   * @param l
//   * @return
//   */  
//  protected double getProb(int[] returned, double[][] cdf, int l){
//	  int d = cdf.length;
//	  double p = returned[0]==0?cdf[0][l-1]:cdf[returned[0]][l-1]-cdf[returned[0]-1][l-1];
//	  for (int i = 1; i < returned.length; i++){
//		  double q = returned[i]==0?cdf[0][i*d+l-1]:cdf[returned[i]][i*d+l-1]-cdf[returned[i]-1][i*d+l-1];
//          if (returned[i-1]==0){
//			  p = p*q;
//		  } else {
//			  if (cdf[returned[i-1]-1][i*d+l-1] >= 1){
//				  return 0;
//			  } else {
//				  p = p*q/(1-cdf[returned[i-1]-1][i*d+l-1]);
//			  }
//		  }
//      }
//	  return p;
//  }
  
  /**
   * probability that returned is next step given the cdf-nr
   * @param returned
   * @param nr: nr of cdf
   * @return
   */
  protected double getProb(int[] returned, int nr, int l){
	  if (this.subbuckets || this.local){
		  if (returned.length == 3){
			  return this.getProbSubbucketsC3(returned, nr, l);
		  } else {
			  if (returned.length == 2){
				  return this.getProbSubbucketsC2(returned, nr, l);
			  } else {
				  throw new IllegalArgumentException("Subbuckets not implemented for this alpha and beta");
			  }
		  }
	  }
	  double p = 1;
	  int c = 0;
	  int old = -1;
	  Vector<Integer> ys = new Vector<Integer>();
	  Vector<Integer> cs = new Vector<Integer>();
	  for (int i = 0; i < returned.length; i++){
		  if (old != returned[i]){
			   if (c > 0){
				  ys.add(old);
				  cs.add(c);
				 }
			   c = 1;
				old = returned[i];
		  } else {
			  c++;
		  }
	  }
	  if ( c > 0){
		  ys.add(old);
		  cs.add(c);
	  }
	
	  
	  double[][] cdf = this.cdfs[nr];
	  double prob;
	  if (returned[0] > 0){
		  if (cdf[returned[0]-1][l] < 1){
			  prob = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
			   }else {
				  return 0;
			  }
	 } else {
		  prob = cdf[0][l];
	  }
	  double[] coeffs = this.biCoeff[k[nr]];
	  if (ys.size() > 1){
		  p = p*coeffs[cs.get(0)]*Math.pow(prob, cs.get(0))*Math.pow(1-cdf[ys.get(0)][l], k[nr]-cs.get(0));
	  } else {
		  double sum = 1;
		  if (returned[0] > 0){
		    sum = Math.pow(1 - cdf[returned[0]-1][l], k[nr]);
		  }
		  for (int i = 0; i < returned.length; i++){
			  sum = sum - coeffs[i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(0)][l], k[nr]-i);
		   }
		  p = p*sum;
		  return p;
	  } 
	  int remain = k[nr]-cs.get(0);
	  for (int j = 1; j < ys.size()-1; j++){
		   p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
		  prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
		  p=p*this.biCoeff[remain][cs.get(j)]*Math.pow(prob, cs.get(j))*Math.pow(1-cdf[ys.get(j)][l], remain-cs.get(j));
		  remain = remain-cs.get(j);
	  }
	  if (ys.size() > 1){
		  int j = ys.size()-1;
		  p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
		  
		 prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
		  double sum = Math.pow((1-cdf[ys.get(j)-1][l]), remain);
		  for (int i = 0; i < cs.get(j); i++){
			  //if (remain == -1) System.out.println("oh");
			  sum = sum - this.biCoeff[remain][i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(j)][l], remain-i);
		  }
		  p = p*sum;
	  }
	  return p;
  }
  
  private double getProbSubbucketsC2(int[] returned, int nr, int l){
	  int c0 = returned[0];
	  int c1 = returned[1];
	  double pall = 0;
	  int addBit = (int)Math.floor(Math.log(this.k[nr])/Math.log(2));
	  int remainder = this.k[nr] - (int)Math.pow(2, addBit);
	  int improve = nr - l -1 - addBit;
	  if (improve < 0) {
		  return 0;
	  }
	  //no contacts in region returned
	  if (c0 > improve) {
		  return 0;
	  } 
	  int regions = (int)Math.pow(2,addBit);
		double regionEm = Math.pow(1-Math.pow(2, -b+nr-1-l)/(double)regions, n-2);
		Binom re = new Binom(regions-1,regionEm);
        for (int r = 0; r < regions; r++){
                double pr = re.getNext();
                int start;
                Binom ra;
                if (r == regions-1){
                	ra = new Binom(1, 1,1);
                	start = r+remainder;
                } else{
                   ra = new Binom(r+remainder, 1/(double)(regions-r));
                   start = 0;
                }
                for (int a = start; a <= r+remainder; a++){
                  double pa = pr*ra.getNext();
			    int links = 1 + a;
			    //more links into region than nodes selected
			    if (c1 > improve && links > 1) {
			    	continue;
			    }
			    //only one link into region but more nodes returned
			    if (c1 <= improve && links == 1) {
			    	continue;
			    }
			    
			    int index = Math.max(nr-addBit, 0);
			    double p = 1;
				 if (c1 <= improve){
			    //case both links into region => as before but with links rather than k[d] 
			    //and cdf nr-addBit rather than nr
			    int c = 0;
				  int old = -1;
				  Vector<Integer> ys = new Vector<Integer>();
				  Vector<Integer> cs = new Vector<Integer>();
				  for (int i = 0; i < returned.length; i++){
					  if (old != returned[i]){
						   if (c > 0){
							  ys.add(old);
							  cs.add(c);
							 }
						   c = 1;
							old = returned[i];
					  } else {
						  c++;
					  }
				  }
				  if ( c > 0){
					  ys.add(old);
					  cs.add(c);
				  }
				
				  
				  double[][] cdf = this.cdfs[index];
				  double prob;
				  if (returned[0] > 0){
					  if (cdf[returned[0]-1][l] < 1){
						  prob = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
						   }else {
							  return 0;
						  }
				 } else {
					  prob = cdf[0][l];
				  }
				  double[] coeffs = this.biCoeff[links];
				  if (ys.size() > 1){
					  p = p*coeffs[cs.get(0)]*Math.pow(prob, cs.get(0))*Math.pow(1-cdf[ys.get(0)][l], links-cs.get(0));
				  } else {
					  double sum = 1;
					  if (returned[0] > 0){
					    sum = Math.pow(1 - cdf[returned[0]-1][l], links);
					  }
					  for (int i = 0; i < returned.length; i++){
						  sum = sum - coeffs[i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(0)][l], links-i);
					   }
					  p = p*sum;
				  } 
				  int remain = links-cs.get(0);
				  for (int j = 1; j < ys.size()-1; j++){
					   p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
					  prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
					  p=p*this.biCoeff[remain][cs.get(j)]*Math.pow(prob, cs.get(j))*Math.pow(1-cdf[ys.get(j)][l], remain-cs.get(j));
					  remain = remain-cs.get(j);
				  }
				  if (ys.size() > 1){
					  int j = ys.size()-1;
					  p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
					  
					 prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
					  double sum = Math.pow((1-cdf[ys.get(j)-1][l]), remain);
					  for (int i = 0; i < cs.get(j); i++){
						  //if (remain == -1) System.out.println("oh");
						  sum = sum - this.biCoeff[remain][i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(j)][l], remain-i);
					  }
					  p = p*sum;
				  }
				  
			    } else {
			    	//one link into region
			    	//a) prob for this link
			    	double[][] cdf = this.cdfs[index];
					if (returned[0] > 0){
						  if (cdf[returned[0]-1][l] < 1){
							  p = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
							   }else {
								  p=0;
							  }
					 } else {
						  p = cdf[0][l];
					  }
					  //b) the other link: no contacts in closer subbuckets (counc-1)
					  int countc = (int)Math.pow(2, addBit-(nr-c1-l));
					  if (r > countc-2){
					  for (int i = 0; i < countc-1; i++){
						  p = p*(r-i)/(regions-1-i);
					  }
					  } else {
						  p = 0;
					  }
					  //c) the other link: at least one contact in subbuckets at this level (countc)
					  if (2*countc <= r+1){
						  //p = p*(1-Calc.binom(regions-2*countc, regions-1-r)/(double)Calc.binom(regions-countc, regions-1-r));
						  p = p*(1-getBiDivide(regions-2*countc,regions-1-r,regions-countc,regions-1-r));
					  }
					  
			    }
			    pall = pall + p*pa;
			}
		}
	  return pall;
  }
  
  public double getProbSubbucketsC3(int[] returned,int nr,int l){
	  int c0 = returned[0];
	  int c1 = returned[1];
	  int c2 = returned[2];
	  
	  double pall = 0;
	  int addBit = (int)Math.floor(Math.log(this.k[nr])/Math.log(2));
	  int remainder = this.k[nr] - (int)Math.pow(2, addBit);
	  int improve = nr - l -1 - addBit;
	  if (improve < 0) {
		  return 0;
	  }
	  //no contacts in region returned
	  if (c0 > improve) return 0;
	  int inr = 0;
	  for (int i = 0; i < 3; i++){
		  if (returned[i] > improve){
			  break;
		  }
		  inr++;
	  }
	  int regions = (int)Math.pow(2,addBit);
		double regionEm = Math.pow(1-Math.pow(2, -b+nr-1-l)/(double)regions, n-2);
		//System.out.println(regionEm + " " + nr + " " + Math.pow(2, b-nr-1-l)/(double)regions);
		Binom re = new Binom(regions-1,regionEm);
            for (int r = 0; r < regions; r++){
            	if (r == regions-1){
    				if (inr != 3){
    					continue;
    				}
    			}
                    double pr = re.getNext();
                    int start;
                    Binom ra;
                    if (r == regions-1){
                    	ra = new Binom(1, 1,1);
                    	start = r+remainder;
                    } else{
                       ra = new Binom(r+remainder, 1/(double)(regions-r));
                       start = 0;
                    }
                    for (int a = start; a <= r+remainder; a++){
                      double pa = pr*ra.getNext();
				if (pa == 0) continue;
			    int links = 1 + a;
			    
			    //too many links in region
			    if (links > inr && inr != 3){
			    	continue;
			    }
			    //not enough links in region
			    if (links < inr){
			    	continue;
			    }
			    int index = Math.max(nr-addBit, 0);
			    double p = 1;
			    if (inr == 3){
			    	//normal but additional addBits, and only links potential contacts
			    	int c = 0;
					  int old = -1;
					  Vector<Integer> ys = new Vector<Integer>();
					  Vector<Integer> cs = new Vector<Integer>();
					  for (int i = 0; i < returned.length; i++){
						  if (old != returned[i]){
							   if (c > 0){
								  ys.add(old);
								  cs.add(c);
								 }
							   c = 1;
								old = returned[i];
						  } else {
							  c++;
						  }
					  }
					  if ( c > 0){
						  ys.add(old);
						  cs.add(c);
					  }
					
					  
					  double[][] cdf = this.cdfs[index];
					  double prob;
					  if (returned[0] > 0){
						  if (cdf[returned[0]-1][l] < 1){
							  prob = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
							   }else {
								  return 0;
							  }
					 } else {
						  prob = cdf[0][l];
					  }
					  double[] coeffs = this.biCoeff[links];
					  if (ys.size() > 1){
						  p = p*coeffs[cs.get(0)]*Math.pow(prob, cs.get(0))*Math.pow(1-cdf[ys.get(0)][l], links-cs.get(0));
					  } else {
						  double sum = 1;
						  if (returned[0] > 0){
						    sum = Math.pow(1 - cdf[returned[0]-1][l], links);
						  }
						  for (int i = 0; i < returned.length; i++){
							  sum = sum - coeffs[i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(0)][l], links-i);
						   }
						  p = p*sum;
					  } 
					  int remain = links-cs.get(0);
					  for (int j = 1; j < ys.size()-1; j++){
						   p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
						  prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
						  p=p*this.biCoeff[remain][cs.get(j)]*Math.pow(prob, cs.get(j))*Math.pow(1-cdf[ys.get(j)][l], remain-cs.get(j));
						  remain = remain-cs.get(j);
					  }
					  if (ys.size() > 1){
						  int j = ys.size()-1;
						  p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
						  
						 prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
						  double sum = Math.pow((1-cdf[ys.get(j)-1][l]), remain);
						  for (int i = 0; i < cs.get(j); i++){
							  //if (remain == -1) System.out.println("oh");
							  sum = sum - this.biCoeff[remain][i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(j)][l], remain-i);
						  }
						  p = p*sum;
					  }

			    } else {
			    	if (inr == 2){
			    		
			    		//only first two links for normal treatment
			    		int c = 0;
						  int old = -1;
						  Vector<Integer> ys = new Vector<Integer>();
						  Vector<Integer> cs = new Vector<Integer>();
						  for (int i = 0; i < 2; i++){
							  if (old != returned[i]){
								   if (c > 0){
									  ys.add(old);
									  cs.add(c);
									 }
								   c = 1;
									old = returned[i];
							  } else {
								  c++;
							  }
						  }
						  if ( c > 0){
							  ys.add(old);
							  cs.add(c);
						  }
						
						  
						  double[][] cdf = this.cdfs[index];
						  double prob;
						  if (returned[0] > 0){
							  if (cdf[returned[0]-1][l] < 1){
								  prob = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
								   }else {
									  return 0;
								  }
						 } else {
							  prob = cdf[0][l];
						  }
						  double[] coeffs = this.biCoeff[links];
						  if (ys.size() > 1){
							  p = p*coeffs[cs.get(0)]*Math.pow(prob, cs.get(0))*Math.pow(1-cdf[ys.get(0)][l], links-cs.get(0));
						  } else {
							  
							  double sum = 1;
							  if (returned[0] > 0){
							    sum = Math.pow(1 - cdf[returned[0]-1][l], links);
							  }
							  for (int i = 0; i < returned.length-1; i++){
								  sum = sum - coeffs[i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(0)][l], links-i);
							   }
							  p = p*sum;
						  } 

						  
						  int remain = links-cs.get(0);
						  for (int j = 1; j < ys.size()-1; j++){
							   p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
							  prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
							  p=p*this.biCoeff[remain][cs.get(j)]*Math.pow(prob, cs.get(j))*Math.pow(1-cdf[ys.get(j)][l], remain-cs.get(j));
							  remain = remain-cs.get(j);
						  }
						  if (ys.size() > 1){
							  int j = ys.size()-1;
							  p = p*Math.pow(1/(1-cdf[ys.get(j-1)][l]), remain);
							  
							 prob = (cdf[ys.get(j)][l] - cdf[ys.get(j)-1][l]);
							  double sum = Math.pow((1-cdf[ys.get(j)-1][l]), remain);
							  for (int i = 0; i < cs.get(j); i++){
								  //if (remain == -1) System.out.println("oh");
								  sum = sum - this.biCoeff[remain][i]*Math.pow(prob, i)*Math.pow(1-cdf[ys.get(j)][l], remain-i);
							  }
							  p = p*sum;
						  }
						  //third edge not in region
						//b) the other link: no contacts in closer subbuckets (counc-1)
						  int countc = (int)Math.pow(2, addBit-(nr-c2-l));
						  if (r > countc-2){
						  for (int i = 0; i < countc-1; i++){
							  p = p*(r-i)/(regions-1-i);
						  }
						  }else {
							  p = 0;
						  }
						  //c) the other link: at least one contact in subbuckets at this level (countc)
						  if (2*countc <= r+1){
							 // double d = Calc.binom(regions-2*countc, regions-1-r);
//							  p = p*(1-Calc.binom(regions-2*countc, regions-1-r)/(double)Calc.binom(regions-countc, regions-1-r));
							  p = p*(1-getBiDivide(regions-2*countc,regions-1-r,regions-countc,regions-1-r));
						  }
						  
			    	} else {
			    		//two links not in region
			    		//link in region
			    		double[][] cdf = this.cdfs[index];
						if (returned[0] > 0){
							  if (cdf[returned[0]-1][l] < 1){
								  p = (cdf[returned[0]][l] - cdf[returned[0]-1][l]);
								   }else {
									  return 0;
								  }
						 } else {
							  p = cdf[0][l];
						  }
				    	
						//links not in region
						//countc-1 empty buckets before
						int countc = (int) Math.pow(2, addBit - (nr - c1-l));
						if (r > countc-2){
						  for (int i = 0; i < countc-1; i++){
							  p = p*(r-i)/(regions-1-i);
						  }
						  } else {
							  p = 0;
						  }
						
						  //c) the other link: at least one contact in subbuckets at this level (countc)
						double pe = 1;
						  if (2*countc <= r+1){
							 // pe = 1-Calc.binom(regions-2*countc, regions-1-r)/(double)Calc.binom(regions-countc, regions-1-r);
							  pe = 1-getBiDivide(regions-2*countc,regions-1-r,regions-countc,regions-1-r);
						  }
						p = p*pe;
						if (c1 == c2){
							//there are at least two non-empty subbuckets with that prefix length
                            if (pe != 1) {
//									double pN = (pe - Calc.binom(regions - 2 * countc,
//											regions - 2 - r)
//											* Calc.binom(countc, 1)
//											/ (double) Calc.binom(regions - countc,
//													regions - 1 - r))
//											/ (pe);
                            	double pN = (pe-getBiDivide(regions-2*countc,regions-2-r,regions-countc,regions-1-r)
                            			*countc)/pe;
									// or if not, there are at least two links into the
									// one region
									double p2 = (1 - pN)
											* (1 - Math.pow(1 - 1 / (double) (regions
													- r - 1), r + remainder));
									p = p * (pN + p2);
								} else {
									if (regions - countc >= regions-1-r){
										if (2*countc <= r+2){
//											double pN = 1-Calc.binom(regions - 2 * countc,
//													regions - 2 - r)
//													* Calc.binom(countc, 1)
//													/ (double) Calc.binom(regions - countc,
//															regions - 1 - r);
											double pN =1-getBiDivide(regions-2*countc,regions-2-r,regions-countc,regions-1-r)
													*countc;
											double p2 = (1 - pN)
													* (1 - Math.pow(1 - 1 / (double) (regions
															- r - 1), r + remainder));
											p = p*(pN+p2);
										}
									}

								}
        			    	
						} else {
							//there is exactly one non-empty region and countc
							double pE=1;
							if (pe != 1) {
//								double pN = (pe - Calc.binom(regions - 2 * countc,
//										regions - 2 - r)
//										* Calc.binom(countc, 1)
//										/ (double) Calc.binom(regions - countc,
//												regions - 1 - r))
//										/ (pe);
								double pN = (pe -getBiDivide(regions-2*countc,regions-2-r,regions-countc,regions-1-r)
										*countc)/pe;
								// or if not, there are at least two links into the
								// one region
								double p2 = Math.pow(1 - 1 / (double) (regions
												- r - 1), r + remainder);
								pE = (1-pN)*p2;
							} else {
								if (regions - countc >= regions-1-r){
									if (2*countc <= r+2){
//										double pN = 1-Calc.binom(regions - 2 * countc,
//												regions - 2 - r)
//												* Calc.binom(countc, 1)
//												/ (double) Calc.binom(regions - countc,
//														regions - 1 - r);
										double pN = 1- getBiDivide(regions-2*countc,regions-2-r,regions-countc,regions-1-r)
												 *countc;
										double p2 = Math.pow(1 - 1 / (double) (regions
														- r - 1), r + remainder);
										pE = (1-pN)*p2;
									}
								} else {
									pE = 0;
								}

							}
							p = p*pE;
							int countc2 = (int) Math.pow(2, addBit - (nr - c2-l));
							if ( 2*countc  <= r+2) {
								//p = p* Calc.binom(regions-countc2, regions-r-2)/(double)Calc.binom(regions-2*countc,regions-r-2);
							    p=p*getBiDivide(regions-countc2,regions-2-r,regions-2*countc,regions-2-r);
							} else {
								p = 0;
							}
							// there is at least one non-empty region at c2
							if (regions - 2 * countc2 >= regions - 2 - r) {
//								p = p
//										* (1 - Calc.binom(regions - 2 * countc2,
//												regions - 2 - r)
//												/ (double) Calc.binom(
//														regions - countc2, regions - 2
//																- r));
								p = p*(1-getBiDivide(regions-2*countc2,regions-2-r,regions-countc2,regions-2-r));
							}
						}  
			    	}
//			    	if (!(p <= 1)){
//						  System.out.println(p+ " " + nr + " " + c0 + " " + c1 + " " + c2);
//					  }
			    }
//		    	if (!(p <= 1)){
//					  System.out.println("P " + p+ " " + nr + " " + c0 + " " + c1 + " " + c2);
//				  }
			    double pallNew = pall + p*pa; 
//				if (!(pallNew < 1)){
//					  System.out.println("PAll " + pall + " " + nr + " " + p + " " + pa + " r " + r + " a " + a);
//				  }
				pall = pallNew;
			}
		}
//		if (!(pall < 1)){
//			  System.out.println(pall + " " + nr);
//		  }
		return pall;
  }
  
//  /**
//   * probability that returned is next step given the cdf-nr and resolved bits
//   * => use for T2, LType.ALL
//   * @param returned
//   * @param cdf
//   * @param l
//   * @return
//   */  
//  protected double getProb(int[] returned, int nr, int l){
//	  int d = this.cdfs[nr].length;
//	  double p = returned[0]==0?this.cdfs[nr][0][l-1]:
//		  this.cdfs[nr][returned[0]][l-1]-this.cdfs[nr][returned[0]-1][l-1];
//	  for (int i = 1; i < returned.length; i++){
//		  //get prob for returned[i]
//		  double q = returned[i]==0?this.cdfs[nr][0][i*d+l-1]:
//			  this.cdfs[nr][returned[i]][i*d+l-1]-this.cdfs[nr][returned[i]-1][i*d+l-1];
//		  //condition on value before
//		  if (returned[i-1]==0){
//			  p = p*q;
//		  } else {
//			  if (this.cdfs[nr][returned[i-1]-1][i*d+l-1] >= 1){
//				  return 0;
//			  } else {
//				  p = p*q/(1-this.cdfs[nr][returned[i-1]-1][i*d+l-1]);
//			  }
//		  }
//	  }
//	  return p;
//  }
	
	//STATIC methods for matrix computations
    
    public static double[] matrixMulti(double[][] matrix, double[] vector){
    	double[] res = new double[matrix.length];
    	for (int i = 0; i < res.length; i++){
    		for (int j = 0; j < matrix[i].length; j++){
//    			if (res[i] + matrix[i][j]*vector[j] < 0){
//    				System.out.println("i ="+ i + "j= " + j + " " + res[i] + " vec " + vector[j] + " maxtrix " + matrix[i][j]);
//    				System.exit(0);
//    			}
    			res[i] = res[i] + matrix[i][j]*vector[j];
    			
    			
    		}
    	}
    	return res;
    }
    
    public double getExpectedDegree(int n){
    	double exp = 0;
    	for (int i = this.b; i > 0; i--){
    		if (this.ltype == LType.SIMPLE){
    			int m = (int)l[0][0];
    			double p = Math.pow(2, -m-(b-i));
    			Binom bi = new Binom(n-1,p);
    			bi.recompute(k[i]);
    			double sum = 0;
    			for (int j = 0; j < k[i]; j++){
    				double binom = bi.getBefore();
    				sum = sum + binom;
    				exp = exp + binom*(k[i]-j-1);
    			}
    			exp = exp + k[i]*(1-sum);
    		}
    		if (this.ltype == LType.ALL){
    			for (int m = 0; m < l[0].length; m++){
    				if (l[i][m] > 0){
    					double p = Math.pow(2, -m-(b-i));
    	    			Binom bi = new Binom(n-1,p);
    	    			double f = Math.pow(2, m-1)*l[i][m];
    	    			double sum = 0;
    	    			for (int j = 0; j < k[i]; j++){
    	    				double binom = bi.getNext();
    	    				sum = sum + binom;
    	    				exp = exp + binom*j*f;
    	    			}
    	    			exp = exp + k[i]*(1-sum)*f;
    				}
    			}
    		}
    	}
    	return exp;
    }

    public void setSubbuckets(boolean sub){
    	this.subbuckets = sub;
    	this.local = false;
    }
    
    public void setLocal(boolean local){
    	this.local = local;
    	this.subbuckets = false;
    }
    
    public void setRandomID(boolean id){
    	this.randomID = id; 
    }
    
    public void setN(int n){
    	this.n = n;
    }
    
    public static double getBiDivide(int a, int b, int c, int d){
    	double p = 1;
    	int m = Math.min(d, b);
    	for (int i = 0; i < m; i++){
    		p = p *(a-i)/(double)(c-i);
    	}
    	if (m == b){
    		for (int i = m; i< d; i++){
    			p = p *(i+1)/(double)(c-i);
    		}
    	} else {
    		for (int i = m; i< b; i++){
    			p = p *(a-i)/(double)(i+1);
    		}
    	}
    	return p;
    }
    
}
