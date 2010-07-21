package aeminium.runtime.examples;
public class Tests {
	
	public static int power(int a,int b){
		if(b==1)
			return a;
		else{
			System.out.println("Power: "+b);
			return a*power(a,b-1);
		}
	}
	
	public static void matrixMultiplication(){
		/*  a*b=c */
		int [][] a = {{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2}};
		int [][] b = {{2,2,2,2,2},{2,2,2,2,2},{2,2,2,2,2}};
		int [][] c = new int [a.length][5];
		int i,j,k;
		
		for(i=0;i<a.length;i++){
			for(j=0;j<b[0].length;j++){
				for(k=0;k<a[i].length;k++){
					c[i][j]+=a[i][k]*b[k][j];
				}
			}
			System.out.println("Row "+i+" from matrix A calculated.");
		}
	}
}