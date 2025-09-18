package ArrayList.Easy;

public class maxElement {

	public static void main(String[] args) {
		
		int num[] = {};
		int max = maxNumber(num);
		System.out.println("Max Number : " + max);
		
	}
	
	public static int maxNumber(int nums[]) {
		int max = Integer.MIN_VALUE;
		if (nums.length==0 || nums == null) {
			throw new IllegalArgumentException("Array cannot be null or empty");
		}
		for (int numMax : nums) {
			if (numMax>max) {
				max=numMax;
			}
		}
		return max;
	}
	
}
