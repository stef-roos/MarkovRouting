package attacksKad;

import kadtype.KadType.LType;

public class FailureKADA4B1Upper extends FailureAlpha4Beta1Upper {
	
	public FailureKADA4B1Upper(int b, double fprob) {
		super(b, 10, makeL(b), LType.ALL,fprob);
		// TODO Auto-generated constructor stub
	}
	
	private static double[][] makeL(int b){
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 0.75;
			l[i][4] = 0.25;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		return l; 
	}

}
