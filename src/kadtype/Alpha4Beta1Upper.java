package kadtype;

import kadtype.KadType.LType;

public class Alpha4Beta1Upper extends KadTypeCDFs {
	
	public Alpha4Beta1Upper(int b, int[] k, double[][] l,
			LType ltype) {
		super(b, 4, 1, k, l, ltype);
	}
	
	public Alpha4Beta1Upper(int b, int[] k, int l) {
		super(b, 4, 1, k, l);
	}
	
	public Alpha4Beta1Upper(int b, int k, double[][] l,
			LType ltype) {
		super(b, 4, 1, k, l, ltype);
	}
	
	public Alpha4Beta1Upper(int b, int k, int l) {
		super(b, 4, 1, k, l);
	}

	@Override
	protected void processCDFsT1(double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i < cdfs[mindist].length; i++){
			for (int j = i; j < cdfs[mindist].length; j++){
				for (int k = j; k < cdfs[mindist].length; k++){
					for (int m = k; m < cdfs[mindist].length; m++){
					int[] re = new int[]{i,j,k,m};
					int indexnew = this.getIndex(re);
					if (this.ltype == LType.SIMPLE){
					   t[indexnew][indexOld] =  t[indexnew][indexOld] 
							+(1-this.success[mindist])*this.getProb(re, mindist,0);
					}   
					if (this.ltype == LType.ALL){
						for (int a = 1; a < mindist+1; a++){
							if (this.l[mindist][a] > 0 && m <= this.cdfs[mindist].length-a){
								t[indexnew][indexOld] =  t[indexnew][indexOld] 
										+(1-this.success[mindist])*this.getProb(re, mindist,a-1)*this.l[mindist][a];
							}
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
		int[][] returned = new int[4][1];
		if (this.ltype == LType.SIMPLE) {
			for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++) {
				returned[0][0] = i1;
				double p1 = this.getProb(returned[0], old[0],0);
				for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++) {
					returned[1][0] = j1;
					double p2 = this.getProb(returned[1], old[1],0);
					for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++) {
						returned[2][0] = k1;
						double p3 = this.getProb(returned[2], old[2],0);
						for (int m1 = 0; m1 < this.cdfs[old[3]].length; m1++) {
							returned[3][0] = m1;
							double p4 = this.getProb(returned[3], old[3],0);
							int[] next = this.topAlpha(returned);
							int newindex = this.getIndex(next);
							t2[newindex][oldindex] = t2[newindex][oldindex]
									+ nsucc * p1 * p2 * p3 * p4;
						}
					}
				}
			}
		}
		if (this.ltype == LType.ALL) {
			for (int a1 = 1; a1 <= old[0]; a1++) {
				if (l[old[0]][a1] == 0) {
					continue;
				}
				for (int i1 = 0; i1 < this.cdfs[old[0]].length-a1+1; i1++) {
					returned[0][0] = i1;
					double p1 = this.getProb(returned[0], old[0],a1-1)
							* l[old[0]][a1];
					for (int a2 = 1; a2 <= old[1]; a2++) {
						if (l[old[1]][a2] == 0) {
							continue;
						}
						for (int j1 = 0; j1 < this.cdfs[old[1]].length-a2+1; j1++) {
							returned[1][0] = j1;
							double p2 = this.getProb(returned[1], old[1],a2-1)
									* l[old[1]][a2];
							for (int a3 = 1; a3 <= old[2]; a3++) {
								if (l[old[2]][a3] == 0) {
									continue;
								}
								for (int k1 = 0; k1 < this.cdfs[old[2]].length-a3+1; k1++) {
									returned[2][0] = k1;
									double p3 = this.getProb(returned[2],
											old[2],a3-1) * l[old[2]][a3];
									for (int a4 = 1; a4 <= old[3]; a4++) {
										if (l[old[2]][a4] == 0) {
											continue;
										}
										for (int m1 = 0; m1 < this.cdfs[old[3]].length-a4+1; m1++) {
											returned[3][0] = m1;
											double p4 = this.getProb(
													returned[3], old[3],a4-1)
													* l[old[3]][a4];
											int[] next = this
													.topAlpha(returned);
											int newindex = this.getIndex(next);
											t2[newindex][oldindex] = t2[newindex][oldindex]
													+ nsucc * p1 * p2 * p3 * p4;
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
