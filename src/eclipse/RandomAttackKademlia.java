package eclipse;

import java.util.HashSet;

public class RandomAttackKademlia {
	
	public static double getAttackEfficiency(int benign, int a){
		return 1 - Math.min(1, getExpectedPre(benign,a));
	}
	
	private static double getExpectedPre(int benign, int a){
		double r = 0;
		double[] probs = new double[(benign+1)*(benign+2)/2];
		probs[1] = 1;
		double p = (double)getExpectedOutDegree(benign+a-1)* benign/(double)(Math.pow(benign+a-1,2));
		
		//for (int i = 0; i < benign; i++){
			for (int j = 1; j < benign; j++){
				for (int k = j; k > 0; k--){
					int oldIndex = getIndex(j,k);
					double[] added = newneighbors(benign-j,p);
					for (int h = 0; h < added.length; h++){
						if (j+h < benign){
						int index = getIndex(j+h,k-1+h);
						probs[index] = probs[index] + probs[oldIndex]*added[h];
						} else {
							int index = getIndex(benign,0);
							probs[index] = probs[index] + probs[oldIndex]*added[h];
						}
					}
					probs[oldIndex] = 0;
				}
			}
		//}
			
			for (int j = 1; j <= benign; j++){
				r = r + j*probs[getIndex(j,0)];
			}	
		return r;
	}
	
	public static double getExpectedOutDegree(int nodes){
		double r = 0;
		for (int i = 0; i < 160; i++){
			double sum = 0;
			double p = Math.pow(0.5, i+1);
			for (int j = 0; j < 10; j++){
				sum = sum + Calc.binomDist(nodes, j, p);
				r = r + 1 - sum;
			}
		}
		return r;
	}
	
	private static double[] newneighbors(int left, double p){
		double[] n = new double[left+1];
		for (int i = 0; i < left+1; i++){
			n[i] = Calc.binomDist(left, i, p);
		}
		return n;
	}
	
	private static int getIndex(int a, int b){
		int c = a*(a+1)/2;
		return c + b-1;
	}
	
	public static void checkIndex(int n){
		HashSet<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < n; i++){
			for (int j = 0; j < i+1; j++){
				int index = getIndex(i,j);
				if (set.contains(index)){
					System.out.println("Shit");
				}else {
					set.add(index);
				}
			}
		}
	}

}
