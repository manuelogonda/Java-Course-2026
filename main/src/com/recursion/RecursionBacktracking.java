package com.recursion;

public class RecursionBacktracking {
    static void main(String[] args) {
        int ans = Fibo(4);
        System.out.println(ans);
    }
    static  int Fibo(int n){
//        base condition
        if(n < 2){
            return n;
        }
        return Fibo(n-1) + Fibo(n-2);
    }
}
