package com.manu.java.learning.projects;

import java.util.Arrays;
import java.util.Scanner;

public class StringsAndStringBuilder {
    static void main(String[] args) {
//        String name1 = "manu";
//        String name2 = "manu";
//        System.out.println(name1 == name2); //true
//        System.out.println(name1.equals(name2));//true
//
//        String fnmname1 = new String("manuel");
//        String fnmname2 = new String("manuel");
//        System.out.println(fnmname1 == fnmname2);//false
//        System.out.println(fnmname1.equals(fnmname2));//true

//        System.out.println(56);
//        System.out.println("manu");
//        Integer num = new Integer(56);
//        System.out.println(num.toString());
//        int arr[] = {12,32,45,67,67};
//        System.out.println(arr);
//        System.out.println(Arrays.toString(arr));

        //pretty prinnting
//        float rnum = 345465.7656868976f;
//        System.out.println("The number is %.2f " + rnum);
//        System.out.println("PI is " + Math.PI);

//   Performance
//        String series = "";
//        for (int i = 0; i < 26; i++){
//            char ch = (char) ( 'a' + i );
//          series+=ch;
//        System.out.println(ch);
//        }
//        System.out.println(series);
//StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < 26; i++){
//            char builder = (char) ( 'a' + i );
//            builder;
//            System.out.println(ch);

//        string palindrome checker
        System.out.println("Enter your palindrome : ");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        System.out.println(isPalindrome(str));

        }
        static boolean isPalindrome(String str){
        if (str == null || str.length() == 0) return true;
        str.toLowerCase();
        for (int i = 0; i < str.length() / 2; i++){
            char start = str.charAt(i);
            char end = str.charAt(str.length() - 1 - i);

            if (start != end){
                return false;
            }
        }
        return true;
    }
}
