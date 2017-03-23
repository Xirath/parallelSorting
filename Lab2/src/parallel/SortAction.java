package parallel;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class SortAction <T extends Comparable<? super T>> extends RecursiveAction {

	private static final long serialVersionUID = -8459027020365920560L;
	private int THRESHOLD = 1000;
	private T[] list;
	private int low, high;
	
	public SortAction(T[] list, int low, int high, int threshold){
		this.list = list;
		this.low = low;
		this.high = high;
		this.THRESHOLD = threshold;
	}	
		
	/*********************************SPLIT***********************************/
	// Determine pivot and sort elements to the two sublists (inplace)
	private int partition(T[] list, int low, int high){
		
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
	
	private void swap(T[] list, int i, int j){
		T tmp = list[i];
		list[i] = list[j];
		list[j] = tmp;
	}
	
	private boolean less(T a, T b){	
		return a.compareTo(b) < 0;
	}
	/*****************************************************************/

	@Override
	protected void compute() {
		// If intervall is less than threshold, sort immediately
		if(high-low < THRESHOLD){
			Arrays.sort(list, low, high+1);
			return;
		// Else split into smaller tasks
		}else{
			boolean consecutive = true;
			do{
				int pivot = partition(list, low, high);
				consecutive = false;
				
				if(pivot != low && pivot != high)
					invokeAll(new SortAction<>(list, low, pivot-1, THRESHOLD),new SortAction<>(list, pivot+1, high, THRESHOLD));
				else if(pivot != low)
					invokeAll(new SortAction<>(list, low, pivot-1, THRESHOLD));
				else if(pivot != high){
					if(high-low < THRESHOLD)
						invokeAll(new SortAction<>(list, pivot+1, high, THRESHOLD));
					else{
						low+=1;
						consecutive = true;
					}
				}
			}while(consecutive);
		}
	}
}