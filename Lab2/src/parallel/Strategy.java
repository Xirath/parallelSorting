package parallel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import Classic.quickSort;

public interface Strategy<T extends Comparable<T>> {
	
	public void execute(T[] listToSort);
	
	public class QuickSortStrategy<T extends Comparable<T>> implements Strategy<T>{
		
		private int threshold = 1000;
		
		public QuickSortStrategy() {}
		
		@Override
		public void execute(T[] listToSort) {
			// Run QuickSort
			quickSort.sort(listToSort);
		}
		
	}
	
	public class ParallelQuickSortStrategy<T extends Comparable<T>> implements Strategy<T>{
		
		ForkJoinPool pool;
		private int threshold = 1000;
		
		// Use default values
		public ParallelQuickSortStrategy(){
			pool = new ForkJoinPool();
		}
		
		// Use default Threshold but specified amount of cores
		public ParallelQuickSortStrategy(int cores) {
			// Set cores in pool?
			pool = new ForkJoinPool();
		}
		
		// Use specified ammounts of cores with a specific threshold
		public ParallelQuickSortStrategy(int cores, int threshold){
			this.threshold = threshold;
			pool = new ForkJoinPool();
		}
		
		@Override
		public void execute(T[] listToSort) {
			// Run parallel QuickSort
			//if(isSorted(listToSort))
			//	return;
			SortAction<T> test = new SortAction<T>(listToSort, 0, listToSort.length-1, threshold);
			pool.invoke(test);
		}
		
		public boolean isSorted(T[] listToSort)
		{     
		    for (int i = 1; i < listToSort.length; i++) {
		        if (listToSort[i-1].compareTo(listToSort[i]) > 0) return false;
		    }

		    return true;
		}
		
	}
	public class ParallelMergeSortStrategy<T extends Comparable<T>> implements Strategy<T>{
		
		ForkJoinPool pool;
		private int threshold = 1000;
		
		// Use default values
		public ParallelMergeSortStrategy(){
			pool = new ForkJoinPool();
		}
		
		// Use default Threshold but specified amount of cores
		public ParallelMergeSortStrategy(int cores) {
			// Set cores in pool?
			pool = new ForkJoinPool();
		}
		
		// Use specified ammounts of cores with a specific threshold
		public ParallelMergeSortStrategy(int cores, int threshold){
			this.threshold = threshold;
			pool = new ForkJoinPool();
		}
		
		@Override
		public void execute(T[] listToSort) {
			// Run parallel QuickSort
			//if(isSorted(listToSort))
			//	return;
			T[] aux = listToSort.clone();
			MergeSortPara<T>test = new MergeSortPara<T>(listToSort,aux, 0, listToSort.length-1, threshold);
			pool.invoke(test);
		}
		
		public boolean isSorted(T[] listToSort)
		{     
		    for (int i = 1; i < listToSort.length; i++) {
		        if (listToSort[i-1].compareTo(listToSort[i]) > 0) return false;
		    }

		    return true;
		}
		
	}
}
