package Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapDemo {
	
	
	public static void main(String[] args) {
		
		HashMap<String, Integer> map = new HashMap<>();
		
		// Uses Hashing --  retrieval, insertion, and removal -- of O(1) time & space complexity --- worst case O(N) time. 
		// Unique Key but value can be duplicate
		// No insertion order or alphabetical order maintained
		// Allows 1 null key and multiple null values
		// Not Thread Safe --> multiple threads modifying it concurrently can cause race conditions, data loss, or even infinite loops.
		
		
		// new map created --> 16 buckets (linkedlist or tree map) created --- indexes from 0 - 15 
		
		// 1 bucket --> Node Class with key, value, hashcode and next pointer to handle collisions (linked list or tree structure)
		
		// so when map.put("One", 1) -> hashcode of "One" (KEY) is calculated  --> index = hashcode && 16 (number of buckets) operation
		
		// then the key-value pair with hashcode is stored in the corresponding bucket index. 
		
		
		
		// Entry --> map.put("One", 2) --> hashcode == same --> index == same --> if(key == previous Key Stored)--then value gets updated.
		
		// Entry --> map.put("Two", 2) --> hashcode == same --> index == same --> if(key != previous Key Stored)--> Collision occurs.
		// Collision Resolve --> new entry added --> (linked list/tree) --> reference address stored in next pointer of previous entry
		
		//if (no of entries in bucket/index >> threshold 8 ) = (linked list converted to a balanced tree to optimize search performance.)
	
		//if all 16 buckets are filled and load factor (0.75) is exceeded --> resize() method called -->
		//new bucket array of double size (32) created --> 
		//all existing entries rehashed and redistributed to new buckets based on their hashcodes.
		
		map.put("One", 1);
		map.put("Two", 2);
		map.put("Three", 3);
		map.put("Four", 4);
		map.put("Five", 5);
		map.put(null, null);
		map.put("Four", 44); // Duplicate key, value will be updated
		
		System.out.println("HashMap: " + map);
		System.out.println("Value for key 'Two': " + map.get("Two"));
		
		map.remove("Three");
		System.out.println("HashMap after removing key 'Three': " + map);
		
		//Iterating over HashMap using for each loop
		System.out.println("Iterating over HashMap:");
//		for (String key : map.keySet()) {
//			System.out.println("Key: " + key + ", Value: " + map.get(key));
//		}
		
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
		}
		
		
	}

}
