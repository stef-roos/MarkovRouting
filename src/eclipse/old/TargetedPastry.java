package eclipse.old;

import Jama.Matrix;

public class TargetedPastry {
	
	public static double getAttackEfficiency(int n, int a, int b){
		double[][] m = getTransitionmatrix(n,a,b);
		for (int i = 0; i < m.length; i++){
			double sum = 0;
			for (int j = 0; j < m.length; j++){
				sum = sum + m[j][i];
//				if (m[j][i] < 0){
//					System.out.println(m[j][i]);
//				}
			}
			//System.out.println(sum);
		}
		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 1;
		double[][] init = getInitial(l);
		Matrix probs = new Matrix(init);
		Matrix t = new Matrix(m);
		double[] done = new double[l];
        for (int i = 1; i < l; i++){
        			probs = t.times(probs);
        			done[i] = probs.getArray()[1][0];
        		}
       return done[done.length-1];
	}
	
	
	public static double[][] getTransitionmatrix(int n, int a, int b){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 1;
		double[][] m = new double[l][l];
		double[][] nodes = getNumNodes(n,a,l);
		m[0][0] = 1;
		m[1][1] = 1;
		for (int i = b+2; i < l; i++){
			double ni = nodes[i-b][1];
			for (int j = 0; j < i-b+1; j++){
				m[j][i] = nodes[j][0]/ni;
			}
		}
		for (int i = 2; i < b+2; i++){
			m[0][i] = (double)1/(double)(a+1);
			m[1][i] = 1-m[0][i];
		}
		return m;
	}
	
	
	private static double[][] getNumNodes(int n, int a, int l){
		double[][] nodes = new double[l][2];
		nodes[0][0] = 1;
		nodes[0][1] = 1; 
		nodes[1][0] = a;
		nodes[1][1] = a +1;
		double c = n/2;
		nodes[l-1][1] = n+a;
		for (int i = l-1; i > 1; i--){
			nodes[i][0] = c;
			if (i > 2)
			nodes[i-1][1] = nodes[i][1]-c; 
			c = c/2;
		}
		return nodes;
	}
	
	private static double[][] getInitial(int l) {
	    double[][] p = new double[l][1];
	    double norm = 0;
	    p[0][0] = 0;
	    norm = 0;
	    p[1][0] = 0;
		for (int i = 2; i < l; i++){
			p[i][0] = Math.pow(0.5, l-i);
			norm = norm + p[i][0];
		}
		for (int i = 0; i < l; i++){
			p[i][0] = p[i][0]/norm;
		}
		return p;
	}

}
