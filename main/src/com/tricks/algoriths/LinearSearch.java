package com.tricks.algoriths;

import java.util.Arrays;

public class LinearSearch {
    static void main(String[] args) {
        int nums[] = {12,45,5564,54645,34756,76,432,2};
        int target = 2;
        int ans = linearSearch(nums,target);
        System.out.println(ans);

        String name = "Emmanuel";
        char target2 = 'm';
        boolean ans2 = searchString(name,target2);
        System.out.println(ans2);

        int[] nums2 = {34,3,24,4,3,23,12,45};
        int target3 = 4;
        int ans3 = searchRange(nums2,target3,1,5);
        System.out.println(ans3);

        int[] nums3 = {34,-3,24,4,3,23,12,45};
        int ans4 = minValue(nums3);
        System.out.println(ans4);

        int[] nums5 = {34,3,24,4,3,2043,12,45};
        System.out.println(maxValue(nums5));

        int[][] nums2d = {
                {4,6,54,34,3},
                {34,23,12,45,34,5,67,89,190},
                {45,-6,23},
                {21,23}
        };
        int target2d = 5;
        int[] ans2d = search2D(nums2d,target2d);
        System.out.println(Arrays.toString(ans2d));

        System.out.println(maxValueIn2D(nums2d));
        System.out.println(minValueIn2D(nums2d));

        int[] evenSearch = {18,124,9,1764,98,1};
        System.out.println(findNumberDigitsEvenCount(evenSearch));
    }


    //    A . Search in Integers
//        search in the array return the index if found otherwise -1
    static int linearSearch(int[] arr , int target){
        if (arr.length == 0){
            return -1;
        }
//        run a for loop
        for (int index = 0; index < arr.length; index++){
//            check for element at every index
            int element = arr[index];
            if (element == target){
                return index;
            }
        }
        return  -1;
    }

//    B . Search in Strings
    public static Boolean searchString(String str,char target){
        if (str.length() == 0){
            return  false;
        }

        for (int i = 0; i < str.length(); i++){
            if (str.charAt(i) == target){
                return true;
            }
        }
        return false;
    }

//    C . Search in a Range
    public static int searchRange(int[] arr,int target,int start,int end){
        if (arr.length == 0){
            return  -1;
        }

        for (int i = 0; i < arr.length; i++){
            if (arr[i] == target){
                return i;
            }
        }
        return -1;
    }

//    D . Min and max Search
    public static int minValue(int[] arr){
        int ans = arr[0];
        if (arr.length == 0) {
            return  -1;
        }
        for (int i = 1; i < arr.length; i++) {
            if(arr[i] < ans ){
                ans = arr[i];
            }
        }
        return ans;
    }

//    E . Max search
    public static int maxValue(int[] arr){
        int ans = arr[0];
        if (arr.length == 0) {
            return  -1;
        }
        for (int i = 1; i < arr.length; i++) {
            if(arr[i] > ans ){
                ans = arr[i];
            }
        }
        return ans;
    }

//    F . Search in Two D array
    public static int[] search2D(int[][] nums2d, int target2d) {
        for (int row = 0; row < nums2d.length; row++){
            for (int col = 0; col < nums2d[row].length; col++){
                if (nums2d[row][col] == target2d){
                    return new int[]{row,col};
                }
            }
        }
        return new int[]{-1,-1};
    }

//    G . max Search in 2D Array
    public static int maxValueIn2D(int[][] nums2d) {
        int max = Integer.MIN_VALUE;
        for (int[] ints : nums2d) {
            for (int element : ints){
                if (element >  max){
                    max = element;
                }
            }
        }
        return max;
    }
    //    H. min Search in 2D Array
    public static int minValueIn2D(int[][] nums2d) {
        int min = Integer.MAX_VALUE;
        for (int[] ints : nums2d) {
            for (int element : ints){
                if (element <  min){
                    min = element;
                }
            }
        }
        return min;
    }
//    I . Some LeetCode - Find Numbers containing Even number of digits
    static int findNumberDigitsEvenCount(int[] nums){
        int count = 0;
        for (int num : nums){
            if (evenNum(num)){
                count++;
            }
        }
        return  count;
    }

    static boolean evenNum(int num){
        int numberOfDigits = digitCount(num);
        if (numberOfDigits % 2 == 0){
            return true;
        }
        return false;
    }

    static int digitCount(int num){
        int count = 0;
        if (num < 0){
            return num *= -1;
        }
        if (num == 0){
            return 1;
        }
        while (num > 0) {
            num /= 10;
            count++;
        }
        return count;
    }

//    J . Person max Wealth
    public static int maxWealth(int[][] accounts){
        int ans = Integer.MIN_VALUE;
        for(int person = 0; person < accounts.length; person++){
            int sum = 0;
            for (int account = 0; account < accounts[person].length;account ++){
                sum += accounts[person][account ];
            }
            if(sum > ans){
                ans = sum;
            }
        }
        return ans;
    }
}
