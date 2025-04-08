package com.arrend_system.pojo.form;

import lombok.Data;

@Data
public class RegisterForm {
    private String username;
    private String password;
    private String email;
    private String code;
}
