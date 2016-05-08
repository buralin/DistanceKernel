package org.gradle;

public class DistanceJava {
	 public static void main(String args[]) {
		int spalten =10000;
		int reihen =3;
		float [] in1 = new float [reihen*spalten];
		float [] in2 = new float [reihen];
		float [] b = new float [spalten];
		
		for (int i = 0;i<reihen * spalten ;i++)
		{
				in1[i]=1;	
		}
		javadist(in1,in2,b,reihen,spalten);
		for (int h = 0; h<b.length;h++)
		{
			System.out.println("DISTANCE *********** " + b[h]);
		}
	 }			
		public static void javadist (float [] in1, float [] in2, float [] out, int reihen, int spalten){
			for (int j=0; j < spalten; j++)
			{
					out [j] = (float) Math.sqrt((in1[j]-in2[0])*(in1[j+spalten]-in2[0])+
							(in1[j+spalten]-in2[1])*(in1[j+spalten]-in2[1])+
							(in1[j+2*spalten]-in2[2])*(in1[j+2*spalten]-in2[2]));
			}
     }
}

