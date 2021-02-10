package com.technosales.net.buslocationannouncement.pojo;

public class Passenger {
    public int id, amount;
    public String name, address, phone, cardNumber;
    public Passenger(){}

    public Passenger(int id, int amount, String name, String address, String phone, String cardNumber){
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.cardNumber = cardNumber;

    }

}
