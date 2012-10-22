package eclipse.old;

import java.util.Vector;

import Jama.Matrix;

public class RandomChord {
	
	
	public static double getAttackEfficiency(int n,int a){
		double[] cdf = getRoutingCDF(n+a);
		double aprob = (double)a/(double)(n+a);
		double p = 0;
		for (int i = 1; i < cdf.length; i++){
			p = p + (cdf[i]-cdf[i-1])*(1-Math.pow(1-aprob, i-1));
		}
		return p;
	}
	
	public static double[] getRoutingCDF(int n){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2))+1;
		double[] done = new double[l+1];
		Matrix t = new Matrix(getTransitionMatrix(n));
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
		for (int i = 1; i < l; i++){
			p[i][0] = Math.pow(0.5, l-i);
			norm = norm + p[i][0];
		}
		for (int i = 0; i < l; i++){
			p[i][0] = p[i][0]/norm;
		}
		return p;
	}

	public static double[][] getTransitionMatrix(int n){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 1;
		double[][] m = new double[l][l];
		double[] p = new double[l];
		for (int i = 0; i < p.length; i++){
			p[i] = Math.pow(0.5, i);
		}
		for (int i = l-1; i > 0; i--){
			double norm = 1 - p[i];
			for (int j = 0; j < i; j++){
				m[j][i] = p[i-j]/norm;
			}
		}
		m[0][0] = 1;
		return m;
		
	}

}
