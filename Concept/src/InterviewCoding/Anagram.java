package InterviewCoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anagram {

	 public List<List<String>> groupAnagrams(String[] strs) {
	        // HashMap to store sorted string as key and list of anagrams as value
	        Map<String, List<String>> anagramGroups = new HashMap<>();

	        // Iterate through each string in the input array
	        for (String str : strs) {
	            // Convert string to character array for sorting
	            char[] charArray = str.toCharArray();

	            // Sort the character array to create a unique key for anagrams
	            Arrays.sort(charArray);

	            // Convert sorted character array back to string to use as key
	            String sortedKey = String.valueOf(charArray);

	            // Add the original string to the corresponding anagram group
	            // If key doesn't exist, create a new ArrayList for this group
	            anagramGroups.computeIfAbsent(sortedKey, key -> new ArrayList<>()).add(str);
	        }

	        // Return all anagram groups as a list of lists
	        return new ArrayList<>(anagramGroups.values());
	    }

}
