package attacksKad;

import kadtype.KadTypeCDFs;
import util.Binom;
import util.Calc;
import util.Hyper1;

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
		//this.setAttackProb(n);
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
			processCDFsT1(t,d,d,1-t[0][d]);
				}
			}
		}
		return t;
	}
	
    /**
     * transition matrix for all further steps
     * override: attacker coordinate
     * @param n
     * @return
     */
	@Override
	public double[][] getT2(int n){
		int[] lookup = new int[alpha];
		//one more state
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
		this.attackProb = new double[this.b+2][this.alpha+2];
		this.attackProb[0][this.alpha] = 1;
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
				if (d > 1)
				q = q*0.5;
			}
			for (int d=b+1; d > 0;d--){
				//start with the expected value, most stable of B(n-2,p[d])
				int exp = (int)((n-1)*p[d]);
				//attack probability: iteratively computed
				Hyper1[] hyper = new Hyper1[this.attackProb[d].length-2];
				double[] attProb = new double[hyper.length];
				for (int j = 0; j < hyper.length; j++){
					hyper[j] = new Hyper1(j,this.attackers,k[d-1],exp);
					attProb[j] = hyper[j].getNext();
				}
				//success probability: iteratively computed
				Binom bi = new Binom(n-2,p[d],exp);
				//binom: (n-2 choose i) : nodes in region
				double binom = bi.getNext();
			  for (int i = this.attackers+exp; i < this.attackers+n-1; i++){
				  if (i < k[d-1]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d-1])/(double)(i+1);
				  }
				  //j = contacted attackers
				  for (int j = 0; j < this.attackProb[d].length-2; j++){
					  if (k[d-1] <= i+1){
//					      +(attackers choose j)*(normal node choose k-j)/(all choose k)*binom;
					      this.attackProb[d][j] = this.attackProb[d][j] + attProb[j]*binom;
					  }
					  attProb[j] = hyper[j].getNext();
				  }
				  binom = bi.getNext();
			  }
			  //go from expected value to 0
			  bi.recompute(exp);
			  binom = bi.getBefore();
			  for (int j = 0; j < hyper.length; j++){
					hyper[j].recompute(exp);
					attProb[j] = hyper[j].getBefore();
				}
			  for (int i = this.attackers+exp-1; i > this.attackers-1; i--){
				  if (i < k[d-1]){
				         this.success[d] = this.success[d] + binom;
				  } else{
						 this.success[d] = this.success[d] + binom*(double)(k[d-1])/(double)(i+1);
				  }
				  for (int j = 0; j < this.attackProb[d].length-2; j++){
					  if (k[d-1] <= i+1){
//					      this.attackProb[d][j] = this.attackProb[d][j] + Calc.binom(this.attackers, j)*Calc.binom(i-this.attackers, k[d-1]-j)
//					    		  /(double)Calc.binom(i+1, k[d-1])*binom;
					      this.attackProb[d][j] = this.attackProb[d][j] + attProb[j]*binom;
					  }
					  attProb[j] = hyper[j].getBefore();
				  }
				  binom = bi.getBefore();
			  }
			  //set prob to have at least alpha attackers in bucket => all new contacts attackers for T_1
			  this.attackProb[d][this.alpha] = 1-this.success[d];
			  for (int j = 0; j < this.alpha; j++){
				  this.attackProb[d][this.alpha] = this.attackProb[d][this.alpha] - this.attackProb[d][j];
			  }
			//set prob to have at least beta attackers in bucket => all new contacts attackers for T_2
			  this.attackProb[d][this.alpha+1] = 1-this.success[d];
			  for (int j = 0; j < this.beta; j++){
				  this.attackProb[d][this.alpha+1] = this.attackProb[d][this.alpha+1] - this.attackProb[d][j];
			  }
			}
		}
//		if (this.ltype == LType.ALL){
//			//case: variable l: iterate over all l
//			for (int m = 0; m < l.length; m++){
//				double[] p = new double[b+1];
//				p[0] = 0;
//				double q = Math.pow(2, -m-1);
//				for (int d=b; d > 0;d--){
//					p[d] = q;
//					q = q*0.5;
//				}
//				for (int d=b; d > 0;d--){
//					double binom = Math.pow(1-p[d],n-2);
//				  for (int i = 0; i < n-1; i++){
//					  if (i < k[d]){
//					         this.success[d] = this.success[d] + l[d][m]*binom;
//					  } else{
//							 this.success[d] = this.success[d] + l[d][m]*binom*(double)(k[d])/(double)(i+1);
//					  }
//					  binom = binom*(n-2-i)/(double)(i+1)*p[d]/(1-p[d]);
//				  }
//				}
//			}
//		}
	}
	

	
	 /**
	   * add probability for attack in first step
	   */
	  protected double getProb(int[] returned, int d, int l){
		  //case a: no attackers: return P(no attackers)*P(returned in old model) 
		  if (returned[0] > 0){
			  int[] re = new int[returned.length];
			  for (int j = 0; j < returned.length; j++){
				  re[j] = returned[j]-1;
				
			 }
            return this.attackProb[d+1][0]*super.getProb(re, d,l);
		  } else {
			  //count attacker
			  int count = 1;
			  for (int j = 1; j < returned.length; j++){
				  if (returned[j] == 0){
					  count++;
				  }else{
					  break;
				  }
			  }
			  //all returned contacts attackers
			  if (count == returned.length){
                return this.attackProb[d+1][this.alpha];
				}else { //otherwise: prob(count att)P(other in original model)
				  int[] re = new int[returned.length-count];
				  for (int j = count; j < returned.length; j++){
					  re[j-count] = returned[j]-1;
				  }
				  //set k[d] to remaining contacts
				  this.k[d] = this.k[d]-count;
				  double p = this.attackProb[d+1][count]*super.getProb(re, d,l);
				  this.k[d] = this.k[d]+count;
				  return p;
			 }
		 }
	  }
	

	


}
