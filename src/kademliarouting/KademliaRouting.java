package kademliarouting;

import java.util.Arrays;
import java.util.HashMap;

import eclipse.Calc;

import Jama.Matrix;


public class KademliaRouting {
	int bits;
	int alpha = 3;
	int beta = 2;
	int k;
	int n;
	double[] notfound;
	double[][] F;
	
	/**
	 * 
	 * @param bits: bits the system uses
	 * @param k
	 * @param nodes
	 */
	public KademliaRouting(int bits, int k, int nodes){
		this.bits = bits;
		this.k = k;
		this.n = nodes;
		this.setNF();
		//this.setF();
	}
	
	/**
	 * get routing distribution by Markov chains
	 * @return
	 */
	public double[] getRoutingCDF(){
		double[] done2 = new double[this.bits];
		//calculate matrix and dist for first step 
		double[][] t = getTransitionFirstStep();
//		HashMap<Integer,int[]> map = this.map();
//		for (int j = 0; j < t.length; j++){
//			double sumC = 0;
//			double sumR = 0; 
//			for (int i = 0; i < t.length; i++){
//				sumC = sumC + t[i][j];
//				sumR = sumR + t[j][i];
//			}
//			int[] s = map.get(j);
//			if (sumC < 0.99)
//			System.out.println(sumC + " " + sumR + " (" + s[0] +"," + s[1]+ "," + s[2] +")");
//		}
		Matrix t2 = new Matrix(t);
		Matrix probs2 = new Matrix(getInitial());
		done2[0] = probs2.getArray()[0][0];
		probs2 = t2.times(probs2);
		done2[1] = probs2.getArray()[0][0];
		
		//calculate matrix for later steps
		t = this.makeTransitionMatrix();
		t2 = new Matrix(t);
		//System.out.println(t2.getColumnDimension() + " " +probs2.getRowDimension());
		for (int i = 2; i < done2.length; i++){
			probs2 = t2.times(probs2);
			done2[i] = probs2.getArray()[0][0];
		}
		return done2;
	}
	
	/**
	 * initial distribution: all IDs not destination equally likely
	 * @return
	 */
	private double[][] getInitial() {
		double[][] res = new double[this.bits+1][1];
		double c=1;
		double sum = 0;
		for (int i = this.bits; i > 0; i--){
			c = c/2;
			res[i][0] = c;
			sum = sum + c;
		}
		for (int i = this.bits; i > 0; i--){
			res[i][0] = res[i][0]/sum;
		}
		return res;
	}

	/**
	 * compute transition matrix for steps > 1
	 * @return
	 */
	public double[][] makeTransitionMatrix(){
		//size of matrix
		int l = getIndex(1,1,this.bits+1);
		double[][] matrix = new double[l][l];
		double[][] next1, next2,next3;
		int indexC,indexR;
		int[] next = new int[6];
		//iterate over alpha selected contacts and get distribution of next contacts
		for (int i1 = 0; i1 < this.bits+1; i1++){
			next1 = getNext(i1);
			for (int i2 = i1; i2 < this.bits+1; i2++){
				next2 = getNext(i2);
				for (int i3 = i2; i3 < this.bits+1; i3++){
					next3 = getNext(i3);
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
	
	
	/**
	 * get distribution of next beta contacts assuming current contact is at bit distance d
	 * @param d
	 * @return
	 */
	public double[][] getNext(int d) {
		if (d < 2){
			double[][] next = new double[1][1];
			next[0][0] = 1;
			return next;
		}
		double[][] next = new double[d][d];
		double[][] f = getF(d);
		//find destination
		next[0][0] = 1- this.notfound[d-1];
		for (int i = 1; i < d; i++){
			for (int j = i; j < d; j++){
				//prob first contact is at distance i
				next[i][j] = this.notfound[d-1]*(f[i][0] - f[i-1][0]);
				if (1 - f[i-1][1] > 0){
					//prob first contact is at distance i*(second at distance j | first at i)
				next[i][j] = next[i][j]*(f[j][1]-f[j-1][1])/(1-f[i-1][1]);
				} else {
					
				}
			}
		}
		return next;
	}
	
	/**
	 * get cumulative distribution over next distance in case destination not found
	 * 
	 * @param d: current distance
	 * @return
	 */
	public double[][] getF(int d){
		double[][] res  = new double[d][this.beta];
		for (int i = 0; i < d; i++){
			double p = 1-Math.pow(0.5, d-1-i);
			//prob for beta-th contact 
			double pA = Math.pow(p, this.k-this.beta);
			for (int j = this.beta-1; j > -1; j--){
				pA = pA*p;
				res[i][j] = 1- pA;
			}
		}
		//'normalize'
		for (int i = 0; i < this.beta; i++){
			double p0 = res[0][i];
			for (int j = 0; j < res.length; j++){
				res[j][i] = (res[j][i]-p0)/(1-p0);
			}
		}
		return res;
	}

	/**
	 * probability not to find destination for each distance
	 */
	private void setNF(){
		notfound = new double[bits+1];
		for (int i = 0; i < notfound.length; i++){
			double p = Math.pow(0.5, this.bits-i);
			for (int x = this.k; x < this.n-alpha; x++){
				notfound[i] = Math.min(notfound[i] + getRT(x)*Calc.binomDist(this.n-alpha-1, x, p),1);
//				if (Calc.binomDist(this.n-alpha-1, x, p) > 1){
//					System.out.println("x= " + x + " p=" + p + " " + Calc.binomDist(this.n-alpha-1, x, p));
//				}
			}
		}
		
	}
	
//	private void setF(){
//		F = new double[bits+1][this.beta];
//		for (int i = 0; i < bits; i++){
//			double p = 1-Math.pow(1/2, this.bits-i);
//			double pA = Math.pow(p, this.k-this.beta);
//			for (int j = this.beta-1; j > -1; j--){
//				pA = pA*p;
//				F[i][j] = pA;
//			}
//		}
//	}
	
	/**
	 * computes probability that k links going in a set of size (x+1) are within x nodes
	 * (=contact node different from destination if x other nodes are in that region)
	 * @param x
	 * @return
	 */
	private double getRT(int x){
		double res = 1;
		for (int i = 0; i < this.k; i++){
			res = res*(double)(x-i)/(double)(x+1-i);
		}
		return res;
	}
	

	/**
	 * index for matrix
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private static int getIndex(int a,int b, int c){
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
	
	
//	private void checkNext(double[][] next){
//		double p = 0;
//		for (int i = 0; i < next.length; i++){
//			for (int j = 0; j < next[i].length; j++){
//				p = p + next[i][j];
//			}
//		}
//		if (p < 0.9999 || p > 1.001)
//		System.out.println(p);
//		
//	}
	
//	private HashMap<Integer, int[]> map(){
//		HashMap<Integer, int[]> map = new HashMap<Integer, int[]>(getIndex(1,1,this.bits+1));
//		int count;
//		for (int j1 = 0; j1 < this.bits+1; j1++){
//			for (int j2 = j1; j2 < this.bits+1; j2++){
//				for (int j3 = j2; j3 < this.bits+1; j3++){
//					count = getIndex(j1,j2,j3);
//					map.put(count, new int[]{j1,j2,j3});
//				}
//			}
//		}	
//		return map;
//	}
	
	/**
	 * get transition matrix for first step
	 * @return
	 */
	private double[][] getTransitionFirstStep(){
		double[][] res = new double[getIndex(1,1,this.bits+1)][this.bits+1];
		res[0][0] = 1;
		res[0][1] = 1;
		for (int d = 2; d < this.bits +1; d++){
			double[][] f = this.getFFirst(d);
			//probability to find target
			res[0][d] = 1- this.notfound[d-1];
			//iterate over all non-terminal possibilities
			for (int a1 = 1; a1 < d; a1++){
				for (int a2 = a1; a2 < d; a2++){
					for (int a3=a2; a3 < d; a3++){
						int rowC = this.getIndex(a1, a2, a3);
						//prob of first contact
						res[rowC][d] = this.notfound[d-1]*(f[a1][0] - f[a1-1][0]);
						if (1 - f[a1-1][1] > 0){
							//*(prob of second contact | prob of first contact)
							res[rowC][d] = res[rowC][d]*(f[a2][1]-f[a2-1][1])/(1-f[a1-1][1]);
							} else {
								
							}
						if (1 - f[a2-1][2] > 0){
							//*(prob of third contact | prob of first and second contact)
							res[rowC][d] = res[rowC][d]*(f[a3][2]-f[a3-1][2])/(1-f[a2-1][2]);
							} else {
								
							}
					}
				}
			}
		}
		
		return res;
	}
	
	/**
	 * cumulative distribution of non-terminal after first step
	 * = getF with beta replaced by alpha
	 * @param d
	 * @return
	 */
	private double[][] getFFirst(int d){
		double[][] res  = new double[d][this.alpha];
		for (int i = 0; i < d; i++){
			double p = 1-Math.pow(0.5, d-1-i);
			double pA = Math.pow(p, this.k-this.alpha);
			for (int j = this.alpha-1; j > -1; j--){
				pA = pA*p;
				res[i][j] = 1- pA;
			}
		}
		for (int i = 0; i < this.alpha; i++){
			double p0 = res[0][i];
			for (int j = 0; j < res.length; j++){
				res[j][i] = (res[j][i]-p0)/(1-p0);
			}
		}
		return res;
	}
	
	
}
