package com.tricks.algoriths;

public class BinarySearch {
    static void main(String[] args) {
        int[] arr = {1,2,3,4,5,45,-5,234};
        int target = 5;
        int ans = binarySearch(arr,target);
        System.out.println(ans);

        int[] arr1 = {1,2,3,4,5,45,-5,234};
        int target1 = 3;
        System.out.println(orderAgonisticBS(arr1,target1));

    }
//   A. Only Assume array sorted either ascending or descending
    static int binarySearch(int[] arr,int target){
        int start = 0;
        int end = arr.length-1;

        while(start<=end){
            int mid = start + (end-start)/2;

            if (target < arr[mid]){
                end = mid -1;
            }else if(target > arr[mid]){
                start = mid +1;
            }else{
                return mid;
            }
        }
        return -1;
    }

//    B . Not aware if sorted or not -OrderAgonistic Binary Search
    public static int orderAgonisticBS(int[] arr, int target){
        int start =0;
        int end = arr.length-1;

        boolean isAscending;
        if(arr[start] < arr[end]){
            isAscending = true;
        }else {
            isAscending =  false;
        }

        while(start<=end){
            int mid = start + (end-start)/2;

            if (isAscending){
                if (target < arr[mid]){
                    end = mid -1;
                }else {
                    start = mid +1;
                }
            }else{
                if (target > arr[mid]){
                    end = mid -1;
                }else {
                    start = mid +1;
                }
            }
        }
        return -1;
    }
}
