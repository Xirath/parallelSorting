package parallel;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

// TODO: It is still 5-10 ms slower than Java:s parallel method (1000000 Elements with 1000 TH)
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
	// Should back away when new pivot is already in right place, otherwise it will lock up
	// consecutive recursive calls
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

	// WORKS now 
	@Override
	protected void compute() {
		// If intervall is less than threshold, sort immediately
		if(high-low < THRESHOLD){
			Arrays.sort(list, low, high+1);
			return;
		// Else split into smaller tasks
		}else{
			boolean test = true;
			do{
				int pivot = partition(list, low, high);
				test = false;
			//System.out.println("Index: " + low + "to " + high + " Decided pivot: " + pivot);
			
			if(pivot != low && pivot != high)
				invokeAll(new SortAction<>(list, low, pivot-1, THRESHOLD),new SortAction<>(list, pivot+1, high, THRESHOLD));
			else if(pivot != low)
				invokeAll(new SortAction<>(list, low, pivot-1, THRESHOLD));
			else if(pivot != high){
				// redo pivot 
				//try{
				
				
				if(high-low < THRESHOLD)
					invokeAll(new SortAction<>(list, pivot+1, high, THRESHOLD));
				else{
					low+=1;
					test = true;
				}
				/*
					if(pivot == low && low != high-1){
						System.out.println("RIP in peaze");
						low+=1;
						test = true;
					}else{
						invokeAll(new SortAction<>(list, pivot+1, high, THRESHOLD));
					}
				/*invokeAll(new SortAction<>(list, pivot+1, high, THRESHOLD));*/
				//}catch(StackOverflowError s){
					//System.out.println("Crash: "+(pivot+1)+" "+high+" "+THRESHOLD);
					//s.printStackTrace();
				//}
			}
			}while(test);
		}
	}
	
	private String printInterval(int from, int to){
		StringBuilder sb = new StringBuilder();
		sb.append("List: ");
		for(int i=from;i<to;i++){
			sb.append(list[i].toString() + ", ");
		}
		return sb.toString();
	}
}