package parallel;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortPara <T extends Comparable<T>>extends RecursiveAction   {
	private static final long serialVersionUID = 1L;
	private static int threshold;
	private T[] list;
	private T[] aux;
	private int low;
	private int high;

	public MergeSortPara(T[] list2, T[] aux2, int low2, int high2, int threshold2) {
		this.list = list2;
		this.aux = aux2;
		this.high = high2;
		this.low = low2;
		MergeSortPara.threshold = threshold2;
	}

	

	public MergeSortPara() {
		
	}



	@Override
	protected void compute() {
		if (low >= high) { /* base case */
			return;
		} else if (high - low < this.threshold) {
			/* local sort on a single thread */
			Arrays.sort(list, low, high+1);
			return;
		}
		/* Divider of the array */
		int mid = low + (high - low) / 2;
		MergeSortPara<T> left = new MergeSortPara<T>(list, aux, low, mid, threshold);
		MergeSortPara<T> right = new MergeSortPara<T>(list, aux, mid + 1, high, threshold);
		invokeAll(left, right);
		merge(list, aux, low, mid, high);

	}



	private void merge(T[] list2, T[] aux2, int low2, int mid, int high2) {

		// We prepare the aux list for sort
		for (int k = low2; k <= high2; k++) {
			aux2[k] = list2[k];
		}
		// Placeholders for pointers
		int i = low2, j = mid + 1;
		// sort
		for (int k = low2; k <= high2; k++) {
			if (i > mid)
				list2[k] = aux2[j++];
			else if (j > high2)
				list2[k] = aux2[i++];
			else if (less(aux2[j], aux2[i]))
				list2[k] = aux2[j++];
			else
				list2[k] = aux2[i++];
		}

	}

	// Comapare function
	private boolean less(T aux1, T aux2) {
		return (aux1).compareTo(aux2) < 0;
	}

}
