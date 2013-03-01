import java.util.Vector;


public class CAN {
	private double side;
	private int dimension; 
	private Vector<double[][]> zones;
	
	public CAN(double side, int d){
		this.side = side;
		this.dimension = d;
		this.zones = new Vector<double[][]>();
	}
	
	public void join(double[] id){
		double[][] z = new double[this.dimension][this.dimension];
		if (this.zones.isEmpty()){
			//first node is responsible for the whole space
			for (int j = 0; j < z.length; j++){
				z[j][0] = 0;
				z[j][1] = this.side;
			}
		} else {
			//find zone where id of new node is in
			double[][] cur;
			for (int i = 0; i < this.zones.size(); i++){
				cur = this.zones.get(i);
				if (this.containsID(cur, id)){
					double length = cur[0][1]-cur[0][0];
					int index = 0;
					for (int j = 1; j < id.length; j++){
						if (cur[j][1]-cur[j][0] >= length){
							index = j;
							break; 
						}
					}
					double middle = (cur[index][0] + cur[index][1])/(double)2;
					//System.out.println(index + " " + middle);
					for (int j = 0; j < id.length; j++){
						if (j != index){
						  z[j][0] = cur[j][0];
						  z[j][1] = cur[j][1];
						}
					}
					if (index == 0){
					z[index][0] = middle;
					z[index][1] = cur[index][1];
					cur[index][1] = middle;
					} else {
						z[index][0] = cur[index][0];
						z[index][1] = middle;
						cur[index][0] = middle;
					}
					
				}
			}
		}
		
		this.zones.add(z);
	}

	
	private boolean containsID(double[][] zone, double[] id){
		for (int i = 0; i < id.length; i++){
			if (zone[i][0] > id[i]){
				return false;
			}
			if (zone[i][1] <= id[i]){
				return false;
			}
		}
		return true;
	}
	
	private double[] getRepID(double[][] zone){
		double[] id = new double[this.dimension];
		for (int i = 0; i < zone.length; i++){
			id[i] = (zone[i][1] + zone[i][0])/(double)2;
		}
		return id;
	}
	
	private Vector<double[][]> getZones(){
		return this.zones;
	}
	
	private Vector<double[]> getRepIds(){
		Vector<double[]> ids = new Vector<double[]>();
		for (int i = 0; i < this.zones.size(); i++){
			ids.add(this.getRepID(zones.get(i)));
		}
		return ids;
	}
	
	public static void main(String[] args){
		double[][] ids = new double[10][2];
//		ids[0][0] = 17; ids[0][1] = 3; 
//		ids[1][0] = 20; ids[1][1] = 10; 
//		ids[2][0] = 15; ids[2][1] = 31; 
//		ids[3][0] = 7; ids[3][1] = 2; 
//		ids[4][0] = 27; ids[4][1] = 5; 
//		ids[5][0] = 2; ids[5][1] = 3; 
//		ids[6][0] = 16; ids[6][1] = 18; 
//		ids[7][0] = 21; ids[7][1] = 19; 
//		ids[8][0] = 25; ids[8][1] = 11; 
//		ids[9][0] = 7; ids[9][1] = 2; 
		
		ids[0][0] = 30; ids[0][1] = 7; 
		ids[1][0] = 1; ids[1][1] = 9; 
		ids[2][0] = 10; ids[2][1] = 26; 
		ids[3][0] = 17; ids[3][1] = 3; 
		ids[4][0] = 30; ids[4][1] = 20; 
		ids[5][0] = 14; ids[5][1] = 5; 
		ids[6][0] = 29; ids[6][1] = 22; 
		ids[7][0] = 13; ids[7][1] = 11; 
		ids[8][0] = 3; ids[8][1] = 14; 
		ids[9][0] = 1; ids[9][1] = 1; 
		
		CAN can = new CAN(32,2);
		String line = "Join ";
		for (int i = 0; i < 10; i++){
			line = line + " & " + (i+1);
		}
		System.out.println(line + "\\\\ \\hline");
		
		for (int j = 0; j < 10; j++){
			line = "" + (j+1);
			can.join(ids[j]);
			Vector<double[]> rep = can.getRepIds();
			for (int i = 0; i < rep.size(); i++){
				double[] id = rep.get(i);
				line = line + " & "+id[0]+"/"+id[1];
			}
			for (int i = rep.size(); i < 10; i++){
				line = line + " & -";
			}
			System.out.println(line + "\\\\");
		}
		
		Vector<double[][]> zones = can.getZones();
		for (int i = 0; i < zones.size(); i++){
			double[][] z = zones.get(i);
			System.out.println("\\item Node "+(i+1)+ " $["+z[0][0]+","+z[0][1]+") \\times ["+z[1][0]+","+z[1][1]+")$");
		}
	}
}
