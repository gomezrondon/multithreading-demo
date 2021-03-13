package com.gomezrondon.multithreadingdemo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="client")
public class Client implements Serializable {

    @Id
    @Column(name = "client_code")
    private Long clientCode;
    @Column(name = "id")
    private String id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "salary")
    private double salary;
    @Column(name = "status")
    private String status;

    public Client() {
    }

    public Client(Long clientCode, String id, String fullName, double salary) {
        this.clientCode = clientCode;
        this.id = id;
        this.fullName = fullName;
        this.salary = salary;
    }

    public Client(Long clientCode, String id, String fullName, double salary, String status) {
        this(clientCode, id, fullName, salary);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getClientCode() {
        return clientCode;
    }

    public void setClientCode(Long clientCode) {
        this.clientCode = clientCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }


    @Override
    public String toString() {
        return "Client{" +
                "clientCode=" + clientCode +
                ", id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", salary=" + salary +
                '}';
    }
}
