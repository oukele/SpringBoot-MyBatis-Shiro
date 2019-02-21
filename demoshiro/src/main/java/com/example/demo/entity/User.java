package com.example.demo.entity;

/**
 * 用户类
 *
 * @author OYL
 * @create 2019-02-21 8:50
 */
public class User {

   private int id;
   private String name;
   private String password;
   private String permit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }
}
