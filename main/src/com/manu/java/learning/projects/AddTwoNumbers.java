package com.manu.java.learning.projects;

import java.util.Scanner;

public class AddTwoNumbers {
    public static void main(String[] args) {
//        integer input from user
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first number: ");

        int num1 = sc.nextInt();
        System.out.println("Enter second number: ");
        int num2 = sc.nextInt();

        int sum = num1 + num2;
        System.out.println("The sum of " + num1 + " and " + num2 + " is = " + sum);

//        float input
        System.out.println("Enter first float number: ");
        float value1 = sc.nextFloat();

        System.out.println("Enter second float number: ");
        float value2 = sc.nextFloat();

        float value3 = value1 * value2;
        System.out.println("The product of " + value1 + " and " + value2 + " is = " + value3);

//        double input
        System.out.println("Enter first double : ");
        double dub1 = sc.nextDouble();

        System.out.println("Enter second double : ");
        double dub2 = sc.nextDouble();

        double dub3 = dub1 % dub2;
        System.out.println("The remainder when " + dub1 + " and " + dub2 + " are  divided " + " is = " + dub3);

//        byte input
        System.out.println("Enter byte value1: ");
        byte byteVal1 = sc.nextByte();

        System.out.println("Enter byte value2: ");
        byte byteVal2 = sc.nextByte();

        System.out.println("Your byte values are "  + byteVal1 + " and " + byteVal2);

//        boolean input
        System.out.println("Enter boolean value1: ");
        boolean boolVal1 = sc.nextBoolean();

        System.out.println("Enter boolean value2: ");
        boolean boolVal2 = sc.nextBoolean();

        System.out.println("Your boolean values are " + boolVal1 + " and " + boolVal2);

//        big decimal values
        System.out.println("Enter big decimal value1 :");
        java.math.BigDecimal bigDeciVal1 = sc.nextBigDecimal();

        System.out.println("Enter big decimal value2 :");
        java.math.BigDecimal bigDeciVal2 = sc.nextBigDecimal();

        System.out.println("Your big decimal values are " +  bigDeciVal1 + " and " + bigDeciVal2);
    }
}
