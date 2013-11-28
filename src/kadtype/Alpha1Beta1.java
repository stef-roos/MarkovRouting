package kadtype;

import kadtype.KadType.LType;

public class Alpha1Beta1  extends KadTypeUpper2{
	
	public Alpha1Beta1(int b, int[] k, double[][] l,
			LType ltype) {
		super(b, 1, 1, k, l, ltype);
	}
	
	public Alpha1Beta1(int b, int[] k, int l) {
		super(b, 1, 1, k, l);
	}
	
	public Alpha1Beta1(int b, int k, double[][] l,
			LType ltype) {
		super(b, 1, 1, k, l, ltype);
	}
	
	public Alpha1Beta1(int b, int k, int l) {
		super(b, 1, 1, k, l);
	}
	
//	public Alpha3Beta2Upper2(int b, int k, double[][] l,
//			LType ltype, int replicas) {
//		super(b, 3, 2, k, l, ltype, replicas);
//	}

	@Override
	protected void processCDFsT1(double[][] t, int indexOld,
			int mindist) {
		for (int i = 0; i < mindist; i++){
					int[] re = new int[]{i};
					int indexnew = this.getIndex(re);
					if (this.ltype == LType.SIMPLE){
						//add prob that routing gets to state re from mindist
					   t[indexnew][indexOld] =  t[indexnew][indexOld] 
							+(1-this.success[mindist])*this.getProb(re, mindist,0);
					}   
					if (this.ltype == LType.ALL){
						//iterate over all possible resolutions
						for (int a = 1; a < mindist+1; a++){
							//only consider positive prob and re's that are within range closer to target
							//if (this.l[mindist][a] > 0 && k <= mindist - a){
								t[indexnew][indexOld] =  t[indexnew][indexOld] 
										+(1-this.success[mindist])*this.getProb(re, mindist,a-1)*this.l[mindist][a];
							//}
						}
					}
				}
			
		
		
	}
	
	@Override
	protected void processCDFsT2(int n, double[][] t2, int[] old, int oldindex,
			double nsucc) {
		int[][] returned = new int[1][1];
		if (this.ltype == LType.SIMPLE){
			//first lookup
		for (int i1 = 0; i1 < this.cdfs[old[0]].length; i1++){
			returned[0][0] = i1;
			
								double p = this.getProb(returned[0], old[0],0);
								this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p);
							}
		}
		if (this.ltype == LType.ALL){
			for (int a1 = 1; a1 <= old[0]; a1++){
				if (l[old[0]][a1] == 0){
					continue;
				}
				for (int i1 = 0; i1 < this.cdfs[old[0]].length-a1+1; i1++){
						returned[0][0] = i1;
						
											double p = this.getProb(returned[0], old[0],a1-1)*l[old[0]][a1];;
											this.makeDistinct(returned, t2, oldindex, old[0], n, nsucc*p);
										
				}
			
			}
		}	
	}
	
	

}
