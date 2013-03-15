package kadtype;

public class KashmirLower extends Alpha3Beta2Lower {

	public KashmirLower(int b){
		super(b,getBucketSize(b),1);
	}
	
	
	private static int[] getBucketSize(int b){
		int[] ks = new int[b+1];
		for (int i = 0; i < ks.length-4;i++){
			ks[i] = 8;
		}
		ks[ks.length-1] = 128;
		ks[ks.length-2] = 64;
		ks[ks.length-3] = 32;
		ks[ks.length-4] = 16;
		return ks;
	}
}
