package com.arrend_system.pojo.form.update;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateUserForm {
    private Integer uId;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private BigDecimal count;
    private String idNumber;
}
