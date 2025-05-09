package com.arrend_system.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo implements Serializable {
    private Integer itemId;
    private String itemName;
    private String description;
    private String categoryName;
    private String img;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private String unit;
}
