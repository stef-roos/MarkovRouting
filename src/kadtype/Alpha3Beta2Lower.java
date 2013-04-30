package kadtype;

import kadtype.KadType.LType;

public class Alpha3Beta2Lower extends KadTypeLower{
	
	public Alpha3Beta2Lower(int b, int[] k, double[][] l,
			LType ltype) {
		super(b, 3, 2, k, l, ltype);
	}
	
	public Alpha3Beta2Lower(int b, int[] k, int l) {
		super(b, 3, 2, k, l);
	}
	
	public Alpha3Beta2Lower(int b, int k, double[][] l,
			LType ltype) {
		super(b, 3, 2, k, l, ltype);
	}
	
	public Alpha3Beta2Lower(int b, int k, int l) {
		super(b, 3, 2, k, l);
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
		for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++){
			returned[0][0] = i1;
			for (int i2 = i1; i2 < this.cdfs[old[0]].length; i2++){
				returned[0][1] = i2;
				   double p1 = this.getProb(returned[0], old[0]);
				for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++){
					returned[1][0] = j1;
					for (int j2 = j1; j2 < this.cdfs[old[1]].length; j2++){
						returned[1][1] = j2;
						double p2 = this.getProb(returned[1], old[1]);
						for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++){
							returned[2][0] = k1;
							for (int k2 = k1; k2 < this.cdfs[old[2]].length; k2++){
								returned[2][1] = k2;
								double p3 = this.getProb(returned[2], old[2]);
								this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3);
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
				for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++){
						returned[0][0] = i1;
						for (int i2 = i1; i2 < this.cdfs[old[0]].length; i2++){
							returned[0][1] = i2;
							   double p1 = this.getProb(returned[0], old[0],a1)*l[old[0]][a1];
							   for (int a2= 1; a2 <= old[1]; a2++){
								   if (l[old[1]][a2] == 0){
										continue;
									}
							for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++){
								returned[1][0] = j1;
								for (int j2 = j1; j2 < this.cdfs[old[1]].length; j2++){
									returned[1][1] = j2;
									double p2 = this.getProb(returned[1], old[1],a2)*l[old[1]][a2];
									for (int a3 = 1; a3 <= old[2]; a3++){
										if (l[old[2]][a3] == 0){
											continue;
										}
									for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++){
										returned[2][0] = k1;
										for (int k2 = k1; k2 < this.cdfs[old[2]].length; k2++){
											returned[2][1] = k2;
											double p3 = this.getProb(returned[2], old[2],a3)*l[old[2]][a3];;
											this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3);
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