package attacksKad;

import kadtype.KadType.LType;
import kadtype.KadTypeUpper2;

public class FailureAlpha3Beta2Upper extends KadTypeUpper2{
	double fprob;
	
	public FailureAlpha3Beta2Upper(int b, int[] k, double[][] l,
			LType ltype, double fprob) {
		super(b, 3, 2, k, l, ltype);
		this.fprob = fprob;
		
	}
	
	public FailureAlpha3Beta2Upper(int b, int[] k, int l, double fprob) {
		super(b, 3, 2, k, l);
		this.fprob = fprob;
	}
	
	public FailureAlpha3Beta2Upper(int b, int k, double[][] l,
			LType ltype, double fprob) {
		super(b, 3, 2, k, l, ltype);
		this.fprob = fprob;
	}
	
	public FailureAlpha3Beta2Upper(int b, int k, int l, double fprob) {
		super(b, 3, 2, k, l);
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
							if (this.l[mindist][a] > 0 && k <= mindist - a){
								double p = this.getProb(re, mindist,a-1);
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
			returned[0][0] = i1<this.cdfs[old[0]].length?i1:old[2];
			for (int i2 = i1; i2 <= Math.max(this.cdfs[old[0]].length-1,i1); i2++){
				returned[0][1] = i1<this.cdfs[old[0]].length?i2:old[2];;
				   double p1 = i1<this.cdfs[old[0]].length?this.getProb(returned[0], old[0],0)*(1-this.fprob):this.fprob;
				for (int j1 = 0; j1 < this.cdfs[old[1]].length+1; j1++){
					returned[1][0] = j1<this.cdfs[old[1]].length?j1:old[2];;
					for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-1,j1); j2++){
						returned[1][1] = j1<this.cdfs[old[1]].length?j2:old[2];
						double p2 = j1<this.cdfs[old[1]].length?this.getProb(returned[1], old[1],0)*(1-this.fprob):this.fprob;
						for (int k1 = 0; k1 < this.cdfs[old[2]].length+1; k1++){
							returned[2][0] = k1<this.cdfs[old[2]].length?k1:old[2];;
							for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-1,k1); k2++){
								returned[2][1] = k1<this.cdfs[old[2]].length?k2:old[2];
								double p3 = k1<this.cdfs[old[2]].length?this.getProb(returned[2], old[2],0)*(1-this.fprob):this.fprob;
								this.makeDistinct(returned, t2, oldindex, old[2], n, nsucc*p1*p2*p3);
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
				for (int i1 = 0; i1 < this.cdfs[old[0]].length-a1+2; i1++){
					returned[0][0] = i1<this.cdfs[old[0]].length-a1+1?i1:old[2];
					for (int i2 = i1; i2 <= Math.max(this.cdfs[old[0]].length-a1,i1); i2++){
						returned[0][1] = i1<this.cdfs[old[0]].length-a1+1?i2:old[2];
						 double p1 = i1<this.cdfs[old[0]].length-a1+1?
									   this.getProb(returned[0], old[0],a1-1)*(1-this.fprob):this.fprob;
									   p1 = p1*l[old[0]][a1]; 
									   
							   for (int a2= 1; a2 <= old[1]; a2++){
								   if (l[old[1]][a2] == 0){
									   continue;
								   }
								   for (int j1 = 0; j1 < this.cdfs[old[1]].length-a2+2; j1++){
										returned[1][0] = j1<this.cdfs[old[1]].length-a2+1?j1:old[2];;
										for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-a2,j1); j2++){
											returned[1][1] = j1<this.cdfs[old[1]].length-a2+1?j2:old[2];
									double p2 = j1<this.cdfs[old[1]].length-a2+1?
											this.getProb(returned[1], old[1],a2-1)*(1-this.fprob):this.fprob;
									p2 = p2 * l[old[1]][a2];
									for (int a3 = 1; a3 <= old[2]; a3++){
										if (l[old[2]][a3] == 0){
											continue;
										}
										for (int k1 = 0; k1 < this.cdfs[old[2]].length-a3+2; k1++){
											returned[2][0] = k1<this.cdfs[old[2]].length-a3+1?k1:old[2];;
											for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-a3,k1); k2++){
												returned[2][1] = k1<this.cdfs[old[2]].length-a3+1?k2:old[2];
											double p3 = k1<this.cdfs[old[2]].length-a3+1?this.getProb(returned[2], old[2],a3-1)
													*(1-this.fprob):this.fprob;
											p3 = p3*l[old[2]][a3];	
											this.makeDistinct(returned, t2, oldindex, old[2], n, nsucc*p1*p2*p3);
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

}
