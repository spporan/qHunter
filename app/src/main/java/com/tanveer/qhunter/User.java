package com.tanveer.qhunter;

public class User {
    public String name;
    public String organization;
    public String phone;
    public String email;
    public String messenger;
    public String address;

    public User(){

    }

    public User(String name, String organization, String phone, String email, String messenger, String address) {
        this.name = name;
        this.organization = organization;
        this.phone = phone;
        this.email = email;
        this.messenger = messenger;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    public String getPhone() {
        return phone.toString();
    }

    public String getEmail() {
        return email;
    }

    public String getMessenger() {
        return messenger;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
