package chord;

import Jama.Matrix;

public class ChordFileExt {
	
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
			//double sum = 0;
			int log = (int)Math.floor(Math.log(i)/Math.log(2));
			int non = (int)Math.pow(2, log+1)-i;
			double p = Math.pow((double)(count-non)/(double)count, nodes-1);
			trans[0][i] = p;
			int steps = i - (int)Math.pow(2, log);
			for (int j = 0; j < steps; j++){
				trans[steps-j][i] = (1-p)*(Math.pow((double)(count-j)/(double)count, nodes-2) - Math.pow((double)(count-j-1)/(double)count, nodes-2));
			}
			trans[0][i] = trans[0][i] + (1-p)*Math.pow((double)(count-steps)/(double)count, nodes-2);
//			int tocover = count;
//			for (int k = log; k >= 0; k--){
//			  int steps = i-(int)Math.pow(2, k);
//			  int bound = (k==log)?steps:(int)(Math.pow(2, k)-1);
//			  //double factor = 1-sum;
//			  for (int j = 0; j <= bound; j++){
//				trans[steps-j][i] = (Math.pow((double)(tocover-j)/(double)count, nodes-1) - Math.pow((double)(tocover-j-1)/(double)count, nodes-1));
//				//sum = 
//			  }
//			  tocover = tocover - bound-1;
//			}
			
			  
			
		}
		
		return trans;
	}
	
	public static double[][] getInitial(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[][] init = new double[count][1];
		for (int d = 1; d < count; d++){
		double p = (double)	1/(double)(count-d);
		p = p*(Math.pow((count-d+1)/(double)count, nodes) - Math.pow((count-d)/(double)count, nodes));
		  for (int i = 1; i < init.length-d+1; i++){
			init[i][0] = init[i][0] + p;
		  }
		}
		return init;
	}

}
