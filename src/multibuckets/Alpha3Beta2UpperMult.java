package multibuckets;

import kadtype.KadType.LType;
import kadtype.KadTypeUpper2;

public class Alpha3Beta2UpperMult extends KadTypeUpper2{
	
	public Alpha3Beta2UpperMult(int b, int[] k, double[][] l,
			LType ltype) {
		super(b, 3, 2, k, l, ltype);
	}
	
	public Alpha3Beta2UpperMult(int b, int[] k, int l) {
		super(b, 3, 2, k, l);
	}
	
	public Alpha3Beta2UpperMult(int b, int k, double[][] l,
			LType ltype) {
		super(b, 3, 2, k, l, ltype);
	}
	
	public Alpha3Beta2UpperMult(int b, int k, int l) {
		super(b, 3, 2, k, l);
	}
	
//	public Alpha3Beta2Upper2(int b, int k, double[][] l,
//			LType ltype, int replicas) {
//		super(b, 3, 2, k, l, ltype, replicas);
//	}

	@Override
	protected void processCDFsT1(double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i <= mindist; i++){
			for (int j = i; j <= mindist; j++){
				for (int k = j; k <= mindist; k++){
					
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
			//first lookup
		for (int i1 = 0; i1 <= old[0]; i1++){
			returned[0][0] = i1;
			for (int i2 = i1; i2 <= old[0] ; i2++){
				returned[0][1] = i2;
				   double p1 = this.getProb(returned[0], old[0],0);
				   //second lookup
				for (int j1 = 0; j1 <= old[1]; j1++){
					returned[1][0] = j1;
					for (int j2 = j1; j2 <= old[1]; j2++){
						returned[1][1] = j2;
						double p2 = this.getProb(returned[1], old[1],0);
						//third lookup
						for (int k1 = 0; k1 <= old[2]; k1++){
							returned[2][0] = k1;
							for (int k2 = k1; k2 <= old[2]; k2++){
								returned[2][1] = k2;
								double p3 = this.getProb(returned[2], old[2],0);
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
				for (int i1 = 0; i1 <= old[0]; i1++){
						returned[0][0] = i1;
						for (int i2 = i1; i2 <= old[0]; i2++){
							returned[0][1] = i2;
							   double p1 = this.getProb(returned[0], old[0],a1-1)*l[old[0]][a1];
							   for (int a2= 1; a2 <= old[1]; a2++){
								   if (l[old[1]][a2] == 0){
									   continue;
								   }
							for (int j1 = 0; j1 <=old[1]; j1++){
								returned[1][0] = j1;
								for (int j2 = j1; j2 <=old[1]; j2++){
									returned[1][1] = j2;
									double p2 = this.getProb(returned[1], old[1],a2-1)*l[old[1]][a2];
									for (int a3 = 1; a3 <= old[2]; a3++){
										if (l[old[2]][a3] == 0){
											continue;
										}
										for (int k1 = 0; k1 <= old[2]; k1++){
										returned[2][0] = k1;
										for (int k2 = k1; k2 <= old[2]; k2++){
											returned[2][1] = k2;
											double p3 = this.getProb(returned[2], old[2],a3-1)*l[old[2]][a3];;
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
