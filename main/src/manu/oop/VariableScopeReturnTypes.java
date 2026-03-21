package manu.oop;

public class VariableScopeReturnTypes {
    static void main(String[] args) {
        Student s1 = new Student("Alice", 16);
        Student s2 = new Student("Bob", 17);
        Student s3 = new Student("Hill",19);
        System.out.println(s3.enrolledInCourse("System Design"));
        System.out.println(s3.tuitionFeePaid(78000));
        System.out.println(s3.study("Design Principles"));
        // Instance variables - unique to each object
        System.out.println(s1.name); // Alice
        System.out.println(s2.name); // Bob
        // Static variables - shared across all instances
        System.out.println(Student.schoolName); // ABC High
        System.out.println(Student.studentCount); // 2
    }

    public static class Student {
        // Instance variable - belongs to each object
        private String name;
        private int age;
        // Class/Static variable - shared by ALL objects
        public static String schoolName = "ABC High";
        public static int studentCount = 0;
        // Constructor
        public Student(String n, int a) {
            this.name = n; // 'this' refers to current object
            this.age = a;
            studentCount++; // Access static variable
        }
        // Method with local variable
        public String study(String subject) {
            int hours = 2; // Local variable
            System.out.println(name + " studied " + subject + " for " + hours + " hours");
            return subject;
        }
        String enrolledInCourse(String course) {
            return name + " enrolled for " + course + " course taking 6 months";
        }
        int tuitionFeePaid(int fee){
            System.out.println( name + " has paid KESH " + fee + " for tuition this semester ");
            return fee;
        }
    }

}
