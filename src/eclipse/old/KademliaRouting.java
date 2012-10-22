package eclipse.old;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import Jama.Matrix;

public class KademliaRouting {
 
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		System.out.println(new Date());
//		int n = 10000;
//		int k = 20;
//		int l = (int)Math.ceil(Math.log(n)/Math.log(2));
//		double[] done2 = new double[l+1];
//		Matrix t2 = new Matrix(getTransitionMatrix2(n,k));
//		Matrix probs2 = new Matrix(getInitial2(t2.getRowDimension(),l));
//		done2[0] = getZero2(probs2.getArray(),l);
//		for (int i = 1; i < l+1; i++){
//			probs2 = t2.times(probs2);
//			done2[i] = getZero2(probs2.getArray(),l);
//		}
//        for (int i = 0; i < done2.length; i++){
//        	System.out.println(i + " " +  + done2[i]);
//        }
//        System.out.println(new Date());
//	}
	
//	public static double getAttackEfficiency(int n,int a, int k){
//		double[] cdf = getRoutingCDF(n+a,k);
//		double aprob = (double)a/(double)(n+a);
//		double p = 0;
//		for (int i = 1; i < cdf.length; i++){
//			p = p + (cdf[i]-cdf[i-1])*(1-Math.pow(1-aprob, i-1));
//		}
//		return p;
//	}
	
	public static double[] getRoutingCDF(int n, int k){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2));
		double[] done2 = new double[l+1];
		Matrix t2 = new Matrix(getTransitionMatrix2(n,k));
		Matrix probs2 = new Matrix(getInitial2(t2.getRowDimension(),l));
		done2[0] = getZero2(probs2.getArray(),l);
		for (int i = 1; i < l+1; i++){
			probs2 = t2.times(probs2);
			done2[i] = getZero2(probs2.getArray(),l);
		}
		return done2;
	}
	
	
	
	public static double[][] getTransitionMatrix2(int n, int k){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2));
		double[][] matrix = new double[getIndex(0,0,l+1)][getIndex(0,0,l+1)];
		int countR, countC;
		countR = 0;
		for (int i1 = 0; i1 < l+1; i1++){
			for (int i2 = 0; i2 < l+1; i2++){
				for (int i3 = 0; i3 < l+1; i3++){
					for (int i4 = 0; i4 < l+1; i4++){
						for (int i5 = 0; i5 < l+1; i5++){
							for (int i6 = 0; i6 < l+1; i6++){
								int[] next = {i1,i2 ,i3,i4,i5,i6};
								int[][] duo = {new int[] {i1,i2}, new int[] {i3,i4}, new int[] {i5,i6}};
								Arrays.sort(next);
								countR = getIndex(next[0], next[1], next[2]);
								for (int j1 = 0; j1 < l+1; j1++){
									for (int j2 = j1; j2 < l+1; j2++){
										for (int j3 = j2; j3 < l+1; j3++){
											countC = getIndex(j1,j2,j3);
											matrix[countR][countC] = matrix[countR][countC]  + getProbto6(duo, new int[]{j1,j2,j3}, k);
//											if (getProbto6(duo, new int[]{j1,j2,j3}, k) > 0){
//												System.out.println(matrix[countR][countC]
//														+ " p: "+countR + " " + countC );
//											}
										}
									}
								}	
							}
						}
					}
				}
			}
		}
		return matrix;
	}
	
	
	
	public static double getProbto6(int[][] next, int[] old, int k){
		double p = 0;
		for (int i = 0; i < old.length; i++){
			if (old[i] == 0){
				if (next[0][0] > 0 || next[0][1] > 0
					||	next[1][0] > 0 || next[1][1] > 0
					|| next[2][0] > 0 || next[2][1] > 0){
				   return 0;
				} else {
					return 1;
				}
			}
		}
		boolean check = true;
		for (int i = 0; i < next.length; i++){
			if (next[i][1] < next[i][0]){
				check = false;
				break;
			}
			if (old[i] <= next[i][1]){
				check = false;
				break;
			}
		}
		if (check){
			p = 1;
			double q,q1,r,r1,s;
			for (int i = 0; i < next.length; i++){
				if (next[i][0] != 0){
				   q = Math.pow(1 - Math.pow(0.5, old[i]-next[i][0]), k);
				   s = Math.pow(1 - Math.pow(0.5, old[i]-next[i][0]), k-1);
				} else {
					q = 1;
					s = 1;
				}
				 q1 = Math.pow(1 - Math.pow(0.5, old[i]-next[i][0]-1), k);
				 if (next[i][1] != 0){
					   r = Math.pow(1 - Math.pow(0.5, old[i]-next[i][1]), k-1);
					} else {
					   r = 1;
					}	
				r1 = Math.pow(1 - Math.pow(0.5, old[i]-next[i][1]-1), k-1);
				
				  p = p * (q - q1)*(r-r1)/s;
				 
				
			}
		}
		return p;
	}
	
	private static double getZero2(double[][] probs, int l){
		double p = 0;
		for (int i1 = 0; i1 < 1; i1++){
			for (int i2 = i1; i2 < l; i2++){
				for (int i3 = i2; i3 < l; i3++){
					if (i1 == 0 || i2 == 0 || i3 == 0){
						p = p + probs[getIndex(i1,i2,i3)][0];
					}
					
				}
			}
		}	
		return p;
	}
	

	
	private static double[][] getInitial2(int size, int l){
		double[][] p = new double[size][1];
		p[0][0] = 0;
		int indexOld = getIndex(1,1,1);
		p[indexOld][0] = Math.pow(0.5, l-1);
		double norm = p[indexOld][0];
		int indexNew;
		for	(int i = 2; i < l; i++){
			indexNew = getIndex(i,i,i);
			p[indexNew][0] = 2*p[indexOld][0];
			norm = norm + p[indexNew][0];
			indexOld = indexNew;
		}
		for (int i = 0; i < l; i++){
			p[i][0] = p[i][0]/norm;
		}
		return p;
	}
	
	private static int getIndex(int a,int b, int c){
		int index = (int)Math.round(0.5*c*(c+1)*((double)(2*c+1)/(double)6 + 0.5));
		index = index + (int)Math.round(0.5*b*(b+1)) + a;	
		return index;		
	}
	

}
