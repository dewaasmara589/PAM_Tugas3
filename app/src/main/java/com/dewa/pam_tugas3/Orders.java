package com.dewa.pam_tugas3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Orders {
    private String name;
    private String address;

    public Orders() {
    }

    public Orders(String name, String address, String createdDate) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
