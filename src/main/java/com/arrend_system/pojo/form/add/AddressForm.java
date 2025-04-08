package com.arrend_system.pojo.form.add;

import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
public class AddressForm {
    private Integer userId;
    private String consignee;
    private String detailAddress;
    private String area;
    private String phone;
    private String label;
    private BigDecimal Longitude;
    private BigDecimal Latitude;
}
