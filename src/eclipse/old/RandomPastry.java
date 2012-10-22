package eclipse.old;

import Jama.Matrix;

public class RandomPastry {
	
	public static double getAttackEfficiency(int n,int a, int b){
		double[] cdf = getRoutingCDF(n+a,b);
		double aprob = (double)a/(double)(n+a);
		double p = 0;
		for (int i = 1; i < cdf.length; i++){
			p = p + (cdf[i]-cdf[i-1])*(1-Math.pow(1-aprob, i-1));
		}
		return p;
	}
	
	public static double[] getRoutingCDF(int n, int b){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2));
		double[] done = new double[l+1];
		Matrix t = new Matrix(getTransitionMatrix(n,b));
		Matrix probs = new Matrix(getInitial(l));
		done[0] = probs.getArray()[0][0];
		for (int i = 1; i < l+1; i++){
			probs = t.times(probs);
			done[i] = probs.getArray()[0][0];
		}
		return done;
	}

	private static double[][] getInitial(int l) {
	    double[][] p = new double[l][1];
	    double norm = 0;
	    p[0][0] = 0;
		for (int i = 1; i < l; i++){
			p[i][0] = Math.pow(0.5,l-i);
			norm = norm + p[i][0];
		}
		for (int i = 0; i < l; i++){
			p[i][0] = p[i][0]/norm;
		}
		return p;
	}

	public static double[][] getTransitionMatrix(int n, int b){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2));
		double[][] m = new double[l][l];
		double[] p = new double[l];
		for (int i = 1; i < p.length; i++){
			p[i] = 1-Math.pow(0.5, i);
		}
		for (int i = l-1; i > b; i--){
			double norm = p[i-b+1];
			for (int j = 0; j <= i-b; j++){
				m[j][i] = (p[j+1] - p[j])/norm;
			}
		}
		for (int i = 0; i < b+1; i++){
		   m[0][i] = 1;
		}
		return m;
		
	}

}
