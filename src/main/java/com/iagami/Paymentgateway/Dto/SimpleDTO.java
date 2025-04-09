package com.iagami.Paymentgateway.Dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User")
public class SimpleDTO {
    private String name;
    private int age;

    public SimpleDTO() {} // JAXB needs default constructor

    public SimpleDTO(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public int getAge() {
        return age;
    }
}

