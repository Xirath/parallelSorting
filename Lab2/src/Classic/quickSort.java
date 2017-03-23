package Classic;

// TODO: look into static <T extends Comparable<? super T>>
// do we have to write it in every method? 
// TODO: look into; Somehow seem faster than java:s Arrays.sort 40ms faster 1000000 Element
public final class quickSort{
	
	private quickSort(){};
	
	public static <T extends Comparable<? super T>> void sort(T[] listToSort){
		//System.out.println("Running quicksort");
		//System.out.println("Length of list: " + listToSort.length);
		
		sort(listToSort, 0, listToSort.length-1);
		//System.out.println(less(listToSort[0],listToSort[1]));
	}
	
	private static <T extends Comparable<? super T>> void sort(T[] list, int low, int high){
		if(high <= low)
			return;
		// Get pivot and "split" list into too sublists
		int pivot = partition(list, low, high);
		// Repeat for the sub-lists
		sort(list, low, pivot-1);
		sort(list, pivot+1,high);
	}
	
	// Determine pivot and sort elements to the two sublists (inplace)
	private static <T extends Comparable<? super T>> int partition(T[] list, int low, int high){
		
		int i = low, j = high+1;
		
		while(true){
			while(less(list[++i],list[low]))
				if(i == high) break;
			while(less(list[low],list[--j]))
				if(j == low) break;
			
			if(i >= j)
				break;
			swap(list,i,j);
		}
		swap(list, low, j);
		return j;
	}
	
	private static <T extends Comparable<? super T>> void swap(T[] list, int i, int j){
		T tmp = list[i];
		list[i] = list[j];
		list[j] = tmp;
	}
	
	private static <T extends Comparable<? super T>> boolean less(T a, T b){	
		return a.compareTo(b) < 0;
	}
}
