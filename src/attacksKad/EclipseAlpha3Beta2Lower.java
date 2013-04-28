package attacksKad;

import kadtype.KadType.LType;

public class EclipseAlpha3Beta2Lower extends EclipseLower {

	public EclipseAlpha3Beta2Lower(int b, int[] k, double[][] l,
			LType ltype, int att) {
		super(b, 3, 2, k, l, ltype,att);
		
	}
	
	public EclipseAlpha3Beta2Lower(int b, int[] k, int l, int att) {
		super(b, 3, 2, k, l,att);
	}
	
	public EclipseAlpha3Beta2Lower(int b, int k, double[][] l,
			LType ltype, int att) {
		super(b, 3, 2, k, l, ltype,att);
	}
	
	public EclipseAlpha3Beta2Lower(int b, int k, int l, int att) {
		super(b, 3, 2, k, l,att);
	}

	@Override
	protected void processCDFsT1(double[][] cdfs, double[][] t, int indexOld,
			int mindist) {
		double p = 0;
		for (int i = 0; i < cdfs.length+1; i++){
			for (int j = i; j < cdfs.length+1; j++){
				for (int k = j; k < cdfs.length+1; k++){
					
					int[] re = new int[]{i,j,k};
					int indexnew = this.getIndex(re);
					if (this.ltype == LType.SIMPLE){
//						if (i == 0 && j == 0){
//						   p = p + this.getProb(re, cdfs,mindist);
//						}
					   t[indexnew][indexOld] =  t[indexnew][indexOld] 
							+(1-this.success[mindist+1])*this.getProb(re, cdfs,mindist);
					}   
//					if (this.ltype == LType.ALL){
//						for (int a = 1; a < mindist+1; a++){
//							if (this.l[mindist][a] > 0){
//								t[indexnew][indexOld] =  t[indexnew][indexOld] 
//										+(1-this.success[mindist])*this.getProb(re, cdfs,a)*this.l[mindist][a];
//							}
//						}
//					}
				}
			}
		}
		
	}

	@Override
	protected void processCDFsT2(int n, double[][] t2, int[] old, int oldindex,
			double nsucc) {
		int[][] returned = new int[3][2];
		if (this.ltype == LType.SIMPLE){
			int max1 = old[0] == 0?1:this.cdfs[old[0]-1].length+1;
			int max2 = old[1] == 0?1:this.cdfs[old[1]-1].length+1;
			int max3 = old[2] == 0?1:this.cdfs[old[2]-1].length+1;	
		for (int i1 = 0; i1 < max1; i1++){
			returned[0][0] = i1;
			for (int i2 = i1; i2 < max1; i2++){
				returned[0][1] = i2;
				   double p1 = this.getProb(returned[0], old[0]-1,old[0],0);
				for (int j1 = 0; j1 < max2; j1++){
					returned[1][0] = j1;
					for (int j2 = j1; j2 < max2; j2++){
						returned[1][1] = j2;
						int c = 0;
						if (old[0] == 0) {
							c=1; 
						} else {
							if (i1 == 0){
								if (i2 == 0){
									c = 2;
								}else {
									c = 1;
								}
							}
						}
						double p2 = this.getProb(returned[1], old[1]-1, old[1],c);
						for (int k1 = 0; k1 < max3; k1++){
							returned[2][0] = k1;
							for (int k2 = k1; k2 < max3; k2++){
								returned[2][1] = k2;
								int c2 = 0;
								if (old[1] == 0) {
									c2 = 1;
								} else {
									if (j1 == 0){
										c2++;
										if (j2 == 0){
											c2++;
										}
									}
								}
								double p3 = this.getProb(returned[2], old[2]-1,old[2],c+c2);
								//int[] next = this.topAlpha(returned);
//								if (next[1] == 0){
//									if (p1*p2*p3 > 0){
//										System.out.println("Problem ["+old[0]+","+old[1]+","+old[2]+"] to ["+returned[0][0]+","+ returned[0][1]+"] " +
//												"["+returned[1][0]+","+ returned[1][1]+"] " + "["+returned[2][0]+","+ returned[2][1]+"] ");
//									}
//								}
								this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3,old);
								
							}
						}
						
					}
				}
			}
		}
		}
//		if (this.ltype == LType.ALL){
//			for (int a1 = 1; a1 <= old[0]; a1++){
//				if (l[old[0]][a1] == 0){
//					continue;
//				}
//				for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++){
//						returned[0][0] = i1;
//						for (int i2 = i1; i2 < this.cdfs[old[0]].length; i2++){
//							returned[0][1] = i2;
//							   double p1 = this.getProb(returned[0], old[0],a1)*l[old[0]][a1];
//							   for (int a2= 1; a2 <= old[1]; a2++){
//								   if (l[old[1]][a2] == 0){
//									   continue;
//								   }
//							for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++){
//								returned[1][0] = j1;
//								for (int j2 = j1; j2 < this.cdfs[old[1]].length; j2++){
//									returned[1][1] = j2;
//									double p2 = this.getProb(returned[1], old[1],a2)*l[old[1]][a2];
//									for (int a3 = 1; a3 <= old[2]; a3++){
//										if (l[old[2]][a3] == 0){
//											continue;
//										}
//										for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++){
//										returned[2][0] = k1;
//										for (int k2 = k1; k2 < this.cdfs[old[2]].length; k2++){
//											returned[2][1] = k2;
//											double p3 = this.getProb(returned[2], old[2],a3)*l[old[2]][a3];;
//											int[] next = this.topAlpha(returned);
//											int newindex = this.getIndex(next);
//											t2[newindex][oldindex] = t2[newindex][oldindex] +
//													nsucc*p1*p2*p3;
//										}
//										}
//										}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
			
		
	}

}
