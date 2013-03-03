package kadtype;

public abstract class KadLower extends KadTypeCDFs {

	public KadLower(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadLower(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	protected void makeDistinct(double[][] returned, int index, int d1){
		
	}

}
