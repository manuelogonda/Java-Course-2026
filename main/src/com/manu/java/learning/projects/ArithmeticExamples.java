package com.manu.java.learning.projects;

public class ArithmeticExamples {
    public static void main(String[] args){
        int num1 = 10;
        int num2 = 20;
        int num3 = 12;
        int num4 = 11;
        int num5 = 14;
        int num6 = 15;
//        addition
        System.out.println("The sum is " + (num1 + num2));
//        substraction
        System.out.println("The difference is " +  (num1 - num2));
        //        Multiplication
        System.out.println("The multiplication is " +  (num1 * num2));
        //        Reminder
        System.out.println("The Reminder is " +  (num1  % num2));
        //     assignment operations
        System.out.println("The assignment addition 0f "+ num3 + " and " + num1 + " is " + (num3 += num1));
        System.out.println("The assignment substraction 0f "+ num4 +  " and " + num1 +  " is " + (num4 -= num1));
        System.out.println("The assignment multiplication 0f "+ num5 +  " and " + num1 +  " is " + (num5 *= num1));
        System.out.println("The assignment reminder 0f "+ num6 +  " and " + num1 +  " is " + (num6 %= num1));

        //Relational operators
        System.out.println("Is num1 equal to num2: " + (num1 == num2));
        System.out.println("Is num1 not equal to num2: " + (num1 != num2));
        System.out.println("Is num1 greater than num2: " + (num1 > num2));
        System.out.println("Is num1 less than num2: " + (num1 < num2));
        System.out.println("Is num1 greater or equal to num1: " + (num1 >= num2));
        System.out.println("Is num1 less or equal to num2: " + (num1 <= num2));

        //Logical operators
        boolean a = true;
        boolean b = false;
        System.out.println("a && b " + (a && b));
        System.out.println("a || b " + (a || b));
        System.out.println("!a " + (!a));
        System.out.println("!b " + (!b));

//        Unary operators
        int num7 = +num2;
        int num8 = -num2;
        int num9 = ++num4;
        int num10 = --num4;
        System.out.println("Increment of num1 " + num1++);
        System.out.println("Post-Increment " + num9);
        System.out.println("Decrement of num1 " + num1--);
        System.out.println("Post-Decrement " + num10);
        System.out.println("Positive num2 is "  + num7);
        System.out.println("Negative num2 is " + num8);

//        misc operators
//        a)Ternary
        int kesh, dollar;
        kesh = 24;
        dollar = (kesh == 20) ? 2600 : 130;
        System.out.println("The value of dollar is " + dollar);

        dollar = (kesh == 24) ? 3120 : 130;
        System.out.println("The value of dollar is " + dollar);
//        b)instanceof
        String name = "Manuel";
        boolean result = name instanceof String;
        System.out.println(result);

//        operator precedence
        int result1 = 10 + 5 * 2;
        int result2 = (10 + 5) * 2;
        int result3 = 20 / 4 * 2;
        int result4 = 10 - 3 + 2;

        System.out.println("10 + 5 * 2 = " + result1);
        System.out.println("(10 + 5) * 2 = " + result2);
        System.out.println("20 / 4 * 2 = " + result3);
        System.out.println("10 - 3 + 2 = " + result4);

//        bitwise operator

    }
}
