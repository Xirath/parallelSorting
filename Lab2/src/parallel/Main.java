package parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import parallel.Test.Algorithm;
import parallel.Test.Result;

public class Main {
	
	// My laptop is to old/weak to sort 10^8 numbers, java.lang.OutOfMemoryError can handle 10^7 though
	
	// Nr of elements in array
	static final int AMOUNT = 5000000;
	// Original list, sorted control and the listToSort
	static Float[] original = new Float[AMOUNT];
	static Float[] control = new Float[AMOUNT];
	static Float[] listToSort = new Float[AMOUNT];
	
	private static long sTime = 0, eTime = 0;
	private final static int cores = Runtime.getRuntime().availableProcessors();
	
	public static void main(String[] args) {
		System.out.println("Cores: " + cores);
		
		// Fill the array with floats
		fillRandomFloats(original);
		//fillConsecutiveFloats(original);
		
		// Make a sorted control-list
		control = original.clone();
		Arrays.sort(control);
		
		// Parameters: IntervalStart, IntervalStop, TimesRunForAverage, Steps
		for(Algorithm a : Algorithm.values()){
			Test.printToFile("\n--- Finding threshold for: " + a + "---");
			Result result = Test.findBestThreshold(original, 2, 10, 20, 1, a);
			Result tmp = Test.findBestThreshold(original, 100, 1000, 20, 100, a);
			if(result.time > tmp.time){
				result = tmp;
			}
			tmp = Test.findBestThreshold(original, 1000, 10000, 20, 1000, a);
			if(result.time > tmp.time){
				result = tmp;
			}
			System.out.println("Best threshold for " + a + ": " + result.threshold);
			Test.printToFile("Best threshold for " + a + ": " + result.threshold);			
			
			// When we have decided the threshold to be used, examine the time it takes to sort
			// with different amount of applied cores
			Strategy<Float> test;
			Test.printToFile("\n---Core Tests---");
			for(int i = 1; i<=cores;i++){
				Test.printToFile("\nCores: " + i);
				listToSort = original.clone();
				test = new Strategy.ParallelQuickSortStrategy<>(i, result.threshold);
				System.gc();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long time = Test.getAverageTime(listToSort, test, 20);
				Test.printToFile("Average time: " + time);
				System.out.println("Operation took ("+i+" cores)"+": " + time/1000000 + " ms");
			}
		}
		
		// For comparison to our implementations
		System.out.println("\n Java Implementations");
		Test.printToFile("Java Implementations");
		
		// java.util.Arrays.sort, serial sorting
		Test.printToFile("Serial");
		ArrayList<Long> test = new ArrayList<>();
		for(int i=0;i<20;i++){
			listToSort = original.clone();
			sTime = System.nanoTime();
			Arrays.sort(listToSort);
			eTime = System.nanoTime();
			test.add(eTime-sTime);
			Test.printToFile(Long.toString(eTime-sTime));
		}
		double sum = 0;
		for(Long tmp : test){
			sum += tmp;
		}
		Test.printToFile("Average: " + sum/test.size());
		
		// java.util.Arrays.parallelSort, parallel sorting
		Test.printToFile("\nParallel");
		for(int i=0;i<20;i++){
			listToSort = original.clone();
			sTime = System.nanoTime();
			Arrays.parallelSort(listToSort);
			eTime = System.nanoTime();
			test.add(eTime-sTime);
			Test.printToFile(Long.toString(eTime-sTime));
		}
		sum = 0;
		for(Long tmp : test){
			sum += tmp;
		}
		Test.printToFile("Average: " + sum/test.size());		
	}
	
	
	
	private static void fillRandomFloats(Float[] list){
		
		Random rand = new Random();
		float MIN = 1, MAX = 1000000;
		
		for(int i=0; i<list.length; i++){
			list[i] = rand.nextFloat() * (MAX-MIN) + MIN;
		}
	}
	
	private static void fillConsecutiveFloats(Float[] list){
		for(int i=0; i<list.length; i++){
			list[i] = (float)i*1.0f;
		}
	}

}
