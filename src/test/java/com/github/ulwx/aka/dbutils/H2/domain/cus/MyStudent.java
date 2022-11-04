package com.github.ulwx.aka.dbutils.H2.domain.cus;


import com.github.ulwx.aka.dbutils.H2.domain.db.db_student.Student;

public class MyStudent extends Student {
    private  String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
