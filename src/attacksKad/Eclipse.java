package attacksKad;

import kadtype.KadType.LType;
import kadtype.KadTypeCDFs;

public abstract class Eclipse extends KadTypeCDFs{
	protected int attackers;
	double[][] attackProb;
	
	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 * @param ltype
	 */
	public Eclipse(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype, int att) {
		super(b, alpha, beta, k, l, ltype);
		this.attackers = att;
	}
	
	public Eclipse(int b, int alpha, int beta, int k, double[][] l,
			LType ltype, int att) {
		super(b, alpha, beta, k, l, ltype);
		this.attackers = att;
	}
	
	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 */
	public Eclipse(int b, int alpha, int beta, int[] k, int l, int att) {
		super(b, alpha, beta, k, l);
		this.attackers = att;
	}
	
	public Eclipse(int b, int alpha, int beta, int k, int l, int att) {
		super(b, alpha, beta, k, l);
		this.attackers = att;
	}
	
	
	
    /**
     * compute transition matrix in first step
     * @param n
     * @return
     */
	@Override
	public double[][] getT1(int n){
		this.setAttackProb(n);
		int[] lookup = new int[alpha];
		lookup[this.alpha-1] = b+2;
		double[][] t = new double[getIndex(lookup)][b+1];
		for (int d= 1; d <= this.b; d++){
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
	@Override
	public double[][] getT2(int n){
		this.setCDFs();
		int[] lookup = new int[alpha];
		lookup[alpha-1] = b+2;
		int index = getIndex(lookup);
		double[][] t = new double[index][index];
		t[0][0] = 1;
		int[] old = new int[this.alpha];
		constructT2(n,t,old,0);
		return t;
	}
	
	/**
	 * compute a vector of the probabilities that a node at dist d
	 * has link to target
	 * NEEDS TO BE OVERRIDEN IN CASE ltype==SPECIAL
	 * @param n
	 */
	protected void setSuccess(int n) {
		this.success = new double[this.b+1];
		this.success[0] = 0;
		if (this.ltype == LType.SIMPLE){
			//case: always resolve by constant l more steps
			int m = (int)l[0][0];
			double[] p = new double[b+1];
			p[0] = 0;
			//probability to be in fraction in id space
			double q = Math.pow(2, -m);
			for (int d=b; d > 1;d--){
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
				double q = Math.pow(2, -m-1);
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
	
	private void setAttackProb(int n){
		this.attackProb = new double[this.b+1][this.alpha];
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
				double[] ps = new double[this.alpha];
			  for (int i = 0; i < n-1; i++){
				  //prob to be successful if there are i nodes besides target in region 
                  ps = this.getAtt(i,k[d],ps);
                  for (int j = 0; j < ps.length; j++){
                	  double pa = 1;
                	  for (int c = 0; c <= j; c++){
                		  pa = pa - ps[c];
                	  }
                	  this.attackProb[d][j] = this.attackProb[d][j] +pa*binom;
                  }
				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
			  }
			  for (int j = 1; j < this.alpha; j++){
				  if (!(this.attackProb[d][j] >=0 &&  this.attackProb[d][j] <= 1)){
					  System.out.println("d="+d + " j="+j + "p="+this.attackProb[d][j]);
				  }
				  this.attackProb[d][j] = this.attackProb[d][j]/this.attackProb[d][j-1];
				  
			  }
			}
		}
		
		if (this.ltype == LType.ALL){
			//case: always resolve by constant l more steps
			for (int a = 1; a <= b; a++){
			double[] p = new double[b+1];
			p[0] = 0;
			//probability to be in fraction in id space
			double q = Math.pow(2, -a);
			for (int d=b; d > 0;d--){
				p[d] = q;
				q = q*0.5;
			}
			for (int d=b; d > 0;d--){
				if (l[d][a] > 0){
				double binom = Math.pow(1-p[d],n-2);
				double[] ps = new double[this.alpha];
			  for (int i = 0; i < n-1; i++){
				  //prob to be successful if there are i nodes besides attackers in region 
                  ps = this.getAtt(i,k[d],ps);
                  for (int j = 0; j < ps.length; j++){
                	  double pa = 1;
                	  for (int c = 0; c <= j; c++){
                		  pa = pa - ps[c];
                	  }
                	  this.attackProb[d][j] = this.attackProb[d][j] +pa*binom;
                  }
				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
			  }
			  
			  for (int j = 0; j < this.alpha; j++){
				  this.attackProb[d][j] = this.attackProb[d][j]*l[d][a];
			  }
				}
			}
			}
			for (int d = 0; d < b+1; d++){
				for (int j = 1; j < this.alpha; j++){
					  this.attackProb[d][j] = this.attackProb[d][j]/this.attackProb[d][j-1];
				}
			}
		}
	}

	private double[] getAtt(int other, int kd, double[] psold){
		double[] psnew = new double[this.alpha];
		int all = this.attackers + other;
		for (int i = 0; i < psnew.length; i++){
			if (this.attackers < i){
				psnew[i] = 0;
				continue;
			}
			if (other < kd-i){
				psnew[i] = 0;
			} else {
				if (other == kd-i){
					double init = 1;
					for (int f = 0; f < i; f++){
						init = init*(double)(this.attackers - f)/(double)(i-f);
					}
					for (int f = 0; f < kd; f++){
						init = init*(double)(kd - f)/(double)(all-f);
					}
					psnew[i] = init;
				} else {
					psnew[i] = psold[i]*other/(double)(other-kd+i)*(all-kd)/(double)all;
				}
				//System.out.println("other = " + other + " i=" + i + " p=" +psnew[i]);
			}
		}
		return psnew;
	}
	
	//CDFs
	@Override
	protected double[][] getCDFs(int d, int c) {
		if (d==0){
			int count = d*c;
			if (this.ltype == LType.SIMPLE){
				count = c;
			}
			double[][] res = new double[1][count];
			for (int i = 0; i < res[0].length; i++){
				res[0][i] = 1;
			}
		}
		switch (c) {
		case 1: return this.getCDFsOne(d); 
		case 2: return this.getCDFsTwo(d); 
		case 3: return this.getCDFsThree(d); 
		case 4: return this.getCDFsFour(d); 
		default: throw new IllegalArgumentException("Only implemented for alpha/beta <= 4");
		}
	}

	/**
	 * cdf for distance after next step for one returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsOne(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1}};
			}
		    double[][] res = new double[d-digit+1][1];
		    double p = 1;
		    res[0][0] = this.attackProb[d][0];
		    for (int i = res.length-1; i > 0; i--){
		    	res[i][0] = (1 - Math.pow(1-p, this.k[d]))*(1-this.attackProb[d][0])+res[0][0];
		    	p = p*0.5;
		    }
		    
		    return res;
		}  
//		if (this.ltype == LType.ALL){
//			double[][] res = new double[d][d];
//			for (int a = 1; a < d+1; a++){
//				if (l[d][a] > 0){
//		      double p = 1;
//		      for (int i = res.length-a; i > -1; i--){
//		    	res[i][a-1] = (1 - Math.pow(1-p, this.k[d]))*(1-this.attackProb[d][0]);
//		    	p = p*0.5;
//		      }
//		      for (int i = res.length-a+1; i < res.length; i++){
//			    	res[i][a-1] = 1;
//		     }
//			 }
//			}
//		    return res;
//		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for two returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsTwo(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1}};
			}
		    double[][] res = new double[d-digit+1][2];
		    double p = 1;
		    //probability prop to fraction of remaining ID space size
		    res[0][0] = this.attackProb[d][0];
		    res[0][1] = this.attackProb[d][1];
		    for (int i = res.length-1; i > 0; i--){
		    	res[i][0] = (1 - Math.pow(1-p, this.k[d]))*(1-this.attackProb[d][0]) +res[0][0];
		    	res[i][1] = (1 - Math.pow(1-p, this.k[d]-1))*(1-this.attackProb[d][1])+res[0][1];
		    	p = p*0.5;
		    }
		    
		    return res;
		}  
//		if (this.ltype == LType.ALL){
//			double[][] res = new double[d][2*d];
//			for (int a = 1; a < d+1; a++){
//				if (l[d][a] > 0){
//		      double p = 1;
//		      for (int i = res.length-a; i > -1; i--){
//		    	res[i][a-1] = 1 - Math.pow(1-p, this.k[d]);
//		    	res[i][d + a-1] = 1 - Math.pow(1-p, this.k[d]-1);
//		    	p = p*0.5;
//		      }
//		      for (int i = res.length-a+1; i < res.length; i++){
//		    	  res[i][d+a-1] = 1;
//			    	res[i][a-1] = 1;
//		     }
//			 }
//			}
//		    return res;
//		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for three returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsThree(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1,1}};
			}
		    double[][] res = new double[d-digit+1][3];
		    double p = 1;
		    res[0][0] = this.attackProb[d][0];
		    res[0][1] = this.attackProb[d][1];
		    res[0][2] = this.attackProb[d][2];
		    for (int i = res.length-1; i > 0; i--){
		    	double diff = Math.pow(1-p, this.k[d]-2);
		    	res[i][2] = (1 - diff)*(1-this.attackProb[d][2])+res[0][2];
		    	diff = diff*(1-p);
		    	res[i][1] = (1 - diff)*(1-this.attackProb[d][1])+res[0][1];
		    	res[i][0] = (1 - diff*(1-p))*(1-this.attackProb[d][0])+res[0][0];
		    	p = p*0.5;
		    }
		    return res;
		}  
//		if (this.ltype == LType.ALL){
//			double[][] res = new double[d][3*d];
//			for (int a = 1; a < d+1; a++){
//				if (l[d][a] > 0){
//		      double p = 1;
//		      for (int i = res.length-a; i > -1; i--){
//		    	  double diff = Math.pow(1-p, this.k[d]-2);
//			    	res[i][2*d+a-1] = 1 - diff;
//			    	diff = diff*(1-p);
//			    	res[i][d+a-1] = 1 - diff;
//			    	res[i][a-1] = 1 - diff*(1-p);
//			    	p = p*0.5;
//		      }
//		      for (int i = res.length-a+1; i < res.length; i++){
//		    	  res[i][2*d+a-1] = 1;
//			      res[i][d+a-1] = 1;
//			    	res[i][a-1] = 1;
//		     }
//			 }
//			}
//		    return res;
//		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for four returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsFour(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1,1,1}};
			}
		    double[][] res = new double[d-digit+1][4];
		    double p = 1;
		    res[0][0] = this.attackProb[d][0];
		    res[0][1] = this.attackProb[d][1];
		    res[0][2] = this.attackProb[d][2];
		    res[0][3] = this.attackProb[d][3];
		    for (int i = res.length-1; i > 0; i--){
		    	double diff = Math.pow(1-p, this.k[d]-3);
		    	res[i][3] = (1 - diff)*(1-this.attackProb[d][3])+res[0][3];
		    	diff = diff*(1-p);
		    	res[i][2] = (1 - diff)*(1-this.attackProb[d][2])+res[0][2];
		    	diff = diff*(1-p);
		    	res[i][1] = (1 - diff)*(1-this.attackProb[d][1])+res[0][1];
		    	res[i][0] = (1 - diff*(1-p))*(1-this.attackProb[d][0])+res[0][0];
		    	p = p*0.5;
		    }
		    return res;
		}  
//		if (this.ltype == LType.ALL){
//			double[][] res = new double[d][4*d];
//			for (int a = 1; a < d+1; a++){
//				if (l[d][a] > 0){
//		      double p = 1;
//		      for (int i = res.length-a; i > -1; i--){
//		    	  double diff = Math.pow(1-p, this.k[d]-3);
//		    	  res[i][3*d+a-1] = 1 - diff;
//			    	diff = diff*(1-p);
//			    	res[i][2*d+a-1] = 1 - diff;
//			    	diff = diff*(1-p);
//			    	res[i][d+a-1] = 1 - diff;
//			    	res[i][a-1] = 1 - diff*(1-p);
//			    	p = p*0.5;
//		      }
//		      for (int i = res.length-a+1; i < res.length; i++){
//		    	  res[i][3*d+a-1] = 1;
//		    	  res[i][2*d+a-1] = 1;
//			      res[i][d+a-1] = 1;
//			    	res[i][a-1] = 1;
//		     }
//			 }
//			}
//		    return res;
//		} 
		return null;
	}

}
