package com.manu.java.learning.projects;

import java.util.LinkedList;

public class LL {
    static void main(String[] args) {
//        LinkedList<Integer> nums = new LinkedList<>();
//        nums.add(2);
//        nums.add(22);
//        nums.add(21);
//        nums.add(24);
//        nums.add(23);
//        nums.add(27);
//       nums.add(2,30);
//       nums.contains(3);
//        System.out.println(nums.get(nums.toArray().length - 1));
//       System.out.println(nums);
LinkedList list = new LinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);

        list.printList();
    }
    // Node class definition
    static class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
            this.next = null;
        }
    }

    // LinkedList class definition
    static class LinkedList {
        Node head;

        public LinkedList() {
            this.head = null;
        }

        // Method to add a node at the end
        public void add(int value) {
            Node newNode = new Node(value);
            if (head == null) {
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newNode;
            }
        }

        // Method to print the list
        public void printList() {
            Node current = head;
            while (current != null) {
                System.out.print(current.value + " ");
                current = current.next;
            }
            System.out.println();
        }
    }
}
