package kademliarouting;

import java.util.Arrays;
import java.util.HashMap;

import Jama.Matrix;
import eclipse.Calc;

public class KademliaUniformUpper {

	int bits;
	int alpha = 3;
	int beta = 2;
	int k;
	int bitperStep;
	int m;
	int n;
	double[][] notfound;
	double[][] F;
	double[][] minNext;
	double[][] chooseInt;
	
	public KademliaUniformUpper(int bits, int k, int nodes){
		this.bits = bits;
		this.k = k;
		this.bitperStep = (int)Math.floor(Math.log(k)/Math.log(2));
		this.m = (int)Math.pow(2, this.bitperStep);
		this.n = nodes;
	}
	
	public double[] getRoutingCDF(){
		double[] done2 = new double[this.bits];
		double[][] t = getTransitionFirstStep();
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
		double[][][] nexts = new double[bits+1][][];
		for (int i = 0; i < nexts.length; i++){
			nexts[i] = getNext(i);
		}
		double[][] next1, next2,next3;
		int indexC,indexR;
		int[] next = new int[6];
		for (int i1 = 0; i1 < this.bits+1; i1++){
			next1 = nexts[i1];
			for (int i2 = i1; i2 < this.bits+1; i2++){
				next2 = nexts[i2];
				for (int i3 = i2; i3 < this.bits+1; i3++){
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
	
	
	
	public double[][] getNext(int d) {
		if (d < 2+this.bitperStep){
			double[][] next = new double[1][1];
			next[0][0] = 1;
			return next;
		}
		double[][] next = new double[d][d];
		
		double px,py,pempty,padd,found,p;
		int lose = this.k-this.m;
		double[][] f;
		//iterate over possible # nodes in bucket
		double sum = 0;
		for (int x = 0; x < this.n-1; x++){
			    px = Calc.binomDist(this.n-2, x, Math.pow(2, d-1-this.bits));
			  //iterate over possible # nodes in 'target subbucket'
			for (int y = 0; y <= x; y++){
				py = Calc.binomDist(x, y, 1/(double)m);
				 //iterate over possible # empty 'subbuckets'
				
				double[] empty = new double[this.m];
				for (int l1 = 0; l1 < Math.pow(2, this.m-1); l1++){
					double[] res = this.getProbSe(l1, x-y);
					empty[(int)res[1]] = empty[(int)res[1]]+res[0];
				}
				for (int l1 = 0; l1 < empty.length; l1++){
					pempty = empty[l1];
					
					 //iterate over possible # additional links in target subbucket
					int extra = Math.max(0,l1+lose-(x-y));
					for (int l2 = extra; l2 <= l1+lose; l2++){
						padd = Calc.binomDist(l1+lose-extra, l2-extra, 1/(double)(this.m-l1));
						
						p = px*py*pempty*padd;
						//sum = sum +p;
						//case success
						if (y <= l2){
							found = 1;
						} else {
							found = (l2+1) * 1/(double)(y+1);
						}
						next[0][0] = next[0][0] + p*found;
						f = getF(d-this.bitperStep,l2+1);
						for (int d1 = 1; d1 < f.length; d1++){
						  if (l2 > 0){
							double cur = f[d1][0]-f[d1-1][0];
							for (int d2 = d1; d2 < f.length; d2++){
								double cur2 = cur*(f[d2][1]-f[d2-1][1]);
								if (f[d1-1][1] < 1){
									cur2 = cur2/(1-f[d1-1][1]);
								}
								next[d1][d2] = next[d1][d2] + p*(1-found)*cur2;
							}
						  } else {
							  for (int i = 0; i < this.bitperStep; i++){
									int better = (int)Math.pow(2, this.bitperStep-1-i)-1;
									if (better <= l1 ){
									double sec = (double) Calc.binom(l1, better)/(double)Calc.binom(m-1, better);
									    if (2*better+1 <= l1 ){
									    	sec = sec - (double) Calc.binom(l1, 2*better+1)/(double)Calc.binom(m-1, 2*better+1);
									    }
									next[d1][d-this.bitperStep+i] =next[d1][d-this.bitperStep+i]+ p*(1-found)*(f[d1][0]-f[d1-1][0])*sec;	
									}
							  } 	
						  }
						}
					}
					
				}
				
			}
		}
		//if (sum < 0.995)
		//System.out.println(sum);
//		for (int i = 0; i < next.length; i++){
//			for (int j = 0; j < next[i].length; j++){
//				sum = sum + next[i][j];
//			}
//		}
//		System.out.println(sum);
		return next;
	}
	
	private double[] getProbSe(int seq, int nodes){
		double count = 0;
		double p = 1;
		for (int i = 0; i < this.m-1; i++){
			int b = seq %2;
			seq = seq/2;
			if (b == 0){
				p = p*(Math.pow(1-1/(double)(this.m-1-i+count), nodes-count));
			} else {
				p = p*(1-Math.pow(1-1/(double)(this.m-1-i+count), nodes-count));
				count++;
			}
		}
		return new double[]{p,this.m-1-count};
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
				for (int j = res.length-1; j >-1; j--){
					res[j][0] = (res[j][0]-res[0][0])/(1-res[0][0]);
				}
			
			return res;
		}
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

	private double[][][] getNext3(int d) {
		if (d < 2+this.bitperStep){
			double[][][] next = new double[1][1][1];
			next[0][0][0] = 1;
			return next;
		}
		double[][][] next = new double[d][d][d];
		
		double px,py,pempty,padd,found,p;
		int lose = this.k-this.m;
		double[][] f;
		//iterate over possible # nodes in bucket
		double sum = 0;
		for (int x = 0; x < this.n-1; x++){
			    
			    px = Calc.binomDist(this.n-2, x, Math.pow(2, d-1-this.bits));
			   //iterate over possible # nodes in 'target subbucket'
			for (int y = 0; y <= x; y++){
				py = Calc.binomDist(x, y, 1/(double)m);
				
					
				
				 //iterate over possible # empty 'subbuckets'
				
				double[] empty = new double[this.m];
				for (int l1 = 0; l1 < Math.pow(2, this.m-1); l1++){
					double[] res = this.getProbSe(l1, x-y);
					empty[(int)res[1]] = empty[(int)res[1]]+res[0];
				}
				for (int l1 = 0; l1 < empty.length; l1++){
					pempty = empty[l1];
					
					 //iterate over possible # additional links in target subbucket
					int extra = Math.max(0,l1+lose-(x-y));
					for (int l2 = extra; l2 <= l1+lose; l2++){
						padd = Calc.binomDist(l1+lose-extra, l2-extra, 1/(double)(this.m-l1));
//						if (y == 0)
//						 System.out.println("d= " + d + " px=" + px + " x=" +x + " bprob=" + Math.pow(2, d-1-this.bits) + 
//								 " py=" + py + " y=" +y + " pempty=" + pempty + " l1=" +l1 + " padd=" + padd + " l2=" +l2); 
//						
						p = px*py*pempty*padd;
						//sum = sum +p;
						//case success
						if (y <= l2){
							found = 1;
						} else {
							found = (l2+1) * 1/(double)(y+1);
						}
//						if (found != 1 && x != 8){
//							System.out.println("d= " + d + " px=" + px + " x=" +x + " bprob=" + Math.pow(2, d-1-this.bits) + 
//									 " py=" + py + " y=" +y + " pempty=" + pempty + " l1=" +l1 + " padd=" + padd + " l2=" +l2); 
//							
//						}
						next[0][0][0] = next[0][0][0] + p*found;
						
						f = getFFirst(d-this.bitperStep,l2+1);
						for (int d1 = 1; d1 < f.length; d1++){
						  if (l2 > 0){
							double cur = f[d1][0]-f[d1-1][0];
							for (int d2 = d1; d2 < f.length; d2++){
								double cur2 = cur*(f[d2][1]-f[d2-1][1]);
								if (f[d1-1][1] < 1){
									cur2 = cur2/(1-f[d1-1][1]);
								}
								if (l2 > 1){
								for (int d3 = d2; d3 < f.length; d3++){
									double cur3 = cur2*(f[d3][2]-f[d3-1][2]);
									if (f[d2-1][2] < 1){
										cur3 = cur3/(1-f[d2-1][2]);
									}
								 next[d1][d2][d3] = next[d1][d2][d3] + p*(1-found)*cur3;
								}
								} else {
									for (int i = 0; i < this.bitperStep; i++){
										int better = (int)Math.pow(2, this.bitperStep-1-i)-1;
										if (better <= l1 ){
										double sec = (double) Calc.binom(l1, better)/(double)Calc.binom(m-1, better);
										    if (2*better+1 <= l1 ){
										    	sec = sec - (double) Calc.binom(l1, 2*better+1)/(double)Calc.binom(m-1, 2*better+1);
										    }
										next[d1][d2][d-this.bitperStep+i] =next[d1][d2][d-this.bitperStep+i]+ p*(1-found)*cur2*sec;	
										}
								  } 
								}
							}
						  } else {
							  if (this.m-2 > l2){
							  double[][] secthr = this.getSecThird(l1);
							  for (int i = 0; i < secthr.length; i++){
								  for (int j = 0; j < secthr[i].length; j++){
									  next[d1][d-1-i][d-1-j] =next[d1][d-1-i][d-1-j]+ p*(1-found)*(f[d1][0]-f[d1-1][0])*secthr[i][j];
								  }
							  }
							  }else {
								  for (int i = 0; i < this.bitperStep; i++){
										int better = (int)Math.pow(2, this.bitperStep-1-i)-1;
										if (better <= l1 ){
										double sec = (double) Calc.binom(l1, better)/(double)Calc.binom(m-1, better);
										    if (2*better+1 <= l1 ){
										    	sec = sec - (double) Calc.binom(l1, 2*better+1)/(double)Calc.binom(m-1, 2*better+1);
										    }
										next[d1][d-this.bitperStep+i][d-this.bitperStep+i] =next[d1][d-this.bitperStep+i][d-this.bitperStep+i]+ p*(1-found)*(f[d1][0]-f[d1-1][0])*sec;	
										}
								  } 
							  }
						  }
						}
					}
					
				}
				
			}
		}
//		sum=0;
//		for (int i = 0; i < next.length; i++){
//			for (int j = 0; j < next[i].length; j++){
//				for (int l = 0; l < next[i][j].length; l++)
//				sum = sum + next[i][j][l];
//			}
//		}
//		System.out.println(sum);
		return next;
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
}
