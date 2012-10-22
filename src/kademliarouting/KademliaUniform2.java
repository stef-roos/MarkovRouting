package kademliarouting;

import java.util.Arrays;
import java.util.HashMap;

import Jama.Matrix;
import eclipse.Calc;

public class KademliaUniform2 {
	
	int bits;
	int alpha = 3;
	int beta = 2;
	int k;
	int bitperStep;
	int m;
	int n;
	double[][] found;
	double[][] F;
	double[][] minNext;
	double[][] chooseInt;
	
	public KademliaUniform2(int bits, int k, int nodes){
		this.bits = bits;
		this.k = k;
		this.bitperStep = (int)Math.floor(Math.log(k)/Math.log(2));
		this.m = (int)Math.pow(2, this.bitperStep);
		this.n = nodes;
		this.setNF();
		this.setDist();
		//this.setF();
	}
	
	public double[] getRoutingCDF(){
		double[] done2 = new double[this.bits];
		double[][] t = getTransitionFirstStep();
//		for (int i = 0; i < t[0].length; i++){
//			double sum = 0;
//			for (int j = 0; j < t.length; j++){
//				sum = sum + t[j][i];
//				
//			}
//			System.out.println(sum);
//		}
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
		t = this.makeTransitionMatrix();
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

	public double[][] makeTransitionMatrix(){
		int l = getIndex(1,1,this.bits+1);
		double[][] matrix = new double[l][l];
		double[][] next1, next2,next3;
		int indexC,indexR;
		int[] next = new int[6];
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
	
	
	
	public double[][] getNext(int d) {
		if (d < 2+this.bitperStep){
			double[][] next = new double[1][1];
			next[0][0] = 1;
			return next;
		}
		double[][] next = new double[d][d];
		double[][] links = this.getExtraLinks(d-1);
		double[][] f = this.getF(d-this.bitperStep, 1);
		//double sum0 = 0;
		
			for (int j = 0; j < links[0].length; j++){
			//	sum0 = 0;
				for (int i = 1; i < f.length; i++){
				next[i][d-1-j] = next[i][d-1-j] + 
						(1-this.found[0][d-1-this.bitperStep])*(f[i][0]-f[i-1][0])*links[0][j]; 
				//sum0 = sum0 + (f[i][0]-f[i-1][0]);
			}
				next[0][0] = next[0][0] + this.found[0][d-1-this.bitperStep]*links[0][j]; 
				//sum0 = sum0 + (1- this.notfound[0][d-1-this.bitperStep]); 
				//System.out.println( j + " sum0 " + sum0 + " links " + links[0][j]);
				//System.out.println("f " + f[f.length-1][0]+ " " + f[0][0]);
		}
		
		
		double cur;
		for (int j = 1; j < links.length; j++){
			f = this.getF(d-this.bitperStep, j+1);
			next[0][0] = next[0][0] + links[j][0]*this.found[j][d-1-this.bitperStep];
			for (int i = 1; i < f.length; i++){
				cur = links[j][0]*(1-this.found[j][d-1-this.bitperStep])*(f[i][0] - f[i-1][0]);
				for (int l = i; l < f.length; l++){
					if (1 - f[i-1][1] > 0){
					  next[i][l] = next[i][l]+ cur*(f[l][1]-f[l-1][1])/(1-f[i-1][1]);
					} else {
						 next[i][l] = next[i][l]+ cur;
					}
               }
			}
		}
		
		return next;
	}
	
	public double[][] getF(int d, int links){
		if (links > 1){
		double[][] res  = new double[d][this.beta];
		for (int i = 0; i < d; i++){
			double p = 1-Math.pow(0.5, d-1-i);
			double pA = Math.pow(p, links-this.beta);
			for (int j = this.beta-1; j > -1; j--){
				pA = pA*p;
				res[i][j] = 1- pA;
			}
		}
		for (int i = 0; i < this.beta; i++){
			double p0 = res[0][i];
			for (int j = 0; j < res.length; j++){
				res[j][i] = (res[j][i]-p0)/(1-p0);
			}
		}
		return res;
		}else {
			double[][] res  = new double[d][1];
			double sum = 0;
			for (int i = 1; i < d; i++){
				res[i][0] = Math.pow(0.5, d-1-i);
				sum = sum + res[i][0];
			}
			//System.out.println("f calc last "+res[d-1][0] + " " )
				for (int j = 0; j < res.length; j++){
					res[j][0] = res[j][0];
				}
			
			return res;
		}
	}
	
	private double[][] getExtraLinks(int d){
		double[][] links = new double[this.k][];
		links[0] = new double[this.bitperStep];
		int lose = this.k-this.m;
		for (int j = 1; j < links.length; j++){
			links[j] = new double[1];
		}
		double[][] pot = this.getPotLinks(d);
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				links[0][i] = links[0][i] + pot[j][i]*this.chooseInt[j+lose][0];
			}
		}
		for (int l = 1; l < links.length; l++){
		for (int j = Math.max(l-lose,0); j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				links[l][0] = links[l][0] + pot[j][i]*this.chooseInt[j+lose][l];
			}
		}
		}
//		double sum=0;
//		for (int i = 0; i < links.length; i++){
//			for (int j = 0; j < links[i].length; j++){
//				sum = sum + links[i][j];
//			}
//		}
//		System.out.println(sum + " d= "+d);
		return links;
	}
	
	private double[][] getPotLinks(int d){
		double[][] links = new double[this.m][this.bitperStep];
		double[] pure = new double[this.m];
		double p = Math.pow(1-Math.pow(2, d-this.bitperStep-this.bits),this.n-2);
		for (int i = 0; i < pure.length; i++){
			pure[i] = Calc.binomDist(m-1, i, p);
		}
		for (int i = 0; i < pure.length; i++){
			for (int j = 0; j < this.bitperStep; j++){
				links[i][j] = pure[i]*this.minNext[i][j];
			}
		}
//		double sum=0;
//		for (int i = 0; i < links.length; i++){
//			for (int j = 0; j < links[i].length; j++){
//				sum = sum + links[i][j];
//			}
//		}
//		System.out.println(sum + " d= "+d);
		return links;
	}

	private void setNF(){
		found = new double[k][bits+1];
		for (int j = 0; j < found.length; j++){
		 for (int i = 0; i < found[j].length; i++){
			double p = Math.pow(0.5, this.bits-i);
			for (int x = 0; x <= j; x++){
				found[j][i] = Math.min(found[j][i] + Calc.binomDist(this.n-alpha-1, x, p),1);
			}
			for (int x = j+1; x < this.n-alpha; x++){
				found[j][i] = Math.min(found[j][i] + (j+1)/(double)(x+1)*Calc.binomDist(this.n-alpha-1, x, p),1);
//				if (!(Calc.binomDist(this.n-alpha-1, x, p) <= 1)){
//					System.out.println("x= " + x + " p=" + p + " " + this.notfound[j][i]);
//				}
			}
			//System.out.println("i= " + i + " j=" + j + " res=" + found[j][i]);
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
	
	private double getRT(int x, int links){
		double res = 1;
		for (int i = 0; i < links; i++){
			res = res*(double)(x-i)/(double)(x+1-i);
		}
		return res;
	}
	

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
	
	private void checkNext(double[][] next){
		double p = 0;
		for (int i = 0; i < next.length; i++){
			for (int j = 0; j < next[i].length; j++){
				p = p + next[i][j];
			}
		}
		if (p < 0.9999 || p > 1.001)
		System.out.println(p);
		
	}
	
	private HashMap<Integer, int[]> map(){
		HashMap<Integer, int[]> map = new HashMap<Integer, int[]>(getIndex(1,1,this.bits+1));
		int count;
		for (int j1 = 0; j1 < this.bits+1; j1++){
			for (int j2 = j1; j2 < this.bits+1; j2++){
				for (int j3 = j2; j3 < this.bits+1; j3++){
					count = getIndex(j1,j2,j3);
					map.put(count, new int[]{j1,j2,j3});
				}
			}
		}	
		return map;
	}
	
	public double[][] getTransitionFirstStep(){
		double[][] res = new double[getIndex(1,1,this.bits+1)][this.bits+1];
		for (int d = 0; d < 2+this.bitperStep; d++){
		  res[0][d] = 1;
		}
		for (int d = 2+this.bitperStep; d < this.bits +1; d++){
			double[][][] f = this.getNext3(d);
			//res[0][d] = 1- this.notfound[d-1];
			
			for (int a1 = 0; a1 < f.length; a1++){
				for (int a2 = a1; a2 < f.length; a2++){
					for (int a3=a2; a3 < f.length; a3++){
						int rowC = this.getIndex(a1, a2, a3);
						res[rowC][d] = res[rowC][d] + f[a1][a2][a3];
						
						
					}
				}
			}
		}
		
		return res;
	}
	
	private double[][] getFFirst(int d, int links){
		if (links > 2){
			double[][] res  = new double[d][this.alpha];
			for (int i = 0; i < d; i++){
				double p = 1-Math.pow(0.5, d-1-i);
				double pA = Math.pow(p, links-this.alpha);
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
				//System.out.println("res i ="+  i + " " + res[d-1][i]);
			}
			
			return res;
			}else {
				if (links == 1){
				double[][] res  = new double[d][1];
				double sum = 0;
				for (int i = 0; i < d; i++){
					res[i][0] = Math.pow(0.5, d-1-i);
					sum = sum + res[i][0];
				}
				
					for (int j = 0; j < res.length; j++){
						res[j][0] = (res[j][0]-res[0][0])/(1 - res[0][0]);
					}
					//System.out.println("res 1 link " + res[d-1][0]);
				return res;
				} else {
					double[][] res  = new double[d][2];
					for (int i = 0; i < d; i++){
						double p = 1-Math.pow(0.5, d-1-i);
						double pA = Math.pow(p, links-2);
						for (int j = 2-1; j > -1; j--){
							pA = pA*p;
							res[i][j] = 1- pA;
						}
					}
					for (int i = 0; i < 2; i++){
						double p0 = res[0][i];
						for (int j = 0; j < res.length; j++){
							res[j][i] = (res[j][i]-p0)/(1-p0);
						}
						//System.out.println("res 2 links i ="+  i + " " + res[d-1][i]);
					}
					
					return res;
					
				}
			}
	}
	
	private double[][][] getExtraLinks3(int d){
		double[][][] links = new double[this.k][][];
		links[0] = new double[this.bitperStep][this.bitperStep];
		links[1] = new double[this.bitperStep][1];
		int lose = this.k-this.m;
		for (int j = 2; j < links.length; j++){
			links[j] = new double[1][1];
		}
		double[][][] pot = this.getPotLinks3(d);
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				 for (int l = 0; l < pot[j][i].length; l++){
				links[0][i][l] = links[0][i][l] + pot[j][i][l]*this.chooseInt[j+lose][0];
				 }
			}
		}
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				 for (int l = 0; l < pot[j][i].length; l++){
					if (j + lose >= 1){
				          links[1][i][0] = links[1][i][0] + pot[j][i][l]*this.chooseInt[j+lose][1];
					}
				 }
			}
		}
		for (int l = 2; l < links.length; l++){
		for (int j = Math.max(l-lose,0); j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				for (int s = 0; s < pot[j][i].length; s++){
				  links[l][0][0] = links[l][0][0] + pot[j][i][s]*this.chooseInt[j+lose][l];
				}
			}
		}
		}
//		double s=0;
//		for (int i = 0; i < links.length; i++){
//			for (int j = 0; j < links[i].length; j++){
//				for (int l = 0; l < links[i][j].length; l++){
//					s = s + links[i][j][l];
//				}
//			}
//		}
//		System.out.println("s " + s);
		return links;
	}
	
	private double[][][] getPotLinks3(int d){
		double[][][] links = new double[this.m][this.bitperStep][this.bitperStep];
		double[] pure = new double[this.m];
		double p = Math.pow(1-Math.pow(2, d-this.bitperStep-this.bits),this.n-2);
		double s = 0;
		for (int i = 0; i < pure.length; i++){
			pure[i] = Calc.binomDist(m-1, i, p);
			
			
		}
		for (int i = 0; i < pure.length; i++){
			
//			double sk=0;
//			for (int a1 = 0; a1 < probs.length; a1++){
//				for (int a2=0; a2 < probs.length; a2++){
//					sk = sk + probs[a1][a2];
//				}
//			}
//			System.out.println(sk + " " + sk);
//			double[] sum = new double[this.minNext[i].length];
//			sum[0] = this.minNext[Math.max(i-1,0)][0];
//			for (int o = 1; o < sum.length; o++){
//				sum[o] = sum[o-1] + this.minNext[Math.max(i-1,0)][o];
//			}
			if (i < this.m-2){
             for (int j = 0; j < this.bitperStep; j++){
				//double suml = 0; 
            	 double[][] probs = this.getSecThird(i);
				for (int l = 0; l <= j; l++){
				  links[i][j][l] = pure[i]*probs[j][l];
//				  if (sum[j] > 0){
//					  links[i][j][l] = links[i][j][l]/sum[j];
//				  } else {
//					  System.out.println( "i= " + i + "j= " + j + " " + sum[j]);
//				  }
//				  suml = suml + links[i][j][l]; 
				}
				//System.out.println(suml + "i= " + i + "j= " + j + " " + pure[i]*this.minNext[i][j]);
			}
			} else {
				if (i == this.m-1){
					for (int j = 0; j < this.bitperStep; j++){
						links[i][j][j] = pure[i]*Math.pow(2, j)/(double)(this.m-1);
					}
				}else {
					links[i][0][0] = pure[i];
				}
			}
		}
//		for (int i = 0; i < links.length; i++){
//			for (int j = 0; j < links[i].length; j++){
//				for (int l = 0; l < links[i][j].length; l++){
//					s = s + links[i][j][l];
//				}
//			}
//		}
//		System.out.println("s " + s);
		return links;
	}
	
	public double[][][] getNext3(int d) {
		if (d < 2+this.bitperStep){
			double[][][] next = new double[1][1][1];
			next[0][0][0] = 1;
			return next;
		}
		double[][][] next = new double[d][d][d];
		double[][][] links = this.getExtraLinks3(d-1);
		double[][] f = this.getFFirst(d-this.bitperStep, 1);
		for (int i = 1; i < f.length; i++){
			for (int j = 0; j < Math.min(links[0].length,next[i].length); j++){
				for (int l = 0; l < Math.min(links[0].length,next[i].length); l++){
				next[i][d-1-j][d-1-l] = next[i][d-1-j][d-1-l] + 
						(1-this.found[0][d-1-this.bitperStep])*(f[i][0]-f[i-1][0])*links[0][j][l]; 
				}
			}
		}
		for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			for (int l = 0; l < Math.min(links[0].length,next[0].length); l++){
			next[0][0][0] = next[0][0][0] + this.found[0][d-1-this.bitperStep]*links[0][j][l]; 
			}
		}
//		for (int i = 0; i < next.length; i++){
//			for (int j = 0; j < next[i].length; j++){
//				for (int l = 0; l < next[i][j].length; l++){
//				if (!(next[i][j][l] <= 1))
//				System.out.println(i + " " + j + " " + l +  " " +next[i][j][l]);
//				}
//			}
//		} 
		double cur;
		f = this.getFFirst(d-this.bitperStep, 2);
		for (int i = 1; i < f.length; i++){
			for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			cur = links[1][j][0]*(1-this.found[1][d-1-this.bitperStep])*(f[i][0] - f[i-1][0]);
			for (int l = i; l < f.length; l++){
				if (1 - f[i-1][1] > 0){
				     next[i][l][d-1-j] = next[i][l][d-1-j] + cur*(f[l][1]-f[l-1][1])/(1-f[i-1][1]);
				} else {
					 next[i][l][d-1-j] = next[i][l][d-1-j] + cur;
				}
           }
			}
		}
		for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			//for (int l = 0; l < Math.min(links[0].length,next[0].length); l++){
			next[0][0][0] = next[0][0][0] + this.found[1][d-1-this.bitperStep]*links[1][j][0]; 
			//}
		}
		
		double cur2;
		for (int j = 2; j < links.length; j++){
			f = this.getFFirst(d-this.bitperStep, j+1);
			next[0][0][0] = next[0][0][0] + links[j][0][0]*this.found[j][d-1-this.bitperStep];
			for (int i = 1; i < f.length; i++){
				cur = links[j][0][0]*(1-this.found[j][d-1-this.bitperStep])*(f[i][0] - f[i-1][0]);
				for (int l = i; l < f.length; l++){
					if (1 - f[i-1][1] > 0){
						 cur2 = cur*(f[l][1]-f[l-1][1])/(1-f[i-1][1]);
						} else {
							 cur2 = cur;
						}
					for (int s = l; s < f.length; s++){
						if (1 - f[l-1][2] > 0){
							 next[i][l][s] = next[i][l][s] + cur2*(f[s][2]-f[s-1][2])/(1-f[l-1][2]);
							} else {
								next[i][l][s] = next[i][l][s] + cur2;
							}
					}
               }
			}
		}
//		double s = 0;
//		for (int i = 0; i < next.length; i++){
//			for (int j = 0; j < next[i].length; j++){
//				for (int l = 0; l < next[i][j].length; l++){
//					s = s + next[i][j][l];
//				}
//			}
//		}
//		System.out.println("s " + s + " d=" + d);
		return next;
	}
	
	private double[][] getSecThird(int links){
		double[][] all = new double[this.m-1][this.m-1];
		for (int i = 0; i < all.length; i++){
			for (int j = i+1; j < all.length; j++){
				all[i][j] = this.get2Prob(i, j, links);
				//System.out.println("i= " + i + " j= " + j + " links= " + links + " res=" + all[i][j]);
			}
		}
		double[][] res = new double[this.bitperStep][this.bitperStep];
		for (int i = 0; i < all.length; i++){
			int a = (int)Math.floor(Math.log(i+1)/Math.log(2));
			for (int j = i+1; j < all.length; j++){
				int b = (int)Math.floor(Math.log(j+1)/Math.log(2));
				res[this.bitperStep-1-a][this.bitperStep-1-b] = res[this.bitperStep-1-a][this.bitperStep-1-b] + all[i][j];
			}
		} 
		return res;
	}
	
	private double get2Prob(int i, int j, int links){
		double res = 1;
		for (int l = 0; l <j; l++){
			if (l == i){
				res = res*(m-links)/(double)(m-l);  
			} 
			if (l < i){
				res = res*(links-l)/(double)(m-l);
			}
			if (l > i){
				res = res*(links-l+1)/(double)(m-l);
			}
		}
		res = res*(m-links-1)/(double)(m-j); 
		//System.out.println("i= " + i + " j= " + j + " links= " + links + " res=" + res);
		return res;
	}
	
	private void setDist(){
		this.minNext = new double[m][this.bitperStep];
		for (int j = 0; j < minNext.length-1; j++){
			//double sum = 0;
			for (int i = 0; i < this.bitperStep; i++){
				int better = (int)Math.pow(2, this.bitperStep-1-i)-1;
				//System.out.println("better " + better);
				
				//System.out.println("factor2 " + factor2);
				if (better <= j ){
				this.minNext[j][i] = (double) Calc.binom(j, better)/(double)Calc.binom(m-1, better);
				    if (2*better+1 <= j ){
				    	this.minNext[j][i] = this.minNext[j][i] - (double) Calc.binom(j, 2*better+1)/(double)Calc.binom(m-1, 2*better+1);
				    }
					
				}	
				//sum = sum + this.minNext[j][i];
				//System.out.println(j + " " + i + " " +this.minNext[j][i]);
			}
			//System.out.println("j= " +j + " sum="+sum);
		}	
		this.minNext[m-1][0] = 1;

		this.chooseInt = new double[k][];
		for (int j = 0; j < k; j++){
			this.chooseInt[j] = new double[j+1];
			for (int i = 0; i < j+1; i++){
				this.chooseInt[j][i] = Calc.binomDist(j, i, 1/(double)(m-(j-(k-m))));
			}
			
		}
		
	}

}
