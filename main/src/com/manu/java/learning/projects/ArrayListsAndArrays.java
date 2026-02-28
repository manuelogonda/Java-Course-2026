package com.manu.java.learning.projects;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ArrayListsAndArrays {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
    ArrayList<Integer> list = new ArrayList<>();
//        list.add(23);
//        list.add(24);
//        list.add(25);
//        list.add(25);
//        list.add(26);
//        list.add(27);
//        list.add(28);
//        list.add(2,2);
//        list.get(2);

//        System.out.println("Display everything in list " + list);
//         System.out.println("Change value at a certain position here first position " + list.set(0,94));
//        System.out.println(list.contains(34));
//        System.out.println(list.getLast());
//        list.addFirst(9);
//        System.out.println(list.isEmpty());
//        System.out.println(list);
//       System.out.println(list.size());
//       System.out.println(list.indexOf(25));

//       input taking
//            for (int i = 0; i < 5; i++){
//                System.out.println(list.add(sc.nextInt()));
//            }
//            get element at index
//        for (int i = 0; i < 5; i++){
//            System.out.println(list.get(i));
//        }
//        System.out.println(list);

//        multi-dimensional arraylist

//        ArrayList<ArrayList<Integer>>  multiArrayList = new ArrayList<>();

////        initialization
//        for (int i = 0; i < 3; i++){
//            multiArrayList.add(new ArrayList<>());
//        }
//
////        add elements
//       for (int i = 0; i < 3; i++){
//           for (int j = 0; j < 3; j++){
//               multiArrayList.get(i).add(sc.nextInt());
//           }
//       }
//        System.out.println(multiArrayList);

//        1 Q: swapping an array
//int arr[] = {23,45,67,78,56,54,64};
//swap(arr,3,4);
//        System.out.println(Arrays.toString(arr));

//        2 Q : Finding Maximum item in an array
//        int arr[] = {23,45,67,78,56,54,64};
//        System.out.println(max(arr));
//        3 Q : finding max range in an array
        int arr[] = {56,34,23,34,45,67,89};
        System.out.print(maxValueInRange(arr,2,4));
    }

    //finding maxvalue in a range
   public static int maxValueInRange(int arr[], int start, int end){
       int maxValue = arr[start];
       for (int i = start; i < end; i++){
           if (arr[i] > maxValue){
               maxValue = arr[i];
           }
       }
       return maxValue;
   }
    //finding max item in an array
//    public static int max(int[] arr){
//        int maxValue = arr[0];
//        for (int i = 1; i < arr.length; i++){
//            if (arr[i] > maxValue){
//                maxValue = arr[i];
//            }
//        }
//        return maxValue;
//    }
    //swapping method
//        static void swap(int arr[],int index1,int index2){
//            int temporaryPosition = arr[index1];
//            arr[index1] = arr[index2];
//            arr[index2] = temporaryPosition;
//        }

}
