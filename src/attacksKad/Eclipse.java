package attacksKad;

import kadtype.KadType.LType;
import kadtype.KadTypeCDFs;

public abstract class Eclipse extends KadTypeCDFs{
	protected int attackers;
	double[][][] attackProb;
	
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
		for (int d= 0; d <= this.b; d++){
			t[0][d] = this.success[d+1];
			//distributions over the other distances
			if (t[0][d] < 1){
				if (d == 0){
					t[1][d] = 1 - this.success[d+1];
				} else{
			//compute other entries of t_1
			processCDFsT1(t,d,d);
				}
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
	    * increase index to b+1
	    */
	  protected void constructT2(int n, double[][] t2, int[] old, int tofill){
		  if (tofill < this.alpha){
			  //set next entry
		  int start=(tofill==0?0:old[tofill-1]);
		     for (int i = start; i <= this.b+1; i++){
			   old[tofill] = i;
			   this.constructT2(n, t2, old, tofill+1);
		     }
		  } else {
			  //compute possibilities for next states
			  //prob to be successful in next step
				int oldindex = this.getIndex(old);
			 double nsucc = 1;
			 for (int i = 0; i < old.length; i++){
				 nsucc = nsucc*(1-this.success[old[i]]);
			 }
			 
			 t2[0][oldindex] = 1-nsucc;
			 //not successful => system-specific
			 if (nsucc > 0)
			 this.processCDFsT2(n, t2, old, oldindex,nsucc);
		  }
	  }
	/**
	 *include attackers 
	 */
	protected void setSuccess(int n) {
		this.success = new double[this.b+2];
		this.success[0] = 0;
		if (this.ltype == LType.SIMPLE){
			//case: always resolve by constant l more steps
			int m = (int)l[0][0];
			double[] p = new double[b+2];
			p[0] = 0;
			//probability to be in fraction in id space
			double q = Math.pow(2, -m);
			for (int d=b+1; d > 0;d--){
				p[d] = q;
				q = q*0.5;
			}
			for (int d=b+1; d > 0;d--){
				double binom = Math.pow(1-p[d],n-2);
			  for (int i = this.attackers; i < this.attackers+n-1; i++){
				  //prob to be successful if there are i nodes besides target in region 
				  if (i < k[d-1]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d-1])/(double)(i+1);
				  }
				  binom = binom*(n-2-(i-this.attackers))/(double)(i-this.attackers+1)*p[d]/(1-p[d]);
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
		this.attackProb = new double[this.b+2][this.alpha][this.alpha*this.beta];
		for (int i = 0; i < this.alpha; i++){
			for (int j= 0; j < this.alpha; j++){
				this.attackProb[0][i][j] = 1;
			}
		}
		if (this.ltype == LType.SIMPLE){
			//case: always resolve by constant l more steps
			int m = (int)l[0][0];
			double[] p = new double[b+2];
			p[0] = 0;
			//probability to be in fraction in id space
			double q = Math.pow(2, -m);
			for (int d=b+1; d > 0;d--){
				p[d] = q;
				q = q*0.5;
			}
			for (int d=b+1; d > 0;d--){
				double binom = Math.pow(1-p[d],n-2);
				double[][] ps = new double[this.alpha*this.beta][this.alpha];
			  for (int i = 0; i < n-1; i++){
				  //prob to be successful if there are i nodes besides target in region 
				  for (int at = 0; at < this.alpha*this.beta; at++){
					  if (this.attackers - at < 1){
						  for (int j = 0; j < this.alpha; j++){
						    this.attackProb[d][j][at] = 0;
						  }
					  } else {
                  ps[at] = this.getAtt(i,k[d-1],ps[at],this.attackers-at);
                  for (int j = 0; j < ps[at].length; j++){
                	  double pa = 1;
                	  for (int c = 0; c <= j; c++){
                		  pa = pa - ps[at][c];
                	  }
                	  this.attackProb[d][j][at] = this.attackProb[d][j][at] +pa*binom;
                  }
				  }
				  }
				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
			  }
			  
//			  for (int j = 1; j < this.alpha; j++){
//				  for (int at = 0; at < this.alpha; at++){
//				  this.attackProb[d][j][at] = this.attackProb[d][j][at]/this.attackProb[d][j-1][at];
//				  }
//				  
//			  }
			}
		}
		
//		if (this.ltype == LType.ALL){
//			//case: always resolve by constant l more steps
//			for (int a = 1; a <= b; a++){
//			double[] p = new double[b+1];
//			p[0] = 0;
//			//probability to be in fraction in id space
//			double q = Math.pow(2, -a);
//			for (int d=b; d > 0;d--){
//				p[d] = q;
//				q = q*0.5;
//			}
//			for (int d=b; d > 0;d--){
//				if (l[d][a] > 0){
//				double binom = Math.pow(1-p[d],n-2);
//				double[] ps = new double[this.alpha];
//			  for (int i = 0; i < n-1; i++){
//				  //prob to be successful if there are i nodes besides attackers in region 
//				  
//                  ps = this.getAtt(i,k[d],ps);
//                  for (int j = 0; j < ps.length; j++){
//                	  double pa = 1;
//                	  for (int c = 0; c <= j; c++){
//                		  pa = pa - ps[c];
//                	  }
//                	  this.attackProb[d][j] = this.attackProb[d][j] +pa*binom;
//                  }
//				  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
//			  }
//			  
//			  for (int j = 0; j < this.alpha; j++){
//				  this.attackProb[d][j] = this.attackProb[d][j]*l[d][a];
//			  }
//				}
//			}
//			}
//			for (int d = 0; d < b+1; d++){
//				for (int j = 1; j < this.alpha; j++){
//					  this.attackProb[d][j] = this.attackProb[d][j]/this.attackProb[d][j-1];
//				}
//			}
//		}
	}
	
	 /**
	   * add probability for attack in first step
	   */
	  protected double getProb(int[] returned, int d, int l){
		  if (returned[0] > 0){
			  int[] re = new int[returned.length];
			  for (int j = 0; j < returned.length; j++){
				  re[j] = returned[j]-1;
				
			 }
//			  System.out.println("d="+ d + "ret=[" + returned[0]+ ","+returned[1]+ ","+returned[2]+"]" 
//			 + " attack: " + (1-this.attackProb[d][0][0]) + " normal: " + this.getProb(re, cdf) + " case: not");
			  return (1-this.attackProb[d][0][0])*super.getProb(re, d,l);
		  } else {
			  int count = 1;
			  for (int j = 1; j < returned.length; j++){
				  if (returned[j] == 0){
					  count++;
				  }else{
					  break;
				  }
			  }
			  if (count > this.attackers){
				  return 0;
			  }
			  if (count == returned.length){
//				  System.out.println("d="+ d + "ret=[" + returned[0]+ ","+returned[1]+ ","+returned[2]+"]" 
//							 + " attack: " + this.attackProb[d][this.alpha-1][0] + " normal: " + 1 + " case: all" );
				  return this.attackProb[d][this.alpha-1][0];
				  
			  }else {
				  int[] re = new int[this.alpha-count];
				  for (int j = count; j < this.alpha; j++){
					  re[j-count] = returned[j]-1;
				  }
//				  System.out.println("d="+ d + "ret=[" + returned[0]+ ","+returned[1]+ ","+returned[2]+"]" 
//							 + " attack: " + this.attackProb[d][count-1][0]*(1-this.attackProb[d][count][0]/this.attackProb[d][count-1][0]) 
//							 + " attackPart: " + this.attackProb[d][count][0] 
//							 + " normal: " + this.getProb(re, cdf) + " case: part");
				  return (this.attackProb[d][count-1][0]-this.attackProb[d][count][0])*super.getProb(re, d,l);
			 }
		 }
	  }
	
	protected double getProb(int[] returned, int d, int l, int c){
		if (d == -1){
			if (returned[1] == 0){
				return 1;
			} else {
				return 0;
			}
		}
		if (returned[0] > 0){
			int[] re = new int[returned.length];
			for (int j = 0; j < returned.length; j++){
				  re[j] = returned[j]-1;
			}
//			System.out.println("d= "+d+" re0= "+(returned[0]) + " re1= "+(returned[1])
//					+ " attack: "+(1-this.attackProb[d][0][c]) + " normal "+this.getProb(re, nr));
			
			return (1-this.attackProb[d][0][c])*super.getProb(re, d,l);
		} else{
			int count = 1;
			  for (int j = 1; j < returned.length; j++){
				  if (returned[j] == 0){
					  count++;
				  }else{
					  break;
				  }
			  }
			  if (count + c > this.attackers){
				  return 0;
			  }
			  if (count == returned.length){
				  //System.out.println("d= "+d+" re0= "+returned[0] + " re1= "+returned[1] + " attack: "+this.attackProb[d][count-1][c] + " normal "+1);
				  return this.attackProb[d][count-1][c];
			  }else {
				  int[] re = new int[returned.length-count];
				  for (int j = count; j < returned.length; j++){
					  re[j-count] = returned[j]-1;
				  }
//				  System.out.println("d= "+d+" re0= "+returned[0] + " re1= "+returned[1] + " attack: "+this.attackProb[d][0][c]*(1-this.attackProb[d][count][c]/this.attackProb[d][count-1][c])
//						  + " normal "+this.getProb(re, nr));
				   return (this.attackProb[d][count-1][c]-this.attackProb[d][count][c])*super.getProb(re, d,l);
				  
			  }
		}
	}

	private double[] getAtt(int other, int kd, double[] psold, int curAtt){
		double[] psnew = new double[this.alpha];
		int all = curAtt + other;
		for (int i = 0; i < psnew.length; i++){
			if (curAtt < i){
				psnew[i] = 0;
				continue;
			}
			if (other < kd-i){
				if (curAtt == i){
					psnew[i] = 1;
					for (int j = 0; j < i; j++){
						psnew[i] = psnew[i] - psnew[j];
					}
				} else {
				   psnew[i] = 0;
				}
			} else {
				if (other == kd-i){
					double init = 1;
					for (int f = 0; f < i; f++){
						init = init*(double)(curAtt - f)/(double)(i-f);
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
	


}
