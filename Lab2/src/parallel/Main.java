package parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
	
	// Min dator är för svag för 10^8 tal... java.lang.OutOfMemoryError 10^7 klarar den av
	// Totally killed my laptop by using all memory
	static final int AMOUNT = 10000000;
	// We throw away results from first run
	static final int RUNS = 2;
	static Float[] original = new Float[AMOUNT];
	static Float[] control = new Float[AMOUNT];
	static Float[] listToSort = new Float[AMOUNT];
	
	public static void main(String[] args) {
		
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Cores: " + cores);
		
		//Float[] testar = {10f, 1f, 9f,4f,2f};
		//Arrays.sort(testar, 0, 2);
		//System.out.println(Arrays.toString(testar));
		//getBestThreshold();
		
		fillRandomFloats(original);
		//fillConsecutiveFloats(original);
		control = original.clone();
		Arrays.sort(control);
		//System.out.println(Arrays.toString(original));
		
		// Do not sort the list supplied, note that every number in intervall is 1*ElementsInList*Precision executions
		// This quickly adds up, ex: 0-100 intervall is 100*100000(Elements)*10(10 Loops for every nr in intervall) = 100 Million
		
		int threshold = 10000;
		//int threshold = findBestThreshold(original, 100, 200, 1);
		//System.out.println("Optimal threshold: " + threshold);
		
		
		System.out.println("Control == Original: " + Arrays.equals(control, original));
		listToSort = original.clone();
		
		//Strategy<Float> s = new Strategy.ParallelQuickSortStrategy<>(0, 120);
		//System.out.println("Average time is: " + getAverageTime(listToSort, s, 10)/1000000 + " ms");
		
		Strategy<Float> test;
		test = new Strategy.ParallelQuickSortStrategy<>(0,threshold);
		
		
		long sTime = System.nanoTime();
		//test.execute(listToSort);
		long eTime = System.nanoTime();
		
		System.out.println("Correctly sorted: " + Arrays.equals(control, listToSort));
		System.out.println("Operation took: " + (eTime-sTime)/1000000 + " ms");
		
		listToSort = original.clone();
		sTime = System.nanoTime();
		Arrays.parallelSort(listToSort);
		eTime = System.nanoTime();
		System.out.println("Operation took: " + (eTime-sTime)/1000000 + " ms");
		/*
		
		
		ForkJoinPool pool = new ForkJoinPool();
	
		ArrayList<Double> timeStamps = new ArrayList<>();
		for(int i=0;i<RUNS;i++){
			System.out.println("Run " + i);
			listToSort = original.clone();
			SortAction<Float> test = new SortAction<>(listToSort, 0, listToSort.length-1);
			
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long sTime = System.nanoTime();
			
			pool.invoke(test);
			
			long eTime = System.nanoTime();
			double time = (eTime-sTime)/1000000;
			if(i>=1)
			timeStamps.add(time);
			System.out.println("Took: " + time + " ms");
			if(!Arrays.equals(control, listToSort)){
				System.out.println("Sorting wrong");
				//System.out.println(Arrays.toString(control));
				//System.out.println(Arrays.toString(listToSort));
			}
		}
		double sum = 0;
		for(double time : timeStamps){
			sum += time;
		}
		System.out.println("Average: " + sum/(RUNS-1));
		
		*/
	}
	
	// My guess is that this will vary depending on the lists size
	/*
	private static int getBestThreshold(Float[] list){
		int START = 100, END = 10000;
		Strategy<Float> currentStraregy;
		for(int i = START; i<END; i++){
			currentStraregy = new Strategy.QuickSortStrategy<>();
		}
	}
	*/
	
	public static long getTime(Float[] listToSort, Strategy<Float> s){
		long sTime, eTime;
		// So we don't actually sort the list
		Float[] clone = listToSort.clone();
		System.gc();
		// System.gc & Thread.wait here severly impacts the time it takes to perform tests
		// 50 ms for intervall of 100 and precision of 10 with 100 elements would give several days in 
		// just thread.wait time...
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
	public static int findBestThreshold(Float[] list, int start, int stop, int precision){
		long recordTime = Long.MAX_VALUE;
		int recordThreshold = start;
		Strategy<Float> s;
		
		for(int i = start;i<stop;i++){
			s = new Strategy.ParallelQuickSortStrategy<>(0,i);
			long timeForCurrentThreshold = getAverageTime(list, s, precision);
			if(timeForCurrentThreshold < recordTime){
				recordTime = timeForCurrentThreshold;
				recordThreshold = i;
			}
		}
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
