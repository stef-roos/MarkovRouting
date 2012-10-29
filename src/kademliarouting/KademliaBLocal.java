package kademliarouting;

import Jama.Matrix;

public class KademliaBLocal {
	
	private KademliaBUniform uni;
	private KademliaBUniformUpper uniU;
	private KademliaB normal;
	
	public KademliaBLocal(int bits, int[] k, int nodes, boolean b){
		if (b){
			uniU = new KademliaBUniformUpper(bits,k,nodes);
		} else {
		   uni = new KademliaBUniform(bits,k,nodes);
		}
		normal = new KademliaB(bits,k,nodes);
		
		
	}
	
	public KademliaBLocal(int bits,  int nodes, boolean b){
		this(bits, makeK(bits),nodes,b);
		}
		
		private static int[] makeK(int bits){
			int[] k = new int[bits+1];
			for (int i = 0; i < k.length-4; i++){
				k[i] = 8;
			}
			k[k.length-4] = 16;
			k[k.length-3] = 32;
			k[k.length-2] = 64;
			k[k.length-1] = 128;
			return k;
		}

	public double[] getRoutingCDF(){
		double[] done2 = new double[normal.bits];
		double[][] t;
		if (uni != null){
		    t = uni.getTransitionFirstStep();
		} else {
			t = uniU.getTransitionFirstStep();
		}

		Matrix t2 = new Matrix(t);
		Matrix probs2 = new Matrix(normal.getInitial());
		done2[0] = probs2.getArray()[0][0];
		probs2 = t2.times(probs2);
		done2[1] = probs2.getArray()[0][0];
		t = normal.makeTransitionMatrix();
//		for (int i = 0; i < t[0].length; i++){
//		double sum = 0;
//		for (int j = 0; j < t.length; j++){
//			sum = sum + t[j][i];
//		}
//		System.out.println(sum);
	//}
		t2 = new Matrix(t);
		//System.out.println(t2.getColumnDimension() + " " +probs2.getRowDimension());
		for (int i = 2; i < done2.length; i++){
			probs2 = t2.times(probs2);
			done2[i] = probs2.getArray()[0][0];
		}
		return done2;
	}

}
