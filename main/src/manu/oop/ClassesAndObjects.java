package manu.oop;

public class ClassesAndObjects {
    static void main(String[] args) {
        // Object = Instance created from class
        Car myCar = new Car("Toyota", "Camry", 2024,"Grey");
        Car yourCar = new Car("Honda", "Civic", 2023,"Dark Grey");
        Car someoneElsesCar = new Car("Toyota", "Crown", 2026,"Black");
        Car yoCar = new Car("Toyota", "Fortune", 2025,"Grey");
        // Accessing attributes
        System.out.println(myCar.brand); // Output: Toyota
        // Calling methods
        myCar.accelerate(20.5);
        System.out.println(myCar.speed); // Output: 20.5

    }
    // Class = Blueprint/Template
    public static class Car {
        // Attributes (State/Data)
        String brand;
        String model;
        int year;
        double speed;
        String color;
        Boolean fwd;
        // Constructor (Initialization)
        public Car(String b, String m, int y, String color) {
            this.brand = b;
            this.model = m;
            this.year = y;
            speed = 0.0;
            fwd = false;
        }
        // Methods (Behavior)
        void accelerate(double amount) {
            speed += amount;
        }
        void startMusic(String name){
            System.out.println("The music playing is: " + name);
        }
        int checkFuel(int amount) {
            System.out.println("Remaining fuel is " + amount + " ltrs.");
            return amount;
        }
    }

}
