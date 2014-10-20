package se.kth.id1020.minifs;

import java.util.ArrayList;
import java.util.Comparator;

public class QuickSort <T> {
	
	public void quickSort(ArrayList<T> input, int low, int high, Comparator<T> comp)
    {
        if(low >= high)
        	return;
        
        int pivot = partition(input, low, high, comp);
        quickSort(input, low, pivot-1, comp);
        quickSort(input, pivot+1, high, comp);
    }

    private int partition(ArrayList<T> input, int low, int high, Comparator<T> comp)
    {
        int i = low + 1;
        int j = high;
        
        while(i <= j) {
            if(comp.compare(input.get(i), input.get(low)) <= 0) { 
                i++; 
            }
            else if(comp.compare(input.get(j), input.get(low)) > 0) { 
                j--;
            }
            else if(j < i) {
                break;
            }
            else
                exchange(input, i, j);
        }
        exchange(input, low, j);
        return j;
    }

    private void exchange(ArrayList<T> a, int i, int j)
    {
        T tmp = a.get(i);
        a.set(i,a.get(j));
        a.set(j, tmp);
    }
	
}
