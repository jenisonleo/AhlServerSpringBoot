package com.comcom.server;

import javax.validation.constraints.Email;

public class Test {
    public String username;
    public String fullname;
    @Email
    public String email;
    public String password;

    @Override
    public String toString() {
        return "Test{" +
                "username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}
