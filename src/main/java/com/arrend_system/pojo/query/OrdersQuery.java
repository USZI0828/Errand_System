package com.arrend_system.pojo.query;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersQuery extends BaseQuery {
    private String title;
    private Integer orderType;
    private Integer addressId;
    private BigDecimal Mincost;
    private BigDecimal Maxcost;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
