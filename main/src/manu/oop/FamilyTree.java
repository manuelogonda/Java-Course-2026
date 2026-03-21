package manu.oop;

public class FamilyTree {
     void main(String[] args) {
        Dog dog1 = new Dog("bosko",1,"local");
        Dog dog2 = new Dog("juli",2,"Belgian malinois");
        Dog dog3 = new Dog("rex",3,"German Shepard");
         System.out.println(dog1.breed);
         System.out.println(dog2.name + " is a " +  dog2.breed + " he is " + dog2.age + " years old");
    }
        // Parent/Superclass
        public class Animal {
            protected String name;
            protected int age;

            public Animal(String name, int age){
                this.name = name;
                this.age = age;
            }
            public void eat() {
                System.out.println(name + " is eating");
            }
            public void sleep() {
                System.out.println(name + " is sleeping");
            }
        }

        // Child/Subclass
        public class Dog extends Animal {
            private String breed;
            public Dog(String name, int age, String breed){
                super(name, age); // Call parent constructor
                this.breed = breed;
            }

            public void bark() {
                System.out.println(name + " says: Woof!");
            }
        }
}



