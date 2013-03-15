package kadtype;

import kadtype.KadType.LType;

public class Alpha4Beta3Lower extends KadTypeLower {

	public Alpha4Beta3Lower(int b, int[] k, double[][] l,
			LType ltype) {
		super(b, 4, 3, k, l, ltype);
	}
	
	public Alpha4Beta3Lower(int b, int[] k, int l) {
		super(b, 4, 3, k, l);
	}
	
	public Alpha4Beta3Lower(int b, int k, double[][] l,
			LType ltype) {
		super(b, 4, 3, k, l, ltype);
	}
	
	public Alpha4Beta3Lower(int b, int k, int l) {
		super(b, 4, 3, k, l);
	}

	@Override
	protected void processCDFsT1(double[][] cdfs, double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i < cdfs.length; i++){
			for (int j = i; j < cdfs.length; j++){
				for (int k = j; k < cdfs.length; k++){
					for (int m = k; m < cdfs.length; m++){
					int[] re = new int[]{i,j,k,m};
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

	}

	@Override
	protected void processCDFsT2(int n, double[][] t2, int[] old, int oldindex,
			double nsucc) {
		int[][] returned = new int[4][3];
		if (this.ltype == LType.SIMPLE) {
			for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++) {
				returned[0][0] = i1;
				for (int i2 = i1; i2 < this.cdfs[old[0]].length; i2++) {
					returned[0][1] = i2;
					for (int i3 = i2; i3 < this.cdfs[old[0]].length; i3++) {
						returned[0][2] = i3;
						double p1 = this.getProb(returned[0], old[0]);
						for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++) {
							returned[1][0] = j1;
							for (int j2 = j1; j2 < this.cdfs[old[0]].length; j2++) {
								returned[1][1] = j2;
								for (int j3 = j2; j3 < this.cdfs[old[0]].length; j3++) {
									returned[1][2] = j3;
									double p2 = this.getProb(returned[1],
											old[1]);
									for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++) {
										returned[2][0] = k1;
										for (int k2 = k1; k2 < this.cdfs[old[2]].length; k2++) {
											returned[2][1] = k2;
											for (int k3 = k2; k3 < this.cdfs[old[2]].length; k3++) {
												returned[2][2] = k3;
												double p3 = this.getProb(
														returned[2], old[2]);
												for (int m1 = 0; m1 < this.cdfs[old[2]].length; m1++) {
													returned[3][0] = m1;
													for (int m2 = m1; m2 < this.cdfs[old[3]].length; m2++) {
														returned[3][1] = m2;
														for (int m3 = m2; m3 < this.cdfs[old[3]].length; m3++) {
															returned[3][2] = m3;
															double p4 = this
																	.getProb(
																			returned[3],
																			old[3]);
															this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3*p4);
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
		}
		if (this.ltype == LType.ALL) {
			for (int a1 = 1; a1 <= old[0]; a1++) {
				if (l[old[0]][a1] == 0) {
					continue;
				}
				for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++) {
					returned[0][0] = i1;
					for (int i2 = i1; i2 < this.cdfs[old[0]].length; i2++) {
						returned[0][1] = i2;
						for (int i3 = i2; i3 < this.cdfs[old[0]].length; i3++) {
							returned[0][2] = i3;
							double p1 = this.getProb(returned[0], old[0])
									* l[old[0]][a1];
							for (int a2 = 1; a2 <= old[1]; a2++) {
								if (l[old[1]][a2] == 0) {
									continue;
								}
								for (int j1 = 0; j1 < this.cdfs[old[1]].length; j1++) {
									returned[1][0] = j1;
									for (int j2 = j1; j2 < this.cdfs[old[0]].length; j2++) {
										returned[1][1] = j2;
										for (int j3 = j2; j3 < this.cdfs[old[0]].length; j3++) {
											returned[1][2] = j3;
											double p2 = this.getProb(
													returned[1], old[1])
													* l[old[1]][a2];
											for (int a3 = 1; a3 <= old[2]; a3++) {
												if (l[old[2]][a3] == 0) {
													continue;
												}
												for (int k1 = 0; k1 < this.cdfs[old[2]].length; k1++) {
													returned[2][0] = k1;
													for (int k2 = k1; k2 < this.cdfs[old[2]].length; k2++) {
														returned[2][1] = k2;
														for (int k3 = k2; k3 < this.cdfs[old[2]].length; k3++) {
															returned[2][2] = k3;
															double p3 = this
																	.getProb(
																			returned[2],
																			old[2])
																	* l[old[2]][a3];
															for (int a4 = 1; a4 <= old[3]; a4++) {
																if (l[old[2]][a4] == 0) {
																	continue;
																}
																for (int m1 = 0; m1 < this.cdfs[old[3]].length; m1++) {
																	returned[3][0] = m1;
																	for (int m2 = m1; m2 < this.cdfs[old[3]].length; m2++) {
																		returned[3][1] = m2;
																		for (int m3 = m2; m3 < this.cdfs[old[3]].length; m3++) {
																			returned[3][2] = m3;
																			double p4 = this
																					.getProb(
																							returned[2],
																							old[2])
																					* l[old[2]][a3];
																			this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p1*p2*p3*p4);
																			
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
						}
					}
				}
			}
		}
	}

}
