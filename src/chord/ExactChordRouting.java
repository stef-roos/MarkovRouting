package chord;

import Jama.Matrix;

public class ExactChordRouting {
	
	public static double[] getRoutingCDFNode(int bits, int nodes){
		double[] res = new double[bits+1];
		
		Matrix t = new Matrix(getTransitionMatrixNode(bits,nodes));
		Matrix probs = new Matrix(getInitialNode(bits,nodes));
		res[0] = probs.getArray()[0][0];
		for (int i = 1; i < res.length; i++){
			probs = t.times(probs);
			res[i] = probs.getArray()[0][0];
		}
		return res;
	}
	
	public static double[][] getTransitionMatrixNode(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[][] trans = new double[count][count];
		trans[0][0] = 1;
		trans[0][1] = 1;
		for (int i = 2; i < count; i++){
			int log = (int)Math.floor(Math.log(i)/Math.log(2));
			int steps = i - (int)Math.pow(2, log);
			for (int j = 0; j < steps; j++){
				trans[steps-j][i] = Math.pow((double)(count-j)/(double)count, nodes-2) - Math.pow((double)(count-j-1)/(double)count, nodes-2);
			}
			trans[0][i] = Math.pow((double)(count-steps)/(double)count, nodes-2);
		}
		
		return trans;
	}
	
	public static double[][] getInitialNode(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[][] init = new double[count][1];
		double p = (double)	1/(double)(count-1);
		for (int i = 1; i < init.length; i++){
			init[i][0] = p;
		}
		return init;
	}
	
	public static double[] getRoutingCDFFile(int bits, int nodes){
		double[] res = new double[bits+1];
		double[] file = getSpaceProbs(bits, nodes);
		
		for (int j = 1; j < file.length; j++){
		Matrix t = new Matrix(getTransitionMatrixFile(bits,nodes,j));
		Matrix probs = new Matrix(getInitialFile(bits,nodes,j));
		res[0] = res[0] + file[j]*probs.getArray()[0][0];
		for (int i = 1; i < res.length; i++){
			probs = t.times(probs);
			res[i] = res[i] + file[j]*probs.getArray()[0][0];
		}
		}
		return res;
	}
	
	public static double[][] getTransitionMatrixFile(int bits, int nodes, int start){
		int count = (int)Math.pow(2, bits);
		double[][] trans = new double[count][count];
		trans[0][0] = 1;
		trans[0][1] = 1;
		int quo = count - start;
		for (int i = start; i < count; i++){
			int log = (int)Math.floor(Math.log(i)/Math.log(2));
			int steps = i - (int)Math.pow(2, log);
			if (steps < start){
				trans[0][i] = 1;
			}
			else {
			for (int j = 0; j < steps-start; j++){
				trans[steps-j][i] = Math.pow((double)(quo-j)/(double)(quo), nodes-3) - Math.pow((double)(quo-j-1)/(double)quo, nodes-3);
			}
			trans[start][i] = Math.pow((double)(quo-steps+start)/(double)quo, nodes-3);
			}
		}
		
		return trans;
	}
	
	public static double[][] getInitialFile(int bits, int nodes, int start){
		int count = (int)Math.pow(2, bits);
		double[][] init = new double[count][1];
		double p = (double)	1/(double)(count-start);
		for (int i = start; i < init.length; i++){
			init[i][0] = p;
		}
		return init;
	}
	
	public static double[] getSpaceProbs(int bits, int nodes){
		int count = (int)Math.pow(2, bits);
		double[] a = new double[count];
		double sum = 0;
		for (int i = 1; i < a.length; i++){
			a[i] = (double)(i)/(double)count*
					(Math.pow((double)(count-i+1)/(double)(count), nodes-2) - Math.pow((double)(count-i)/(double)(count), nodes-2));
			sum = sum + a[i];
		}
		for (int i = 0; i < a.length; i++){
			a[i] = a[i]/sum;
		}
		return a;
	}
	
	

}
