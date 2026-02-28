package com.manu.java.learning.projects;

import java.util.HashMap;
import java.util.Map;

public class LongestValidSubarray {
    record Result(int length, int start, int end) {
        @Override
        public String toString() {
            return length == 0
                    ? "No valid subarray found"
                    : String.format("Length=%d  Indices=[%d, %d]", length, start, end);
        }
    }
    public static Result longestValidSubarray(int[] arr, int k) {
        int n = arr.length;

        int left     = 0;
        int sum      = 0;
        int oddCount = 0;
        int maxLen   = 0;
        int bestStart = -1;
        int bestEnd   = -1;

        Map<Integer, Integer> freq = new HashMap<>();

        for (int right = 0; right < n; right++) {

            //1. EXPAND window by including arr[right]
            int val = arr[right];
            sum += val;
            if (val % 2 != 0) oddCount++;
            freq.merge(val, 1, Integer::sum);

            // 2. SHRINK window until hard constraints are satisfied
            while (oddCount > k || freq.getOrDefault(val,0) > 2) {
                int rem = arr[left];
                sum -= rem;
                if (rem % 2 != 0) oddCount--;
                freq.merge(rem, -1, Integer::sum);
                if (freq.get(rem) == 0) freq.remove(rem);
                left++;
            }
            // 3. CHECK soft constraint (passive — never drives shrink)

            if (sum % 3 != 0) {
                int curLen = right - left + 1;
                if (curLen > maxLen) {
                    maxLen    = curLen;
                    bestStart = left;
                    bestEnd   = right;
                }
            }
        }

        return new Result(maxLen, bestStart, bestEnd);
    }

    //  Tests
    public static void main(String[] args) {

        // Test 1 — normal case
        int[] t1 = {1, 2, 3, 1, 2, 3};
        System.out.println("Test 1: " + longestValidSubarray(t1, 2));

        // Test 2 — all sums divisible by 3, no valid window
        int[] t2 = {3, 6, 9, 3};
        System.out.println("Test 2: " + longestValidSubarray(t2, 5));

        // Test 3 — duplicate constraint forces shrink
        int[] t3 = {1, 1, 1, 2};
        System.out.println("Test 3: " + longestValidSubarray(t3, 3));

        // Test 4 — k=0, no odds allowed
        int[] t4 = {2, 4, 6, 8, 1, 4};
        System.out.println("Test 4: " + longestValidSubarray(t4, 0));

        // Test 5 — single odd element, k=1
        int[] t5 = {7};
        System.out.println("Test 5: " + longestValidSubarray(t5, 1));

        // Test 6 — k=0, single even element divisible by 3
        int[] t6 = {6};
        System.out.println("Test 6: " + longestValidSubarray(t6, 0));

        // Test 7 — all same element (duplicate cap test)
        int[] t7 = {5, 5, 5, 5};
        System.out.println("Test 7: " + longestValidSubarray(t7, 4));
    }
}

