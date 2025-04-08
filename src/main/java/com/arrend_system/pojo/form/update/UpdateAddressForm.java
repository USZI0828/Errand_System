package com.arrend_system.pojo.form.update;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAddressForm {
    private Integer id;
    private String consignee;
    private String detailAddress;
    private String area;
    private String phone;
    private String label;
    private BigDecimal Longitude;
    private BigDecimal Latitude;
}
