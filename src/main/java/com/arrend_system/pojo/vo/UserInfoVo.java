package com.arrend_system.pojo.vo;


import com.arrend_system.pojo.entity.Perm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoVo {
    private Integer uId;
    private String sex;
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private List<Perm> permList = new ArrayList<>();
}
