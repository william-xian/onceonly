package cn.mx.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		int[] arr = init();
		print(arr,dest);
		Scanner sc = new Scanner(System.in);
		List<String> history = new ArrayList<>();
		String line = null;
		int lastSum = 81;
		int thisSum = 0;
		while((line = sc.nextLine()) != null) {
			String[] opt= line.split(" ");
			if(opt.length == 3 && opt[0].matches("h|v") && opt[1].matches("[0-8]{1}")  && opt[2].matches("[0-8]{1}")){
				history.add(line);
				int f = Integer.parseInt(opt[1]);
				int t = Integer.parseInt(opt[2]);
				if(opt[0].equals("v")){
					swapVertical(arr,f,t);
					thisSum = print(arr,dest);
				}else if(opt[0].equals("h")){
					swapHorizontal(arr,f,t);
					thisSum = print(arr,dest);
				}
				System.out.println(String.format("%s  -> %d-%d -> %d",line, lastSum,thisSum,(lastSum-thisSum)));
				lastSum = thisSum;
			}else if(line.equals("exit")){
				break;
			}else if(line.equals("p")){
				for(String o:history){
					System.out.println(o);
				}
			}
		}
		sc.close();
		
	}

	static int[] dest = new int[] {
			0, 0, 0, 8, 0, 0, 0, 0, 0,
			0, 0, 4, 0, 7, 0, 0, 0, 0,
			0, 9, 2, 0, 0, 3, 1, 0, 0,
			3, 1, 0, 0, 0, 6, 8, 7, 0,
			0, 7, 8, 0, 0, 0, 0, 4, 0,
			0, 0, 0, 0, 2, 0, 0, 0, 0,
			0, 3, 0, 0, 0, 0, 0, 1, 0,
			9, 6, 0, 0, 0, 1, 0, 0, 5,
			0, 0, 0, 0, 0, 0, 9, 0, 0,
	};
	
	public static int[] init() {
		int[] param = new int[81];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				param[9*i+j] = (i+j)% 9 +1;	
			}
		}
		return param;
	}
	
	public static int print(int[] param,int[] dest) {
		System.out.print("--|");
		for(int i = 0; i < 9; i++) {
			System.out.print(String.format("---%d--",i));
		}
		System.out.println("-----");
		int[][] notzero = new int[9][2];
		int sum = 0;
		for(int i = 0; i < 9; i++) {
			System.out.print(i+" | ");
			int[] nzc = new int[2];
			for(int j = 0; j < 9; j++) {
				int v = param[9*i+j];
				int d = dest[9*i+j];
				if(d != 0) {
					nzc[0] ++;
					notzero[j][0]++;
					
					if(v != d){
						nzc[1] ++;
						notzero[j][1]++;
						sum++;
						System.out.print(String.format("(%d,%d) ",v , d));
					}else {
						System.out.print(String.format("(%d,%s) ",v ," "));	
					}
				}else {
					System.out.print(String.format("(%d %s) ",v , " "));
				}
			}
			System.out.println(String.format("| %d,%d",nzc[1],nzc[0]));
		}
		System.out.print("--|");
		for(int i = 0; i < 9; i++) {
			System.out.print(String.format("-(%d,%d)",notzero[i][1],notzero[i][0]));
		}
		System.out.println("-----");
		return sum;
	}
	
	public static void swapVertical(int[]arr,int from, int to) {
		for(int i = 0; i < 9; i++) {
			int temp = arr[9*i+from];
			arr[9*i+from] = arr[9*i+to];
			arr[9*i+to] = temp;
		}		
	}
	public static void swapHorizontal(int[]arr,int from, int to) {
		for(int i = 0; i < 9; i++) {
			int temp = arr[9*from+i];
			arr[9*from+i] = arr[9*to+i];
			arr[9*to+i] = temp;
		}
	}
}
