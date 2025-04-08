package com.arrend_system.pojo.form.add;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AddOrderForm {
    private String title;
    private Integer orderType;
    private Integer addressId;
    private String position;
    private BigDecimal cost;
    private Integer publisher;
    private Integer orderTaker;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private Integer itemId;
    private String description;
}
