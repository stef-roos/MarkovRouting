package kademliarouting;

import java.util.Arrays;

import Jama.Matrix;
import eclipse.Calc;

public class KademliaBUniform {
	
	int bits;
	int alpha = 3;
	int beta = 2;
	int[] k;
	int[] bitperStep;
	int[] m;
	int n;
	double[][] notfound;
	double[][] F;
	double[][][] minNext;
	double[][][] chooseInt;
	
	public KademliaBUniform(int bits, int[] k, int nodes){
		this.bits = bits;
		this.k = k;
		this.bitperStep = new int[k.length];
		this.m = new int[k.length];
		for (int i = 0; i < k.length; i++){
		 this.bitperStep[i] = (int)Math.floor(Math.log(k[i])/Math.log(2));
		 this.m[i] = (int)Math.pow(2, this.bitperStep[i]);
		}
		this.n = nodes;
		this.setNF();
		this.setDist();
		//this.setF();
	}
	
	public KademliaBUniform(int bits, int nodes){
		this(bits, makeK(bits),nodes);
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
	
	/**
	 * cdf of routing length
	 * @return
	 */
	public double[] getRoutingCDF(){
		double[] done2 = new double[this.bits];
		double[][] t = getTransitionFirstStep();
//		for (int j = 0; j < t[0].length; j++){
//			double s = 0;
//			for (int i = 0; i < t.length; i++){
//				s = s + t[i][j];
//			}
//			System.out.println(s);
//		}
		Matrix t2 = new Matrix(t);
		Matrix probs2 = new Matrix(getInitial());
		done2[0] = probs2.getArray()[0][0];
		probs2 = t2.times(probs2);
		done2[1] = probs2.getArray()[0][0];
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
	double[][] getInitial() {
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
	
	
	/**
	 * get distribution of next beta contacts assuming current contact is at bit distance d
	 * @param d
	 * @return
	 */
	public double[][] getNext(int d) {
		if (d < 2+this.bitperStep[0]){
			double[][] next = new double[1][1];
			next[0][0] = 1;
			return next;
		}
		double[][] next = new double[d][d];
		//distribution of additional links into target region
		double[][] links = this.getExtraLinks(d-1);
		double[][] f = this.getF(d-this.bitperStep[d], 1);
		double sum0 = 0;
		//case: 0 links
			for (int j = 0; j < links[0].length; j++){
				sum0 = 0;
				for (int i = 1; i < f.length; i++){
					//distribution P(first)*P(second|first)
				next[i][d-1-j] = next[i][d-1-j] + 
						this.notfound[0][d-1-this.bitperStep[d]]*(f[i][0]-f[i-1][0])*links[0][j]; 
				//sum0 = sum0 + (f[i][0]-f[i-1][0]);
			}
				//case: found
				next[0][0] = next[0][0] + (1- this.notfound[0][d-1-this.bitperStep[d]])*links[0][j]; 
				//sum0 = sum0 + (1- this.notfound[0][d-1-this.bitperStep]); 
				//System.out.println( j + " sum0 " + sum0 + " links " + links[0][j]);
				//System.out.println("f " + f[f.length-1][0]+ " " + f[0][0]);
		}
		
		//case links > 0
		double cur;
		for (int j = 1; j < links.length; j++){
			f = this.getF(d-this.bitperStep[d], j+1);
			//case: found
			next[0][0] = next[0][0] + links[j][0]*(1-this.notfound[j][d-1-this.bitperStep[d]]);
			for (int i = 1; i < f.length; i++){
				//case: not found
				cur = links[j][0]*this.notfound[j][d-1-this.bitperStep[d]]*(f[i][0] - f[i-1][0]);
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
		double[][] links = new double[this.k[d]][];
		links[0] = new double[this.bitperStep[d]];
		int lose = this.k[d]-this.m[d];
		for (int j = 1; j < links.length; j++){
			links[j] = new double[1];
		}
		double[][] pot = this.getPotLinks(d);
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				links[0][i] = links[0][i] + pot[j][i]*this.chooseInt[d][j+lose][0];
			}
		}
		for (int l = 1; l < links.length; l++){
		for (int j = Math.max(l-lose,0); j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				links[l][0] = links[l][0] + pot[j][i]*this.chooseInt[d][j+lose][l];
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
		double[][] links = new double[this.m[d]][this.bitperStep[d]];
		double[] pure = new double[this.m[d]];
		double p = Math.pow(1-Math.pow(2, d-this.bitperStep[d]-this.bits),this.n-2);
		for (int i = 0; i < pure.length; i++){
			pure[i] = Calc.binomDist(m[d]-1, i, p);
		}
		for (int i = 0; i < pure.length; i++){
			for (int j = 0; j < this.bitperStep[d]; j++){
				links[i][j] = pure[i]*this.minNext[d][i][j];
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

	/**
	 * probability not to reach target
	 */
	private void setNF(){
		notfound = new double[k[k.length-1]][bits+1];
		for (int j = 0; j < notfound.length; j++){
		 for (int i = 0; i < notfound[j].length; i++){
			double p = Math.pow(0.5, this.bits-i);
			for (int x = j+1; x < this.n-alpha; x++){
				notfound[j][i] = Math.min(notfound[j][i] + getRT(x,j+1)*Calc.binomDist(this.n-alpha-1, x, p),1);
//				if (!(Calc.binomDist(this.n-alpha-1, x, p) <= 1)){
				
			}
		 }
		}
//		for (int i = 0; i < this.notfound[0].length-3; i++){
//			System.out.println("Compar " + i + " " +this.notfound[0][i] + " " + this.notfound[k-1][i+3] + " p*n=" +Math.pow(0.5, this.bits-i)*n);
//		}
	}
	

	/**
	 * probability that links lead to x nodes of x+1
	 * @param x
	 * @param links
	 * @return
	 */
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
	
	
	/**
	 * get transition matrix for first step
	 * @return
	 */
	public double[][] getTransitionFirstStep(){
		double[][] res = new double[getIndex(1,1,this.bits+1)][this.bits+1];
		//when close enough => target reached in any case
		for (int d = 0; d < 2+this.bitperStep[0]; d++){
		  res[0][d] = 1;
		}
		//iterate over all possible distances
		for (int d = 2+this.bitperStep[0]; d < this.bits +1; d++){
			double[][][] f = this.getNext3(d);
			//iterate over all possible distances of alpha closest contacts
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
	
	/**
	 * get F of first step, when x links go into selected region
	 * @param d
	 * @param links
	 * @return
	 */
	private double[][] getFFirst(int d, int links){
		if (links > 2){
			//as in normal kademlia: replace k with links
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
					//only one link into region = only first part of distribution
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
					//only dist of 2 links
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
	
	/**
	 * get distribution of number of links additional to the one guaranteed in region
	 * => three-dim array: number of links in region 
	 * x distance of closest link not in target region (only necessary if links < 2)
	 * x distance of second closest link not in target region (only necessary if links = 0 )
	 * @param d
	 * @return
	 */
	private double[][][] getExtraLinks3(int d){
		double[][][] links = new double[this.k[d]][][];
		links[0] = new double[this.bitperStep[d]][this.bitperStep[d]];
		links[1] = new double[this.bitperStep[d]][1];
		//number of links not assigned to any region
		int lose = this.k[d]-this.m[d];
		for (int j = 2; j < links.length; j++){
			links[j] = new double[1][1];
		}
		//distribution of number of links initially reserved for empty regions
		double[][][] pot = this.getPotLinks3(d);
		
		//case links=0
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				 for (int l = 0; l < pot[j][i].length; l++){
				links[0][i][l] = links[0][i][l] + pot[j][i][l]*this.chooseInt[d][j+lose][0];
				 }
			}
		}
		//case links=1
		for (int j = 0; j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				 for (int l = 0; l < pot[j][i].length; l++){
					if (j + lose >= 1){
				          links[1][i][0] = links[1][i][0] + pot[j][i][l]*this.chooseInt[d][j+lose][1];
					}
				 }
			}
		}
		//case links > 1
		for (int l = 2; l < links.length; l++){
		for (int j = Math.max(l-lose,0); j < pot.length; j++){
			for (int i = 0; i < pot[j].length; i++){
				for (int s = 0; s < pot[j][i].length; s++){
				  links[l][0][0] = links[l][0][0] + pot[j][i][s]*this.chooseInt[d][j+lose][l];
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
	
	/**
	 * distribution of links going to empty region for 3 contacts
	 *  => three-dim array: number of links in reserved for empty region 
	 * x distance of closest link not in target region (only necessary if links < 2)
	 * x distance of second closest link not in target region (only necessary if links = 0 )
	 * @param d
	 * @return
	 */
	private double[][][] getPotLinks3(int d){
		double[][][] links = new double[this.m[d]][this.bitperStep[d]][this.bitperStep[d]];
		double[] pure = new double[this.m[d]];
		double p = Math.pow(1-Math.pow(2, d-this.bitperStep[d]-this.bits),this.n-2);
		double s = 0;
		for (int i = 0; i < pure.length; i++){
			pure[i] = Calc.binomDist(m[d]-1, i, p);
			
			
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
			if (i < this.m[d]-2){
             for (int j = 0; j < this.bitperStep[d]; j++){
				//double suml = 0; 
            	 double[][] probs = this.getSecThird(i,d);
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
				if (i == this.m[d]-1){
					for (int j = 0; j < this.bitperStep[d]; j++){
						links[i][j][j] = pure[i]*Math.pow(2, j)/(double)(this.m[d]-1);
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
	
	/**
	 * get distribution for next alpha contacts
	 * @param d
	 * @return
	 */
	public double[][][] getNext3(int d) {
		//close enough => done
		if (d < 2+this.bitperStep[0]){
			double[][][] next = new double[1][1][1];
			next[0][0][0] = 1;
			return next;
		}
		double[][][] next = new double[d][d][d];
		//distribution over number of links
		double[][][] links = this.getExtraLinks3(d-1);
		double[][] f = this.getFFirst(d-this.bitperStep[d], 1);
		
		//case: links = 0
		  //case: not found
		for (int i = 1; i < f.length; i++){
			for (int j = 0; j < Math.min(links[0].length,next[i].length); j++){
				for (int l = 0; l < Math.min(links[0].length,next[i].length); l++){
					//take distribution over second and third, compute that over first
				next[i][d-1-j][d-1-l] = next[i][d-1-j][d-1-l] + 
						this.notfound[0][d-1-this.bitperStep[d]]*(f[i][0]-f[i-1][0])*links[0][j][l]; 
				}
			}
		}
		 //case found, add all possibilities
		for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			for (int l = 0; l < Math.min(links[0].length,next[0].length); l++){
			next[0][0][0] = next[0][0][0] + (1- this.notfound[0][d-1-this.bitperStep[d]])*links[0][j][l]; 
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
		//case links = 1
		double cur;
		f = this.getFFirst(d-this.bitperStep[d], 2);
		for (int i = 1; i < f.length; i++){
			//iterate over all poss third contacts (= not in region)
			for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			cur = links[1][j][0]*this.notfound[1][d-1-this.bitperStep[d]]*(f[i][0] - f[i-1][0]);
			for (int l = i; l < f.length; l++){
				if (1 - f[i-1][1] > 0){
				     next[i][l][d-1-j] = next[i][l][d-1-j] + cur*(f[l][1]-f[l-1][1])/(1-f[i-1][1]);
				} else {
					 next[i][l][d-1-j] = next[i][l][d-1-j] + cur;
				}
           }
			}
		}
		//case: found destination
		for (int j = 0; j < Math.min(links[0].length,next[0].length); j++){
			//for (int l = 0; l < Math.min(links[0].length,next[0].length); l++){
			next[0][0][0] = next[0][0][0] + (1- this.notfound[1][d-1-this.bitperStep[d]])*links[1][j][0]; 
			//}
		}
		
		
		//case links > 1
		double cur2;
		for (int j = 2; j < links.length; j++){
			f = this.getFFirst(d-this.bitperStep[d], j+1);
			next[0][0][0] = next[0][0][0] + links[j][0][0]*(1-this.notfound[j][d-1-this.bitperStep[d]]);
			for (int i = 1; i < f.length; i++){
				//dist of first links
				cur = links[j][0][0]*this.notfound[j][d-1-this.bitperStep[d]]*(f[i][0] - f[i-1][0]);
				for (int l = i; l < f.length; l++){
					if (1 - f[i-1][1] > 0){
						//*(dist of second | first)
						 cur2 = cur*(f[l][1]-f[l-1][1])/(1-f[i-1][1]);
						} else {
							 cur2 = cur;
						}
					for (int s = l; s < f.length; s++){
						if (1 - f[l-1][2] > 0){
							//*(dist of third | first and second)
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
	
	/**
	 * distribution of first two links not in target region
	 * @param links
	 * @return
	 */
	private double[][] getSecThird(int links, int d){
		double[][] all = new double[this.m[d]-1][this.m[d]-1];
		for (int i = 0; i < all.length; i++){
			for (int j = i+1; j < all.length; j++){
				all[i][j] = this.get2Prob(i, j, links,m[d]);
				//System.out.println("i= " + i + " j= " + j + " links= " + links + " res=" + all[i][j]);
			}
		}
		double[][] res = new double[this.bitperStep[d]][this.bitperStep[d]];
		for (int i = 0; i < all.length; i++){
			int a = (int)Math.floor(Math.log(i+1)/Math.log(2));
			for (int j = i+1; j < all.length; j++){
				int b = (int)Math.floor(Math.log(j+1)/Math.log(2));
				res[this.bitperStep[d]-1-a][this.bitperStep[d]-1-b] = res[this.bitperStep[d]-1-a][this.bitperStep[d]-1-b] + all[i][j];
			}
		} 
		return res;
	}
	
	/**
	 * prob that i and j are positions of closest links
	 * @param i
	 * @param j
	 * @param links
	 * @return
	 */
	private double get2Prob(int i, int j, int links, int r){
		double res = 1;
		for (int l = 0; l <j; l++){
			if (l == i){
				res = res*(r-links)/(double)(r-l);  
			} 
			if (l < i){
				res = res*(links-l)/(double)(r-l);
			}
			if (l > i){
				res = res*(links-l+1)/(double)(r-l);
			}
		}
		res = res*(r-links-1)/(double)(r-j); 
		//System.out.println("i= " + i + " j= " + j + " links= " + links + " res=" + res);
		return res;
	}
	
	/**
	 * initialization
	 * set minNext
	 * number of links in target region x distance of first link not in target region
	 * set chooseInt
	 * number of free links x number of links in target region 
	 */
	private void setDist(){
		this.minNext = new double[this.bits+1][][];
		for (int x = 0; x< minNext.length; x++){
			this.minNext[x] = new double[m[x]][this.bitperStep[x]];
		for (int j = 0; j < minNext[x].length-1; j++){
			
			//double sum = 0;
			for (int i = 0; i < this.bitperStep[x]; i++){
				int better = (int)Math.pow(2, this.bitperStep[x]-1-i)-1;
				//System.out.println("better " + better);
				
				//System.out.println("factor2 " + factor2);
				if (better <= j ){
				this.minNext[x][j][i] = (double) Calc.binom(j, better)/(double)Calc.binom(m[x]-1, better);
				    if (2*better+1 <= j ){
				    	this.minNext[x][j][i] = this.minNext[x][j][i] - (double) Calc.binom(j, 2*better+1)/(double)Calc.binom(m[x]-1, 2*better+1);
				    }
					
				}	
				//sum = sum + this.minNext[j][i];
				//System.out.println(j + " " + i + " " +this.minNext[j][i]);
			}
			//System.out.println("j= " +j + " sum="+sum);
		}	
		this.minNext[x][m[x]-1][0] = 1;
		}

		this.chooseInt = new double[bits+1][][];
		for (int x = 0; x < this.chooseInt.length; x++){
			this.chooseInt[x] = new double[this.k[x]][];
		for (int j = 0; j < k[x]; j++){
			this.chooseInt[x][j] = new double[j+1];
			for (int i = 0; i < j+1; i++){
				this.chooseInt[x][j][i] = Calc.binomDist(j, i, 1/(double)(m[x]-(j-(k[x]-m[x]))));
			}
			
		}
		}
		
	}

}
