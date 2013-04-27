package attacksKad;

import kadtype.KadType.LType;
import kadtype.KadTypeLower2;

public abstract class Failure extends KadTypeLower2 {
	protected double fprob;
	
	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 * @param ltype
	 */
	public Failure(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype, double fprob) {
		super(b, alpha, beta, k, l, ltype);
		this.fprob = fprob;
	}
	
	public Failure(int b, int alpha, int beta, int k, double[][] l,
			LType ltype, double fprob) {
		super(b, alpha, beta, k, l, ltype);
		this.fprob = fprob;
	}
	
	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 */
	public Failure(int b, int alpha, int beta, int[] k, int l, double fprob) {
		super(b, alpha, beta, k, l);
		this.fprob = fprob;
	}
	
	public Failure(int b, int alpha, int beta, int k, int l, double fprob) {
		super(b, alpha, beta, k, l);
		this.fprob = fprob;
	}

	 /**
	    * construct the transition matrix for the second step
	    * @param n: #nodes
	    * @param t2: transition matrix
	    */
	  protected void constructT2(int n, double[][] t2, int[] old, int tofill){
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
				 nsucc = nsucc*(1-this.success[old[i]]*(1-this.fprob));
			 }
			 int oldindex = this.getIndex(old);
			 t2[0][oldindex] = 1-nsucc;
			 //not successful => system-specific
			 if (nsucc > 0)
			 this.processCDFsT2(n, t2, old, oldindex,nsucc);
		  }
	  }

}
