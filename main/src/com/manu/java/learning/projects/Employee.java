package com.manu.java.learning.projects;
import java.io.*;
import java.sql.*;

public class Employee {
    public String name;
    private double salary;
    public Employee(String empName){
        name =empName;
    }
  public void setSalary(double employeeSalary) {
        salary = employeeSalary;
  }
  public void printEmployee() {
        System.out.println("Employee Name: " + name);
        System.out.println("Employee Salary: " + salary);
  }
    public static void main(String[] args){
        Employee employeeOne = new Employee("Emmanuel");
        employeeOne.setSalary(10000);
        employeeOne.printEmployee();
    }

}
