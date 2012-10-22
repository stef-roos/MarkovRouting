package eclipse;

import Jama.Matrix;

public class PastryRouting {
	int L;
	int M;
	int b;
	int nodes;
	double[] found;
	
	public PastryRouting(int L,int M, int b, int nodes){
		this.L = L;
		this.M = M;
		this.b = b;
		this.nodes = nodes;
		this.setNotFound();
	}
	
	public double[] getRoutingCDF(){
		double[] done2 = new double[this.M/this.b];
		Matrix probs2 = new Matrix(getInitial());
		done2[0] = probs2.getArray()[0][0];
		double[][] t = this.getTransitionMatrix();
		Matrix t2 = new Matrix(t);
		for (int i = 1; i < done2.length; i++){
			probs2 = t2.times(probs2);
			done2[i] = probs2.getArray()[0][0];
		}
		return done2;
	}
	
	private double[][] getInitial(){
		double[][] init = new double[this.M+1][1];
		double sum = 0;
		for (int i = 1; i < this.M+1; i++){
			init[i][0] = Math.pow(2, i-this.M+1);
			sum = sum + init[i][0];
		}
		for (int i = 1; i < this.M+1; i++){
			init[i][0] = init[i][0]/sum;
		}
		return init;
	}
	
	private double[][] getTransitionMatrix(){
		double[][] matrix = new double[this.M+1][this.M+1];
		matrix[0][0] = 1;
		matrix[0][1] = 1;
		for (int i = 2; i < matrix.length; i++){
			matrix[0][i] = this.found[i];
			double sum = 0;
			int k = b*((i-1)/b);
			for (int j = 1; j <= k; j++){
				sum = sum + Math.pow(2, j-k);
				matrix[j][i] = (1-this.found[i])*Math.pow(2, j-k);
			}
			for (int j = 1; j <= k; j++){
				matrix[j][i] = matrix[j][i]/sum;
			}
		}
		return matrix;
	}
	
	
	private void setNotFound(){
		this.found = new double[this.M+1];
//        for (int i = 1; i <= M; i++){
//        	double p = Math.pow(2, b*((i-1)/b)-M);
//        	//System.out.println(i+" " + p);
//        	for (int j = 0; j < nodes-2; j++){
//        		//System.out.println();
//        		this.found[i] = this.found[i] + 1/(double)(j+1)*Calc.binomDist(nodes-2, j,p);
//        	}
//        	//System.out.println("Found: " + i+" " + this.found[i]);
//        }
		 for (int i = 1; i <= M; i++){
			 int s = b*((i-1)/b);
	        	double p = Math.pow(2, s-M);
	        	for (int j = 0; j < nodes-1; j++){
	        		 double pj = Calc.binomDist(nodes-2, j,p);
	        		for (int k = 0; k <= i; k++){
	        			double pk = Math.pow(2, k-i-1);
	        			for (int l = 0; l <= s; l++){
	        				double pl = Math.pow(2, l-s-1);
	        				
	        				if (k<= l){
	        					double inter = Math.pow(2, k-l);
	        					for (int c1 = 0; c1 <= j; c1++){
	        						if (c1 < this.L/2){
	        							this.found[i] = this.found[i] + pj*pk*pl*Calc.binomDist(j, c1, inter);
	        						} else {
	        							this.found[i] = this.found[i] + 1/(double)(j+1)*pj*pk*pl*Calc.binomDist(j, c1, inter);
	        						}
	        					}
	        				}else {
	        					double inter = (Math.pow(2, k-M) - Math.pow(2, l-M));
	        					for (int c1 = 0; c1 <= j; c1++){
	        						double pc1 = Calc.binomDist(j, c1, pl);
	        						if (c1 < this.L/2){
	        							for (int c2 = 0; c2 <= this.nodes-2-j; c2++){
	        								double pc2 = Calc.binomDist(this.nodes-2-j, c2, inter);
	        								if (c1 + c2 < this.L/2){
	        									this.found[i] = this.found[i] + pj*pk*pl*pc1*pc2;
	        								} else {
	        									this.found[i] = this.found[i] + 1/(double)(j+1)*pj*pk*pl*pc1*pc2;
	        								}
	        							}
	        							//this.found[i] = this.found[i] + pj*pk*Calc.binomDist(j, c1, pl);
	        						} else {
	        							this.found[i] = this.found[i] + 1/(double)(j+1)*pj*pk*pl*pc1;
	        						}
	        					}
	        				}
	        				
	        			}
	        			
	        		}
	        		//this.found[i] = this.found[i] + 1/(double)(j+1)*Calc.binomDist(nodes-2, j,p);
	        	}
	        	//System.out.println("Found: " + i+" " + this.found[i]);
	        }
	}
	
	private int r(int l, int k){
		int r = l;
		for (int c= l-1; c > k; k++){
			r = r*c;
		}
		return r;
	}
	
	public double failure(double p){
		double[] cdf = this.getRoutingCDF();
		double fail = 0;
		for (int i = 1; i < cdf.length; i++){
			fail = fail + (cdf[i]-cdf[i-1])*(1 - Math.pow(1-p, i));
		}
		return fail;
	}
	
	public double randomAttack(int a){
		double[] cdf = this.getRoutingCDF();
		double fail = 0;
		double p = 1;
		for (int i = 1; i < cdf.length; i++){
			p = p * (double)(nodes-i)/(nodes+a-i);
			fail = fail + (cdf[i]-cdf[i-1])*(1 - p);
		}
		return fail;
	}

}
