package replicas;

import kadtype.Alpha3Beta2Upper;

public class Alpha3Beta2UpperReplica extends Alpha3Beta2Upper {

	public Alpha3Beta2UpperReplica(int b, int[] k, double[][] l,
			LType ltype) {
		super(b,k, l, ltype);
	}
	
	public Alpha3Beta2UpperReplica(int b, int[] k, int l) {
		super(b, k, l);
	}
	
	public Alpha3Beta2UpperReplica(int b, int k, double[][] l,
			LType ltype) {
		super(b, k, l, ltype);
	}
	
	public Alpha3Beta2UpperReplica(int b, int k, int l) {
		super(b, k, l);
	}

}
