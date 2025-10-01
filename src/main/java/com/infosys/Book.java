package com.infosys;


public class Book {

    private String empName;
    private Integer salary;

    @Override
    public String toString() {
        return "Book{" +
                "empName='" + empName + '\'' +
                ", salary=" + salary +
                '}';
    }

    public Book() {
    }

    public Book(String empName, Integer salary) {
        this.empName = empName;
        this.salary = salary;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }
}
