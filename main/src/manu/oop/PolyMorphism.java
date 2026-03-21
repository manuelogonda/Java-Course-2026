package manu.oop;

public class PolyMorphism {
     void main(String[] args) {
         Calculator cal1 = new Calculator();
         System.out.println("Adding two integers " + cal1.add(4,6));
         System.out.println("Adding three integers " + cal1.add(5,7,3));
         System.out.println("Adding two doubles " + cal1.add(2.5,3.5));
        // Runtime polymorphism
        Animal animal = new Cat();
        animal.makeSound(); // Output: Meow!
         Animal donkey = new Donkey();
         donkey.makeSound();
    }
//    A . method overloading
    class Calculator {
    // Two integers
        int add(int a, int b) {
            return a + b;
        }
    // Three integers
        int add(int a, int b, int c) {
            return a + b + c;
        }
    // Two doubles
        double add(double a, double b) {
            return a + b;
        }
    }

//    B . method overriding
    class Animal {
        void makeSound() {
            System.out.println("Some sound");
        }
    }
        class Cat extends Animal {
            @Override
            void makeSound() {
                System.out.println("Meow!");
            }
        }

        class Donkey extends Animal{
         @Override
            void makeSound(){
             System.out.println("Hiho!");
         }
        }
}
