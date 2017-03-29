package parallel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import sun.print.PrinterJobWrapper;

public class Test {
	
	public static enum Algorithm {PARALLELQUICKSORT,PARALLELMERGESORT};
	
	public static class Result {
		int threshold;
		long time;
		
		public Result(int threshold, long time){
			this.threshold = threshold;
			this.time = time;
		}
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
		
		// Just ignore first result
		getTime(listToSort, s);
		for(int i=0;i<times;i++){
			timeStamps.add(getTime(listToSort,s));
		}
		
		long timeSum = 0;
		printToFile("Samples " + times + ", Time in nanosecond to perform sort");
		for(long ts : timeStamps){
			timeSum += ts;
			printToFile(Long.toString(ts));
		}
		
		/*
		// Calculate confidence interval, changed so that we save raw data to file instead
		// We then make the calculations and present the results in Octave instead
		long average = timeSum/timeStamps.size();
		long sum = 0;
		for(long ts : timeStamps){
			//System.out.println("\n" + ts/1000000);
			sum += Math.pow(ts-average,2);
		}
		double SD = Math.sqrt(sum/timeStamps.size());
		System.out.println("\n Average: " + average/1000000f + " ms");
		System.out.println("Standard diviation: " + SD/1000000 + " ms");
		
		double confidencecoefficient = 1.96; // 95%, have to get from z-table
		double temp = confidencecoefficient*(SD/Math.sqrt(timeStamps.size()));
		System.out.println("Test: " + confidencecoefficient);
		System.out.println("Felmarginal: " + temp/1000000 + " ms");
		*/
		return timeSum/timeStamps.size();
	}
	
	// Decide threshold based on specific list, not sure if we need to run several times for average time?
	// around 110 - 120 seem to be correct for 100000 floats in a list
	
	// Tests and finds best threshold for list in interval start to stop, precision(how many executions/test per threshold)
	// How do i send in a strategy to this method, i dont want to know what kind of strat to run test on
	public static Result findBestThreshold(Float[] list, int start, int stop, int precision, int steps, Algorithm a){
		long recordTime = Long.MAX_VALUE;
		int recordThreshold = start;
		
		Strategy<Float> s;
		for(int i = start;i<=stop;i+=steps){
			//System.out.print("\nTesting threshold: " + i);
			printToFile("Threshold: " + i);
			if(a.equals(Algorithm.PARALLELQUICKSORT)){
				s = new Strategy.ParallelQuickSortStrategy<>(Runtime.getRuntime().availableProcessors(),i);
			}else if(a.equals(Algorithm.PARALLELMERGESORT)){
				s = new Strategy.ParallelMergeSortStrategy<>(Runtime.getRuntime().availableProcessors(),i);
			}else{
				//System.out.println("Invalid algorithm to test");
				return null;
			}
			System.gc();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long timeForCurrentThreshold = getAverageTime(list, s, precision);
			if(timeForCurrentThreshold < recordTime){
				recordTime = timeForCurrentThreshold;
				recordThreshold = i;
				//System.out.print(" New Record: " + recordTime/1000000 + " ms");
			}
			//else System.out.print(" Time : " + timeForCurrentThreshold/1000000 + " ms");
		}
		//System.out.println("\n");
		if(recordTime == Long.MAX_VALUE)
			System.out.println("Something went wrong");
		return new Result(recordThreshold, recordTime);
	}
	
	public static void printToFile(String text){
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try{
			fw = new FileWriter("../Lab2/src/testValues/test.txt", true);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
			out.println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null)
				out.close();
		}
	}
}
