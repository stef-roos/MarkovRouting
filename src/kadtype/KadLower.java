package kadtype;

public abstract class KadLower extends KadTypeCDFs {

	public KadLower(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadLower(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	protected void makeDistinct(int[][] returned, double[][] t, int index, int d1, int n){
		int[][] max = new int[d1][2];
		for (int i=0; i < d1; i++){
			for (int j=0; j < this.alpha; j++){
				int count = 0;
				for (int m=0; m < this.beta; m++){
					if (returned[j][m] == i){
						count++;
					}
				}
				if (count > max[i][1]){
					max[i][0] = j;
					max[i][1] = count;
				}
			}
		}
		
	}
	
	protected void recusivecombine(int[][] returned, double[][] t, int index, 
			int a, int c, int n, int[][] max, boolean[][] contain, double p){
		while (a < this.alpha && (returned[a][c] > max.length-1 || max[returned[a][c]][0] == a)){
			if (max[returned[a][c]][0] == a){
				contain[a][c] = true;
			}else {
				contain[a][c] = false;
			}
			a = (a*this.alpha+c+1)/this.alpha;
			c = (a*this.alpha+c+1) % this.alpha;
		}
		if (a < this.alpha){
			//compute p'
			
		}else {
			//Evaluation
			int[][] combi = new int[this.alpha][this.beta];
		}
		
	}

}
