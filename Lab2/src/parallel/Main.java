package parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
	
	// My laptop is to old/weak to sort 10^8 numbers, java.lang.OutOfMemoryError can handle 10^7 though
	
	// Nr of elements in array
	static final int AMOUNT = 10000;
	// Original list, sorted control and the listToSort
	static Float[] original = new Float[AMOUNT];
	static Float[] control = new Float[AMOUNT];
	static Float[] listToSort = new Float[AMOUNT];
	
	public static void main(String[] args) {
		
		// TODO: implement some functions to vary the applied cores
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Cores: " + cores);
		
		// Fill the array with floats
		//fillRandomFloats(original);
		fillConsecutiveFloats(original);
		
		// Make a sorted control-list
		control = original.clone();
		Arrays.sort(control);
		
		// Find the best threshold (Note: quickly adds up to alot of operations)
		// After a while it seem constant regardless the threshold
		//int threshold = 100;
		int threshold = findBestThreshold(original, 100, 200, 15, 10);
		System.out.println("Optimal threshold: " + threshold);
		
		// Clone original to the array to be sorted
		listToSort = original.clone();
		
		Strategy<Float> test;
		test = new Strategy.ParallelQuickSortStrategy<>(0,threshold);
		
		long sTime = System.nanoTime();
		test.execute(listToSort);
		long eTime = System.nanoTime();
		
		System.out.println("Correctly sorted: " + Arrays.equals(control, listToSort));
		System.out.println("Operation took: " + (eTime-sTime)/1000000 + " ms");
		
		listToSort = original.clone();
		sTime = System.nanoTime();
		Arrays.parallelSort(listToSort);
		eTime = System.nanoTime();
		System.out.println("Operation took: " + (eTime-sTime)/1000000 + " ms");
		
	}
	
	public static long getTime(Float[] listToSort, Strategy<Float> s){
		long sTime, eTime;
		// So we don't actually sort the list
		Float[] clone = listToSort.clone();
		sTime = System.nanoTime();
		s.execute(clone);
		eTime = System.nanoTime();
		return eTime-sTime;
	}
	
	public static long getAverageTime(Float[] listToSort, Strategy<Float> s, int times){
		
		ArrayList<Long> timeStamps = new ArrayList<>();
		long timeSum = 0;
		// Just ignore first result
		getTime(listToSort, s);
		for(int i=0;i<times;i++){
			timeStamps.add(getTime(listToSort,s));
		}
		
		for(long ts : timeStamps){
			//System.out.println("Control: " +  ts/1000000 + " ms");
			timeSum += ts;
		}
		
		return timeSum/timeStamps.size();
	}
	
	// Decide threshold based on specific list, not sure if we need to run several times for average time?
	// around 110 - 120 seem to be correct for 100000 floats in a list
	
	// Tests and finds best threshold for list in intervall start to stop, precision(how many executions/test per threshold)
	public static int findBestThreshold(Float[] list, int start, int stop, int precision, int steps){
		long recordTime = Long.MAX_VALUE;
		int recordThreshold = start;
		Strategy<Float> s;
		
		for(int i = start;i<=stop;i+=steps){
			System.out.print("\nTesting threshold: " + i);
			s = new Strategy.ParallelQuickSortStrategy<>(0,i);
			System.gc();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long timeForCurrentThreshold = getAverageTime(list, s, precision);
			if(timeForCurrentThreshold < recordTime){
				recordTime = timeForCurrentThreshold;
				recordThreshold = i;
				System.out.print(" New Record: " + recordTime/100000 + " ms");
			}else
			System.out.print(" Time : " + timeForCurrentThreshold/100000 + " ms");
		}
		System.out.println("\n");
		if(recordTime == Long.MAX_VALUE)
			System.out.println("Something went wrong");
		
		return recordThreshold;
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
