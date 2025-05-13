package com.arrend_system.pojo.form.update;

import lombok.Data;

@Data
public class UpdateUserForm {
    private Integer uId;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String avatar;
}
