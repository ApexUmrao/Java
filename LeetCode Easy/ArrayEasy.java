package DSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ArrayEasy {


    public static void main (String [] args){
        int [] nums = {1,2,2,9,110,0};
        int nums1[] = {-2,-3,4,-1,-2, 1, 5,-3};
        int k =2;
        // System.out.println(maxElement(nums));
        // System.out.println(minElement(nums));
        // System.out.println(sumElement(nums));
        // System.out.println(evenOdd(nums));
        // System.out.println(Arrays.toString(reverse(nums)));
        // System.out.println(Arrays.toString(alternate(nums)));
        // System.out.println(secondMax(nums));
        // System.out.println(secondMin(nums));
        // System.out.println(BinarySearch(nums,5));
        // System.out.println(isSorted(nums));
        System.out.println(Arrays.toString(reverseArrayByKTerm(nums, k)));
        System.out.println(largeSumSubArray(nums1));
        System.out.println(Arrays.toString(moveZerosAtEnd(nums)));
        System.out.println(frequencyElement(nums,k));


    }

    public static int maxElement(int [] nums){
        int max = nums[0];
        for (int i = 0 ; i <nums.length; i++){
            if (nums[i]>max){
                max = nums[i];
            }
        }
        return max;
    }

    public static int minElement(int [] nums){
        int min = nums[0];
        for (int i =0 ; i<nums.length; i++){
            if (nums[i]<min){
                min=nums[i];
            }
        }
        return min;
    }

    public static int sumElement(int [] nums){
        int sum = 0;
        for(int i = 0; i<nums.length;i++){
            sum =sum+nums[i];
        }
        return sum;
    }

    public static String evenOdd(int [] nums){
        int evenCount =0;
        int oddCount =0;
        for (int i =0; i<nums.length;i++){
            if (nums[i]%2==0){
                evenCount++;
            }else{
                oddCount++;
            }
        }
        return "Even No = "+ evenCount + " and Odd No = "+ oddCount;
    }

   
    public static int[] reverse(int [] nums){
        int temp =0;
        for (int i = 0; i< nums.length/2; i++){
            temp = nums[i];
            nums[i]=nums[nums.length-i-1];
            nums[nums.length-i-1] = temp;
        }
        return nums;
    }

    public static int[] alternate(int [] nums){
        int [] result = new int [nums.length/2];
        int index = 0;
        for (int i = 0 ; i< nums.length; i=i+2){
            result[index]=nums[i];
            index++;
        }
        return result;
    }

    public static int secondMax(int [] nums){
        int max = nums[0];
        int secondMax =0;
        for (int i =0; i<nums.length; i++){
            if (nums[i]>=max){
                secondMax=max;
                max=nums[i];
            }
        }
        return secondMax;
    }

    public static int secondMin(int [] nums){
        int min = nums[0];
        int secondMin =0;
        for (int i = 0; i<nums.length;i++){
            if (nums[i]<=min){
                secondMin = min;
                min = nums[i];
            } else if(nums[i]<secondMin){
                secondMin = nums[i];
            }
        }
        return secondMin;
    }


    /// Worst case --- O[n]
    public static int LinearSearch(int [] nums, int target){
        int index = 0;
        for (int i = 0; i<nums.length;i++){
            if (nums[i] == target){
                index = i;
            }
        }
        return index;
    }
    // Worst Case --- O[Log(n)]
    public static int BinarySearch(int [] nums, int target){
        
        int firstIndex = 0;
        int lastIndex = nums.length-1;
        int midIndex = 0;
        while ( firstIndex <= lastIndex){

             midIndex = (lastIndex+firstIndex)/2;

            if (nums[midIndex] == target){
                return midIndex;
            }

            if (target<nums[midIndex]){
                lastIndex = midIndex-1;
            }else if (target>nums[midIndex]){
                firstIndex = midIndex+1;
            }
            
        }

        return -1;

    }

    public static boolean isSorted(int [] nums){
        boolean isSorted = true;

        for (int i = 1 ; i < nums.length; i++){
            if (nums[i-1] > nums[i]){
                isSorted = false;
            }
        }

        return isSorted;
    }

    public static int [] mergeArr(int n, int m, int [] nums1, int [] nums2){

       return nums1; 
    }

    public static int [] reverseArrayByKTerm(int [] nums, int k){
        int n = nums.length;
        k=k%n;

        //Left Rotate
        reverseArray(nums, 0, n-1);
        reverseArray(nums, 0, k-1);
        reverseArray(nums, k, n-1);

        //Right Rotate
        reverseArray(nums, 0, n-1);
        reverseArray(nums, 0, n-k-1);
        reverseArray(nums, n-k, n-1);
        return nums;
    }

    public static void reverseArray(int [] nums, int start, int end){
        int temp = 0;
        while (start<end){
            temp = nums[start];
            nums[start] = nums[end];
            nums[end]= temp;
            start++;
            end--;
        }
    }

    //Kadane Algo
    public static int largeSumSubArray(int []nums){
        int sum =nums[0];
        int max = nums[0];
        for (int i = 1; i < nums.length; i++){
            sum =sum+nums[i];
            if (max<sum){
                max = sum;
            }
            if (sum<0){
                sum = 0;
            }
        }
        return max;
    }


    public static int[] moveZerosAtEnd(int []nums){
        int index = 0;
        for (int i = 0; i<nums.length; i++){
            if (nums[i]!=0){
                nums[index] = nums[i];
                index++;
            }
        }
        for (int i = index; i <nums.length;i++){
            nums[i] = 0;
        }
        return nums;
    }

    public static int frequencyElement(int [] nums, int n){
        int freq = 0;
        HashMap<Integer,Integer> freqMap = new HashMap<>();
        // for (int i = 0; i<nums.length;i++){
        //     freqMap.put(nums[i],frequencyCount(nums, nums[i]));
        // }
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
    
        System.out.println(" ArrayEasy.frequencyElement() : HasMap : " + freqMap);

       for ( Map.Entry<Integer, Integer> entrySet : freqMap.entrySet()){
            if (entrySet.getKey() == n){
                freq=entrySet.getValue();
            }
        }

        return freq;
    }

    public static int frequencyCount(int[] nums, int target){
        int freq = 0;
        for (int i =0; i <nums.length;i++){
            if (nums[i] == target){
                freq++;
            }
        }
        return freq;
    }

}