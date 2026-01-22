package com.manu.java.learning.projects;

enum FreshJuiceExample{SMALL,MEDIUM,LARGE};

public class FreshJuice {
    public static void main(String[] args){
        FreshJuiceExample juiceSize = FreshJuiceExample.MEDIUM;
        System.out.println("Size " + juiceSize);
    }
}
