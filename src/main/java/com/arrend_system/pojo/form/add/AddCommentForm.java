package com.arrend_system.pojo.form.add;

import lombok.Data;

@Data
public class AddCommentForm {
    private Integer orderId;
    private String description;
    private Integer userId;
}
