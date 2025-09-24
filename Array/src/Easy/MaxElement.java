package Easy;

public class MaxElement {

    public static void main(String[] args) {
        int[] num = {1, 2, 3, 4, 923, 12};
        int max = maxNumber(num);
        System.out.println("Max Number : " + max);
    }

    public static int maxNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        int max = Integer.MIN_VALUE;
        for (int numMax : nums) {
            if (numMax > max) {
                max = numMax;
            }
        }
        return max;
    }
}
