package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExpectedRoutingLength {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getHopFromFile("/home/stef/svns/p2pmodel/results/kadlower1M.txt"));
//		String[] sys = {"kademlia", "kad", "kashmir"};
//		String[] writeSys = {"Kademlia", "KAD", "DBS"};
//		String[] routing = {"", "4-1"};
//		//String[] writeRouting = {"3-2", "4-1"};
//		String[] size = {"100K", "10M"};
//		for (int i = 0; i < 3; i++){
//			String line = writeSys[i];
//			for (int j = 0; j < size.length; j++){
//				for (int k = 0; k < routing.length; k++){
//					line = line + " & " + "(" +round(getHopFromFile("/home/stef/svns/p2pmodel/results/"+sys[i]+routing[k]+"upper"+size[j]+".txt"),2)
//							+ ","+round(getHopFromFile("/home/stef/svns/p2pmodel/results/"+sys[i]+routing[k]+"lower"+size[j]+".txt"),2) + ")";
//				}
//			}
//			System.out.println(line + " \\\\");
//		}

	}

	public static double getHopFromFile(String file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			double exp = 0;
			while ((line = br.readLine()) != null){
				String[] parts = line.split(" ");
				if (parts.length > 1){
					exp = exp + 1 - Double.parseDouble(parts[1]);
				}
			}
			return exp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static double round(double a, int digits){
		int factor = (int) Math.pow(10, digits);
		int r = (int)Math.round(a*factor);
		return r/(double)factor;
	}
}
