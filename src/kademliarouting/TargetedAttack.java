package kademliarouting;

import java.util.Arrays;

import Jama.Matrix;
import eclipse.Calc;

public class TargetedAttack {
	
	int bits;
	int alpha = 3;
	int beta = 2;
	int k;
	int n;
	double[] notfound;
	double[][] F;
	double[][] attacked;
	int a; 
	
	/**
	 * 
	 * @param bits: bits the system uses
	 * @param k
	 * @param nodes
	 */
	public TargetedAttack(int bits, int k, int nodes, int a){
		this.bits = bits;
		this.k = k;
		this.n = nodes;
		this.a = a;
		this.setNF();
		//this.setF();
	}
	
	public double getAttackEfficiency(){
		double[] done2 = new double[this.bits];
		//calculate matrix and dist for first step 
		double[][] t = getTransitionFirstStep();
		int index = this.getIndex(1, 1, 1);
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
		done2[0] = probs2.getArray()[index][0];
		//System.out.println(done2[0]);
		probs2 = t2.times(probs2);
		done2[1] = probs2.getArray()[index][0];
		//System.out.println(done2[1]);
		//calculate matrix for later steps
		t = this.makeTransitionMatrix();
//		for (int j = 0; j < t[0].length; j++){
//			double s = 0;
//			for (int i = 0; i < t.length; i++){
//				s = s + t[i][j];
//			}
//			System.out.println(s);
//		}
		t2 = new Matrix(t);
		//System.out.println(t2.getColumnDimension() + " " +probs2.getRowDimension());
		for (int i = 2; i < done2.length; i++){
			probs2 = t2.times(probs2);
			done2[i] = probs2.getArray()[index][0];
		}
		return done2[done2.length-1];
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
//		for (int j = 0; j < t[0].length; j++){
//			double s = 0;
//			for (int i = 0; i < t.length; i++){
//				s = s + t[i][j];
//			}
//			System.out.println(s);
//		}
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
	double[][] getInitial() {
		double[][] res = new double[this.bits+2][1];
		double c=1;
		double sum = 0;
		for (int i = this.bits; i > 1; i--){
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
		int l = getIndex(1,1,this.bits+2);
		double[][] matrix = new double[l][l];
		double[][] next1, next2,next3;
		int indexC,indexR;
		int[] next = new int[6];
		double[][][] nexts = new double[this.bits+2][][];
		for (int i = 0; i < nexts.length; i++){
			nexts[i] = this.getNext(i);
		}
		//iterate over alpha selected contacts and get distribution of next contacts
		for (int i1 = 0; i1 < this.bits+2; i1++){
			next1 = nexts[i1];
			for (int i2 = i1; i2 < this.bits+2; i2++){
				next2 = nexts[i2];
				for (int i3 = i2; i3 < this.bits+2; i3++){
					next3 = nexts[i3];
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
		if (d == 0){
			double[][] next = new double[1][1];
			next[0][0] = 1;
			return next;
		}
		if (d == 1){
			double[][] next = new double[2][2];
			next[1][1] = 1;
			return next;
		}
		if (d == 2){
			double[][] next = new double[2][2];
			next[0][0] = a < k?1:k/a;
			next[1][1] = 1 - next[0][0];
			return next;
		}
		double[][] next = new double[d][d];
		double[][] f = getF(d-1);
		//find destination
		next[0][0] = 1- this.notfound[d-1];
		next[1][1] = this.notfound[d-1]*this.attacked[d-1][1];
		for (int j = 2; j < d; j++){
			next[1][j] = this.notfound[d-1]*(this.attacked[d-1][0]-this.attacked[d-1][1])*(f[j-1][1] - f[j-2][1]);
		}
		for (int i = 2; i < d; i++){
			for (int j = i; j < d; j++){
				//prob first contact is at distance i
				next[i][j] = this.notfound[d-1]*(1-this.attacked[d-1][0])*(f[i-1][0] - f[i-2][0]);
				if (1 - f[i-1][1] > 0){
					//prob first contact is at distance i*(second at distance j | first at i)
				next[i][j] = next[i][j]*(f[j-1][1]-f[j-2][1])/(1-f[i-2][1]);
				} else {
					
				}
			}
		}
		
//		double sum = 0;
//		for (int i = 0; i < d; i++){
//			for (int j = i; j < d; j++){
//				sum = sum + next[i][j];
//			}
//		}
//		System.out.println(sum + " d=" +d);
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
		notfound = new double[bits+2];
		attacked = new double[bits+2][3];
		attacked[1][2] = a==0?0:1;
		attacked[1][1] = a==0?0:1;
		attacked[1][0] = a==0?0:1;
		notfound[0] = 0;
		notfound[1] = 1;
		for (int i = 2; i < notfound.length; i++){
			double p = Math.pow(0.5, this.bits-i+1);
			for (int x = 0; x < this.n-1; x++){
				double xp = Calc.binomDist(this.n-2, x, p);
				if (x >= this.k-a){
				notfound[i] = Math.min(notfound[i] + (1-k/(double)(a+x+1))*xp,1);
				}
				double s = 0;
				for (int j = 0; j < 3; j++){
					s = s + this.getRT(x+a, j);
					//System.out.println(s);
					attacked[i][j] = (1 -s)*xp;
				}
//				if (Calc.binomDist(this.n-alpha-1, x, p) > 1){
//					System.out.println("x= " + x + " p=" + p + " " + Calc.binomDist(this.n-alpha-1, x, p));
//				}
			}
		}
		
//      for (int i = 0; i < this.notfound.length; i++){
//			//System.out.println("attacked " + this.attacked[i][0] + " " + this.attacked[i][1] + " " + this.attacked[i][2] + " ");
//		}
//		
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
	private double getRT(int x,int c){
		if (c > a) return 0;
		double res = (double)(Calc.binom(this.k, c)*Calc.binom(x-this.k, a-c))/(double)Calc.binom(x, a);
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
		double[][] res = new double[getIndex(1,1,this.bits+2)][this.bits+2];
		res[0][0] = 1;
		for (int d = 1; d < this.bits +2; d++){
			double[][][] next = this.getNextFirst(d);
			//double sum = 0;
			
			//probability to find target
			//iterate over all non-terminal possibilities
			for (int a1 = 0; a1 < d; a1++){
				for (int a2 = a1; a2 < d; a2++){
					for (int a3=a2; a3 < d; a3++){
						int rowC = this.getIndex(a1, a2, a3);
						//System.out.println(rowC );
						//prob of first contact
						res[rowC][d] = res[rowC][d] +next[a1][a2][a3];
						//sum = sum + next[a1][a2][a3];
						
					}
				}
			}
			//System.out.println("d= " + d  + " sum = " + sum);
			
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

	public double[][][] getNextFirst(int d) {
		if (d == 0){
			double[][][] next = new double[1][1][1];
			next[0][0][0] = 1;
			return next;
		}
		if (d == 1){
			double[][][] next = new double[2][2][2];
			next[1][1][1] = 1;
			return next;
		}
		double[][][] next = new double[d][d][d];
		double[][] f = getFFirst(d-1);
		//find destination
		next[0][0][0] = 1- this.notfound[d-1];
		next[1][1][1] = this.notfound[d-1]*this.attacked[d-1][2];
		for (int j = 2; j < d; j++){
			next[1][1][j] = this.notfound[d-1]*(this.attacked[d-1][1]-this.attacked[d-1][2])*(f[j-1][2] - f[j-2][2]);
		}
		for (int i = 2; i < d; i++){
			for (int j = i; j < d; j++){
				//prob first contact is at distance i
				next[1][i][j] = this.notfound[d-1]*(this.attacked[d-1][0]-this.attacked[d-1][1])*(f[i-1][1] - f[i-2][1]);
				if (1 - f[i-1][2] > 0){
					//prob first contact is at distance i*(second at distance j | first at i)
				next[1][i][j] = next[1][i][j]*(f[j-1][2]-f[j-2][2])/(1-f[i-2][2]);
				} else {
					
				}
			}
		}
		for (int k = 2; k < d; k++){
		for (int i = k; i < d; i++){
			for (int j = i; j < d; j++){
				//prob first contact is at distance i
				next[k][i][j] = this.notfound[d-1]*(1-this.attacked[d-1][0])*(f[k-1][0] - f[k-2][0]);
				if (1 - f[k-1][1] > 0){
					//prob first contact is at distance i*(second at distance j | first at i)
				next[k][i][j] = next[k][i][j]*(f[i-1][1]-f[i-2][1])/(1-f[k-2][1]);
				} else {
					
				}
				if (1 - f[i-1][2] > 0){
					//prob first contact is at distance i*(second at distance j | first at i)
				next[k][i][j] = next[k][i][j]*(f[j-1][2]-f[j-2][2])/(1-f[i-2][2]);
				} else {
					
				}
			}
		}
		}
//		double sum = 0;
//		for (int k = 0; k < d; k++){
//			for (int i = k; i < d; i++){
//				for (int j = i; j < d; j++){
//					sum = sum + next[k][i][j];
//				}
//			}
//		}
//		System.out.println("d= " + d  + " sum = " + sum);
		return next;
	}
}
