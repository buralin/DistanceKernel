package org.gradle;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class DistanceCOMMONS {
	 public static void main(String args[]) {
		int n=1200;
		float [] in1 = new float [n];
		float [] in2 = new float [n];
		float [] b = new float [n/3];
		
		for (int i = 0;i<n;i++){
			in1[i]=i+1;
		}
		float a [] = javadist(in1,in2,b,in1.length);
		
		System.out.println("a-Values " + a[0] + " " + a[399]);
		
	 }			
		public static float [] javadist (float [] in1, float [] in2, float [] out, int n){
			float [] a = new float [n] ;
			for (int i=0; i<n; i++){
				a[i] = in1[i]-in2[i]*in1[i]-in2[i];
			}
			for (int j=0; j<n/3; j++){
					out[j] = (float) Math.sqrt(a[j]+a[j+1]+a[j+2]);
			}
			return out;
}
}

