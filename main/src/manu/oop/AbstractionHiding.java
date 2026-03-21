//package manu.oop;
//
//public class AbstractionHiding {
//     void main(String[] args) {
//        Circle circ1 = new Circle();
//    }
////    A. Abstract classes
//    // Cannot be instantiated directly
//    abstract class Shape {
//            protected String color;
//        // Abstract method - nobody
//        abstract double calculateArea();
//        // Concrete method - with body
//        public void setColor(String c) {
//            this.color = c;
//        }
//    }
//        class Circle extends Shape {
//            private double radius;
//            public Circle (double r) {
//                this.radius = r;
//            }
//            @Override
//            double calculateArea() {
//                return Math.PI * radius * radius;
//            }
//        }
////    B . Interfaces 100% abstraction
//// Pure contract - no implementation
//    interface Drawable {
//        void draw(); // Abstract by default
//    // Default method (Java 8+)
//        default void printInfo() {
//            System.out.println("Drawing shape...");
//        }
//    }
//        interface Resizable {
//            void resize(double factor);
//        }
//    // Multiple interface implementation
//        class Rectangle implements Drawable, Resizable {
//            @Override
//            public void draw() {
//                System.out.println("Drawing rectangle");
//            }
//            @Override
//            public void resize(double factor) {
//            // Implementation
//            }
//        }
//}
