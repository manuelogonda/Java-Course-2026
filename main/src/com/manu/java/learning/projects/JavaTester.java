package com.manu.java.learning.projects;

public class JavaTester {
    public static void main(String[] args){
        byte byteValue1 = 2;
        byte byteValue2 = 6;
        byte byteResult = (byte) (byteValue1 + byteValue2);
        System.out.println("byteResult = " + byteResult);

        short shortValue1 = 2;
        short shortValue2 = 4;
        short shortResult = (short) (shortValue1 + shortValue2);
        System.out.println("shortResult = " + shortResult);

        int intValue1 = 2;
        int intValue2 = 4;
        int intResult = (int) (intValue1 + intValue2);
        System.out.println("intResult = " + intResult);

        long longValue1 = 22;
        long longValue2 = 42;
        long longResult = (long) longValue1 + longValue2;
        System.out.println("longResult = " + longResult);

        float floatValue1 = 2.0f;
        float floatValue2 = 6.0f;
        float floatResult = (float) (floatValue1 + floatValue2);
        System.out.println("floatResult = " + floatResult);

        double doubleValue1 = 2.0;
        double doubleValue2 = 6.0;
        double doubleResult = (double) doubleValue1 + doubleValue2;
        System.out.println("doubleResult = " + doubleResult);

        char charValue1 = 'A';
       System.out.println("charValue1 = " + charValue1);

        boolean booleanValue = true;
        System.out.println(booleanValue);
    }
}
