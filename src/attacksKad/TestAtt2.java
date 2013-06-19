package attacksKad;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import kadtype.Accuracy;
import kadtype.Alpha3Beta2Upper2;
import kadtype.KADUpper;
import kadtype.KadType;
import kadtype.KadType.LType;

public class TestAtt2 {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int modus = Integer.parseInt(args[0]);
		int k = 10;
		double error = Double.parseDouble(args[1]);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[2]+ ".txt"));
		for (int i = 0; i < 21; i++){
			System.out.println("i="+i );
		int n = (int) ((int)1000*Math.pow(2, i));
		int aimB = 160;
		int b = Accuracy.getBitCount(k, n, error, aimB);
		double exp = 0;
		switch (modus){
		case 1: exp = KADNormalDeg(n,b); break;
		case 2: exp = KAD8050NormalDeg(n,b); break;
		case 3: exp = KAD3bitsNormalDeg(n,b); break;
		case 4: exp = KAD8040NormalDeg(n,b); break;
		case 5: exp = KADFailureDeg(n,b); break;
		case 6: exp = KAD8050FailureDeg(n,b); break;
		case 7: exp = KAD3bitsFailureDeg(n,b); break;
		case 8: exp = KAD8040FailureDeg(n,b); break;
		default: throw new IllegalArgumentException();
		}
		bw.write(n + " " + exp);
		bw.newLine();
			
			bw.flush();
			bw.close();
		}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

//		int modus = Integer.parseInt(args[0]);
//		int k = 10;
//		double error = Double.parseDouble(args[1]);
//		for (int i = 0; i < 21; i++){
//			System.out.println("i="+i );
//		int n = (int) ((int)1000*Math.pow(2, i));
//		int aimB = 160;
//		int b = Accuracy.getBitCount(k, n, error, aimB);
//		double[] cdf = new double[0];
//		switch (modus){
//		case 1: cdf = KADNormal(n,b); break;
//		case 2: cdf = KAD8050Normal(n,b); break;
//		case 3: cdf = KAD3bitsNormal(n,b); break;
//		case 4: cdf = KAD8040Normal(n,b); break;
//		case 5: cdf = KADFailure(n,b); break;
//		case 6: cdf = KAD8050Failure(n,b); break;
//		case 7: cdf = KAD3bitsFailure(n,b); break;
//		case 8: cdf = KAD8040Failure(n,b); break;
//		default: throw new IllegalArgumentException();
//		}
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(args[2]+ n+".txt"));
//			for (int j = 0; j < cdf.length; j++){
//				bw.write(j + " " + cdf[j]);
//				bw.newLine();
//			}
//			bw.flush();
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		}
		
	}
	
	
	public static double[] KADNormal(int n, int b){
		return (new KADUpper(b)).getRoutingLength(n);
	}
	
    public static double[] KAD8050Normal(int n, int b){
		int[] k = new int[b+1];
		k[k.length-1] = 80;
		for (int i = 0; i < b; i++){
			k[i] = 50;
		}
		return (new Alpha3Beta2Upper2(b,k,1)).getRoutingLength(n);
	}

    public static double[] KAD3bitsNormal(int n, int b){
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1; 
		for (int i = 3; i < l.length-1; i++){
			l[i][3] = 1;
		}
		l[2][2] = 1;
		l[1][1] = 1;
		return (new Alpha3Beta2Upper2(b,10,l,LType.ALL)).getRoutingLength(n); 
	}
    
    public static double[] KAD8040Normal(int n, int b){
    	int[] k = new int[b+1];
		k[k.length-1] = 80;
		for (int i = 0; i < b; i++){
			k[i] = 40;
		}
		return (new Alpha3Beta2Upper2(b,k,1)).getRoutingLength(n);
	}
    
    public static double[] KADFailure(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
			k[i] = 8;
		}
		for (int i = Math.max(0, k.length-10); i < k.length; i++){
			k[i] = 9;
		}
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 0.75;
			l[i][4] = 0.25;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		return (new FailureAlpha3Beta2Upper(b,k,l, KadType.LType.ALL,0.1)).getRoutingLength(n);
	}
    
public static double[] KAD8050Failure(int n, int b){
	int[] k = new int[b+1];
	for (int i = 0; i < Math.max(0, k.length-10); i++){
		k[i] = 40;
	}
	for (int i = Math.max(0, k.length-10); i < k.length-1; i++){
		k[i] = 45;
	}
	k[k.length-1] = 72;
	return (new FailureAlpha3Beta2Upper(b,k,1,0.1)).getRoutingLength(n);
	}

    public static double[] KAD3bitsFailure(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
			k[i] = 8;
		}
		for (int i = Math.max(0, k.length-10); i < k.length; i++){
			k[i] = 9;
		}
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 1;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		return (new FailureAlpha3Beta2Upper(b,k,l, KadType.LType.ALL,0.1)).getRoutingLength(n);
	}
    
    public static double[] KAD8040Failure(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
    		k[i] = 32;
    	}
    	for (int i = Math.max(0, k.length-10); i < k.length-1; i++){
    		k[i] = 36;
    	}
    	k[k.length-1] = 72;
    	double[][] l = new double[b+1][b+1];
    	return (new FailureAlpha3Beta2Upper(b,k,1,0.1)).getRoutingLength(n);
	}
    
    public static double KADNormalDeg(int n, int b){
		return (new KADUpper(b)).getExpectedDegree(n);
	}
	
    public static double KAD8050NormalDeg(int n, int b){
		int[] k = new int[b+1];
		k[k.length-1] = 80;
		for (int i = 0; i < b; i++){
			k[i] = 50;
		}
		return (new Alpha3Beta2Upper2(b,k,1)).getExpectedDegree(n);
	}

    public static double KAD3bitsNormalDeg(int n, int b){
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1; 
		for (int i = 3; i < l.length-1; i++){
			l[i][3] = 1;
		}
		l[2][2] = 1;
		l[1][1] = 1;
		return (new Alpha3Beta2Upper2(b,10,l,LType.ALL)).getExpectedDegree(n); 
	}
    
    public static double KAD8040NormalDeg(int n, int b){
    	int[] k = new int[b+1];
		k[k.length-1] = 80;
		for (int i = 0; i < b; i++){
			k[i] = 40;
		}
		return (new Alpha3Beta2Upper2(b,k,1)).getExpectedDegree(n);
	}
    
    public static double KADFailureDeg(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
			k[i] = 8;
		}
		for (int i = Math.max(0, k.length-10); i < k.length; i++){
			k[i] = 9;
		}
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 0.75;
			l[i][4] = 0.25;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		return (new FailureAlpha3Beta2Upper(b,k,l, KadType.LType.ALL,0.1)).getExpectedDegree(n);
	}
    
public static double KAD8050FailureDeg(int n, int b){
	int[] k = new int[b+1];
	for (int i = 0; i < Math.max(0, k.length-10); i++){
		k[i] = 40;
	}
	for (int i = Math.max(0, k.length-10); i < k.length-1; i++){
		k[i] = 45;
	}
	k[k.length-1] = 72;
	return (new FailureAlpha3Beta2Upper(b,k,1,0.1)).getExpectedDegree(n);
	}

    public static double KAD3bitsFailureDeg(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
			k[i] = 8;
		}
		for (int i = Math.max(0, k.length-10); i < k.length; i++){
			k[i] = 9;
		}
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 1;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		return (new FailureAlpha3Beta2Upper(b,k,l, KadType.LType.ALL,0.1)).getExpectedDegree(n);
	}
    
    public static double KAD8040FailureDeg(int n, int b){
    	int[] k = new int[b+1];
    	for (int i = 0; i < Math.max(0, k.length-10); i++){
    		k[i] = 32;
    	}
    	for (int i = Math.max(0, k.length-10); i < k.length-1; i++){
    		k[i] = 36;
    	}
    	k[k.length-1] = 72;
    	double[][] l = new double[b+1][b+1];
    	return (new FailureAlpha3Beta2Upper(b,k,1,0.1)).getExpectedDegree(n);
	}
    
}
