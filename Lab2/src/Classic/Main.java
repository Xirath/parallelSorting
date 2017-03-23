package Classic;

import java.util.Arrays;
import java.util.Random;

// Mby we need to declare time variables before(takes time to allocate memory? or just takes the same?)
public class Main {
	
	static final int AMOUNT = 1000000;
	static Float[] original = new Float[AMOUNT];
	static Float[] control = new Float[AMOUNT];
	static Float[] listToSort = new Float[AMOUNT];
	
	
	public static void main(String[] args) {
	
		fillRandomFloats(original);
		control = original.clone();
		Arrays.sort(control);
		
		System.out.println("Control == Original: " + Arrays.equals(control, original));
		
		for(int i=0;i<20;i++){
			System.out.println("Run " + i);
			listToSort = original.clone();
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			quickSort(listToSort);
			if(!Arrays.equals(control, listToSort)){
				System.out.println("Sorting wrong");
				System.out.println(Arrays.toString(control));
				System.out.println(Arrays.toString(listToSort));
			}
		}
		
		for(int i=0;i<20;i++){
			System.out.println("Run " + i);
			listToSort = original.clone();
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long sTime = System.nanoTime();
			Arrays.sort(listToSort);
			long eTime = System.nanoTime();
			double time = (eTime-sTime)/1000000;
			System.out.println("Took: " + time + " ms");
			if(!Arrays.equals(control, listToSort)){
				System.out.println("Sorting wrong");
				System.out.println(Arrays.toString(control));
				System.out.println(Arrays.toString(listToSort));
			}
		}
		
		for(int i=0;i<20;i++){
			System.out.println("Run " + i);
			listToSort = original.clone();
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long sTime = System.nanoTime();
			Arrays.parallelSort(listToSort);
			long eTime = System.nanoTime();
			double time = (eTime-sTime)/1000000;
			System.out.println("Took: " + time + " ms");
			if(!Arrays.equals(control, listToSort)){
				System.out.println("Sorting wrong");
				System.out.println(Arrays.toString(control));
				System.out.println(Arrays.toString(listToSort));
			}
		}
		
		/*
		copy = listToSort;
		System.gc();
		sTime = System.nanoTime();
		Arrays.sort(copy);
		eTime = System.nanoTime();
		time = (eTime-sTime)/1000000;
		System.out.println("Took: " + time + " ms");
		
		copy = listToSort;
		System.gc();
		sTime = System.nanoTime();
		Arrays.parallelSort(copy);
		eTime = System.nanoTime();
		time = (eTime-sTime)/1000000;
		System.out.println("Took: " + time + " ms");
		*/
		
		// int cores = Runtime.getRuntime().availableProcessors()
	}
	
	private static void quickSort(Float[] listToSort){
		long sTime = System.nanoTime();
		quickSort.sort(listToSort);
		long eTime = System.nanoTime();
		double time = (eTime-sTime)/1000000;
		System.out.println("Took: " + time + " ms");
	}
	
	private static void fillRandomFloats(Float[] list){
		
		Random rand = new Random();
		float MIN = 1, MAX = 1000000;
		
		for(int i=0; i<list.length; i++){
			list[i] = rand.nextFloat() * (MAX-MIN) + MIN;
		}
	}

}
