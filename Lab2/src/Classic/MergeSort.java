package Classic;

public class MergeSort <T extends Comparable<T>> {
	
	public MergeSort(){
		
	}
	

	public static<T extends Comparable<T>> void sort(T[] list, T[] aux, int low,int high){
		if(high<=low)return;
		int mid = low+(high-low)/2;	
		sort(list,aux,low,mid);
		sort(list,aux,mid+1,high);
		merge(list,aux,low,mid,high);
		
	}


	private static <T extends Comparable<T>>void merge(T[] list, T[] aux, int low, int mid, int high) {
		
		//We prepare the aux list for sort 
		for(int k = low;k<=high;k++){
			aux[k] = list[k];
		}
		//Placeholders for pointers
		int i = low, j = mid+1;
		//sort 
		for(int k = low;k<=high;k++){
			if(i>mid)list[k] = aux[j++];
			else if(j>high)list[k] = aux[i++];
			else if(less(aux[j],aux[i]))list[k] = aux[j++];
			else list[k] = aux[i++];
		}
		
		
	}
	//compare function
	private static <T extends Comparable<T>> boolean less(T aux, T aux2) {
		return aux.compareTo(aux2)<0;
	}

	public static<T extends Comparable<T>> void sort(T[] listToSort){
		
		T[] aux = listToSort.clone();
		sort(listToSort, aux, 0, listToSort.length-1);
	}
	
		

}
