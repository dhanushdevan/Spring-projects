package com.iagami.Paymentgateway.Dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetFromAnotherApi")
public class GetFromAnotherApi {
    private String name;
    private Integer number;
    private Object orders;

    public GetFromAnotherApi() {
    }

    public GetFromAnotherApi(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @XmlElement
    public Object getOrders() {
        return orders;
    }

    public void setOrders(Object orders) {
        this.orders = orders;
    }
}
