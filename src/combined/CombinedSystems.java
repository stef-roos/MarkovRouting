package combined;

import kadtype.Alpha3Beta2Upper2;
import kadtype.KadType;

public class CombinedSystems {
	KadType local;
	KadType global;

	public CombinedSystems(KadType local, KadType global){
		this.local = local;
		this.global = global;
	}
	
	public double[] getRoutingLength(int n){
		local.setSuccess(n);
		global.setSuccess(n);
		double[] dist = local.getI();
		double[] cdf = new double[dist.length];
		cdf[0] = 0;
		double[][]  m = local.getT1(n);
		dist = local.matrixMulti(m,dist);
       cdf[1] = dist[0];
		m = global.getT2(n);
		for (int i = 2; i < cdf.length; i++){
			//System.out.println("step " + i);
			dist = global.matrixMulti(m,dist);
			cdf[i] = dist[0];
		}
		return cdf;
	}
	
}
