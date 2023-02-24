package com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.cus;


import com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.db.db_student.Student;

public class MyStudent extends Student {
    private  String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
