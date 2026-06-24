package Collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class twoSum {

	public static void main(String[] args) {
		
		int target = 9;
		
		//Sorted Array
		//List<Integer> list = Arrays.asList(2,7,11,15);
		 int [] arr = {2,7,11,15};
		 int sum =0;
		 List<Integer> results = new ArrayList<>();
	        int first = 0;
	        int last = arr.length-1;
	         while(first<last) {
	        	 sum = arr[first] + arr[last];
        		 System.out.println("sum = "+ sum + "Elements :"+ first + last);

        		 if (sum == target) {
	        		 System.out.println("Element = "+ first + last);
	        		 results.add(first);
	        		 results.add(last);
	        	 }
        		 
	        	 if (sum < target) {
	        		 first++;
	        	 } else {
	        		 last--;
	        	 }
	         }
	        
	        System.out.println(results);
	        
	        int rem = 0;
	        
	        Map<Integer,Integer> map = new HashMap<>();
	        for (int i =0; i<=arr.length-1;i++) {
	        	rem = arr[i]-target;
	        	
	        	if (map.containsKey(rem)) {
	        		System.out.println("true --> "+ rem);
	        	}
	        	
	        	map.put(arr[i], i);	
	        }
	}
}
