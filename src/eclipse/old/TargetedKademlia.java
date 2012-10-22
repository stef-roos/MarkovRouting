package eclipse.old;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import Jama.Matrix;

public class TargetedKademlia {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 1000;
		int a = 6;
		int k = 20;
		System.out.println(getAttackEfficiency(n, a, k));
//		double[][] m = getTransitionmatrix(n,k,a);
//		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 2;
//		double[][] init = getInitial(m.length, l);
//		Matrix probs = new Matrix(init);
//		Matrix t = new Matrix(m);
//		double[] done = new double[l];
//        for (int i = 1; i < l; i++){
//        			probs = t.times(probs);
//        			done[i] = probs.getArray()[0][0];
//        		}
//                for (int i = 0; i < done.length; i++){
//                	System.out.println(i + " " +  + done[i]);
//                }
//                System.out.println(new Date());
		
	}
	
	public static double getAttackEfficiency(int n, int a, int k){
		double[][] m = getTransitionmatrix(n,k,a);
		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 1;
		double[][] init = getInitial(m.length, l);
		Matrix probs = new Matrix(init);
		Matrix t = new Matrix(m);
		double[] done = new double[l];
        for (int i = 1; i < l; i++){
        			probs = t.times(probs);
        			done[i] = probs.getArray()[0][0];
        		}
       return 1-done[done.length-1];
	}
	
	public static double[][] getTransitionmatrix(int n, int k, int a){
		int l = (int)Math.ceil(Math.log(n)/Math.log(2)) + 1;
		int s = getIndex(1,1,l);
		double[][] matrix = new double[s][s];
		double[][] next1, next2,next3;
		int indexC,indexR;
		int[] next = new int[6];
		for (int i1 = 0; i1 < l; i1++){
			next1 = getNext(a,k,i1);
			for (int i2 = i1; i2 < l; i2++){
				next2 = getNext(a,k,i2);
				for (int i3 = i2; i3 < l; i3++){
					next3 = getNext(a,k,i3);
					indexC = getIndex(i1,i2,i3);
					if (i1 == 0 && i3 > 0){
						continue;
					}
					
					for (int j1 = 0; j1 < next1.length; j1++){
						for (int j2 = j1; j2 < next1.length; j2++){
							//if (j1 == 0 && j2 > 0) continue;
							for (int j3 = 0; j3 < next2.length; j3++){
								for (int j4 = j3; j4 < next2.length; j4++){
									//if (j3 == 0 && j4 > 0) continue;
									//if (j1 == 0 && j3 > 0) continue;
									for (int j5 = 0; j5 < next3.length; j5++){
										for (int j6 = j5; j6 < next3.length; j6++){
											//if (j5 == 0 && j6 > 0) continue;
											//if (j1 == 0 && j6 > 0) continue;
											next[0] = j1;
											next[1] = j2;
											next[2] = j3;
											next[3] = j4;
											next[4] = j5;
											next[5] = j6;
											Arrays.sort(next);
											indexR = getIndex(next[0], next[1], next[2]);
											matrix[indexR][indexC] = matrix[indexR][indexC] + next1[j1][j2]*next2[j3][j4]*next3[j5][j6];
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

	
	
	private static double[][] getNext(int a, int k, int old) {
		double[][] total = new double[old+1][old+1];
		if (old == 0) {
			total[0][0] = 1;
		} else {
			if (old == 1) {
				total[1][1] = 1;
			} else {
				int n = (int) Math.pow(2, old - 1);
				double[] p = new double[old + 1];
				p[0] = (double) (1) / (a + n);
				int c = 1;
				for (int i = 0; i < old; i++) {
					p[i + 1] = (double) (c + a) / (a + n);
					c = 2 * c;
				}
				double[] poss = new double[old+1];
				double[] possCDF = new double[old+1];
				double[] poss2 = new double[old+1];
				poss[0] = 1 - Math.pow(1-p[0], k);
				poss2[0] = 1 - Math.pow(1-p[0], k-1);
				possCDF[0] = poss2[0];
				for (int i = 0; i < old; i++){
					poss[i+1] = (1-Math.pow(1-p[i+1], k)) - (1-Math.pow(1-p[i], k));
					poss2[i+1] = (1-Math.pow(1-p[i+1], k-1)) - (1-Math.pow(1-p[i], k-1));
					possCDF[i+1] = possCDF[i] + poss2[i+1];
				}
				
				
				for (int i = 0; i < old +1; i++){
					double sum = 0;
					for (int j = i; j < old +1; j++){
						if (i > 0){
							if (possCDF[i-1] < 1)
						  total[i][j] = poss[i]*1/(1-possCDF[i-1])*poss2[j];
						} else {
							total[i][j] = poss[i]*poss2[j];	
						}
					}
					
				}
			}
			
			
		}
		return total;
	}

	

	private static int getIndex(int a, int b, int c) {
		if (a == 0 || b == 0 || c == 0) {
			return 0;
		} else {
			a--;
			b--;
			c--;
		}
		int index = (int) Math.round(0.5 * c * (c + 1)
				* ((double) (2 * c + 1) / (double) 6 + 0.5));
		index = index + (int) Math.round(0.5 * b * (b + 1)) + a + 1;
		return index;
	}

	private static HashMap<Integer, int[]> map(int l){
		HashMap<Integer, int[]> map = new HashMap<Integer, int[]>(getIndex(1,1,l));
		int count;
		for (int j1 = 0; j1 < l+1; j1++){
			for (int j2 = j1; j2 < l+1; j2++){
				for (int j3 = j2; j3 < l+1; j3++){
					count = getIndex(j1,j2,j3);
					map.put(count, new int[]{j1,j2,j3});
				}
			}
		}	
		return map;
	}
	
	private static double[][] getInitial(int size, int l){
		double[][] p = new double[size][1];
		p[0][0] = 0;
		int indexOld = getIndex(2,2,2);
		p[indexOld][0] = Math.pow(0.5, l-2);
		double norm = p[indexOld][0];
		int indexNew;
		for	(int i = 3; i < l; i++){
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

}
