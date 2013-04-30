package attacksKad;

import kadtype.KadType.LType;
import kadtype.KadTypeCDFs;

public class FailureAlpha3Beta2Upper extends Failure{
	
	public FailureAlpha3Beta2Upper(int b, int[] k, double[][] l,
			LType ltype, double fprob) {
		super(b, 3, 2, k, l, ltype,fprob);
		
	}
	
	public FailureAlpha3Beta2Upper(int b, int[] k, int l, double fprob) {
		super(b, 3, 2, k, l,fprob);
	}
	
	public FailureAlpha3Beta2Upper(int b, int k, double[][] l,
			LType ltype, double fprob) {
		super(b, 3, 2, k, l, ltype,fprob);
	}
	
	public FailureAlpha3Beta2Upper(int b, int k, int l, double fprob) {
		super(b, 3, 2, k, l,fprob);
	}

	@Override
	protected void processCDFsT1(double[][] cdfs, double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i < cdfs.length; i++){
			for (int j = i; j < cdfs.length; j++){
				for (int k = j; k < cdfs.length; k++){
					
					int[] re = new int[]{i,j,k};
					int indexnew = this.getIndex(re);
					if (this.ltype == LType.SIMPLE){
					   t[indexnew][indexOld] =  t[indexnew][indexOld] 
							+(1-this.success[mindist])*this.getProb(re, cdfs);
					}   
					if (this.ltype == LType.ALL){
						for (int a = 1; a < mindist+1; a++){
							if (this.l[mindist][a] > 0){
								t[indexnew][indexOld] =  t[indexnew][indexOld] 
										+(1-this.success[mindist])*this.getProb(re, cdfs,a)*this.l[mindist][a];
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
				   double p1 = i1<this.cdfs[old[0]].length?this.getProb(returned[0], old[0])*(1-this.fprob):this.fprob;
				for (int j1 = 0; j1 < this.cdfs[old[1]].length+1; j1++){
					returned[1][0] = j1<this.cdfs[old[1]].length?j1:old[2];;
					for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-1,j1); j2++){
						returned[1][1] = j1<this.cdfs[old[1]].length?j2:old[2];
						double p2 = j1<this.cdfs[old[1]].length?this.getProb(returned[1], old[1])*(1-this.fprob):this.fprob;
						for (int k1 = 0; k1 < this.cdfs[old[2]].length+1; k1++){
							returned[2][0] = k1<this.cdfs[old[2]].length?k1:old[2];;
							for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-1,k1); k2++){
								returned[2][1] = k1<this.cdfs[old[2]].length?k2:old[2];
								double p3 = k1<this.cdfs[old[2]].length?this.getProb(returned[2], old[2])*(1-this.fprob):this.fprob;
								//consider all possibilities of stale entries
//								double add = nsucc*p1*p2*p3;
//								double[] succfail = {1-this.fprob, fprob};
//								int[] res2 = returned[1];
//								int[] res3 = returned[2];
//								for (int s1 = 0; s1 < 2; s1++){
//									if (s1 == 1){
//										returned[0] = new int[]{old[0], old[0]};
//									}
//								    for (int s2 = 0; s2 < 2; s2++){
//								    	if (s2 == 1){
//								    	returned[1] = new int[]{old[1], old[1]};
//								    	}	
//								    	for (int s3 = 0; s3 < 2; s3++){
//									    	if (s3 == 1){
//									    	returned[2] = new int[]{old[2], old[2]};
//									    	}	
									    	int[] next = this.topAlpha(returned);
											int newindex = this.getIndex(next);
											t2[newindex][oldindex] = t2[newindex][oldindex] +
													nsucc*p1*p2*p3;
									    	
//									    }
//								    	returned[1] = res2;
//								    }
//								}
								
								
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
					returned[0][0] = i1<this.cdfs[old[0]].length?i1:old[2];
					for (int i2 = i1; i2 <= Math.max(this.cdfs[old[0]].length-1,i1); i2++){
						returned[0][1] = i1<this.cdfs[old[0]].length?i2:old[2];
							   double p1 = i1<this.cdfs[old[0]].length?
									   this.getProb(returned[0], old[0],a1)*l[old[0]][a1]:this.fprob*l[old[0]][a1];
							   for (int a2= 1; a2 <= old[1]; a2++){
								   if (l[old[1]][a2] == 0){
									   continue;
								   }
								   for (int j1 = 0; j1 < this.cdfs[old[1]].length+1; j1++){
										returned[1][0] = j1<this.cdfs[old[1]].length?j1:old[2];;
										for (int j2 = j1; j2 <= Math.max(this.cdfs[old[1]].length-1,j1); j2++){
											returned[1][1] = j1<this.cdfs[old[1]].length?j2:old[2];
									double p2 = j1<this.cdfs[old[1]].length?this.getProb(returned[1], old[1],a2)*l[old[1]][a2]:this.fprob*l[old[1]][a2];
									for (int a3 = 1; a3 <= old[2]; a3++){
										if (l[old[2]][a3] == 0){
											continue;
										}
										for (int k1 = 0; k1 < this.cdfs[old[2]].length+1; k1++){
											returned[2][0] = k1<this.cdfs[old[2]].length?k1:old[2];;
											for (int k2 = k1; k2 <= Math.max(this.cdfs[old[2]].length-1,k1); k2++){
												returned[2][1] = k1<this.cdfs[old[2]].length?k2:old[2];
											double p3 = k1<this.cdfs[old[2]].length?this.getProb(returned[2], old[2],a3)*l[old[2]][a3]:this.fprob*l[old[2]][a3];
											int[] next = this.topAlpha(returned);
											int newindex = this.getIndex(next);
											t2[newindex][oldindex] = t2[newindex][oldindex] +
													nsucc*p1*p2*p3;
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