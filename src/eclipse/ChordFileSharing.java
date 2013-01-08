package eclipse;

import Jama.Matrix;

public class ChordFileSharing {
	
	public static double[] getRoutingCDF(int bits, int nodes){
		double[] res = new double[bits+1];
		
		Matrix t = new Matrix(getTransitionMatrix(bits,nodes));
		Matrix probs = new Matrix(getInitial(bits,nodes));
		res[0] = probs.getArray()[0][0];
		for (int i = 1; i < res.length; i++){
			probs = t.times(probs);
			res[i] = probs.getArray()[0][0];
		}
		return res;
	}
	
	public static double[][] getTransitionMatrix(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[][] trans = new double[count][count];
		trans[0][0] = 1;
		trans[0][1] = 1;
		for (int i = 2; i < count; i++){
			int log = (int)Math.floor(Math.log(i)/Math.log(2));
			int steps = i - (int)Math.pow(2, log);
			for (int j = 0; j < steps; j++){
				trans[steps-j][i] = Math.pow((double)(count-j)/(double)count, nodes-1) - Math.pow((double)(count-j-1)/(double)count, nodes-1);
			}
			trans[0][i] = Math.pow((double)(count-steps)/(double)count, nodes-1);
		}
		
		return trans;
	}
	
	public static double[][] getInitial(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[][] init = new double[count][1];
		double p = (double)	1/(double)(count-1);
		for (int i = 1; i < init.length; i++){
			init[i][0] = p;
		}
		return init;
	}

}
