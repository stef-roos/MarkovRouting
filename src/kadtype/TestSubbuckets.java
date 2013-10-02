package kadtype;

import util.Calc;

public class TestSubbuckets {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KadType kad = new KademliaLower(8,8);
		//double[] normal = kad.getRoutingLength(1000);
        kad.setSubbuckets(true);
        kad.setN(100);
        double p = 0;
       int h = 7;
        for (int i = 0; i < h; i++){
        	for (int j = i; j < h; j++){
        		for (int k = j; k < h; k++){
        		   p = p +kad.getProbSubbucketsC3(new int[]{i,j,k}, h, 0);
        		}
        	}
        }
        System.out.println(p);
        //System.out.println(kad.getProbSubbucketsC3(new int[]{4,4,5}, h, 0));
      // System.out.print(kad.getProbSubbucketsC2(new int[]{4,5}, 8, 0));

//        kad.setSuccess(100);
//        double[][] t = kad.getT1(1000);
//        for (int i = 0; i < t[0].length; i++){
//        	double sum = 0;
//        	for (int j = 0; j < t.length; j++){
//        		sum = sum + t[j][i];
//        	}
//        	System.out.println(sum);
//        }
//        double[] sub = kad.getRoutingLength(100);
////        kad.setSubbuckets(false);
////        kad.setLocal(true);
////        double[] local = kad.getRoutingLength(1000);
//        for (int i = 0; i < sub.length; i++){
//        	System.out.println(i + " " + sub[i] );
////        	System.out.println(i + " " + normal[i] + " " + sub[i] + " " + local[i]);
//        }
	}

}
