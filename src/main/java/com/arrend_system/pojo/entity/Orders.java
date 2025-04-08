package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName orders
 */
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="orders")
@Data
public class Orders implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 跑腿类型
     */
    @TableField(value = "order_type")
    private Integer orderType;

    /**
     * 收货地址id
     */
    @TableField(value = "address_id")
    private Integer addressId;

    /**
     * 跑腿目的地
     */
    @TableField(value = "position")
    private String position;

    /**
     * 费用
     */
    @TableField(value = "cost")
    private BigDecimal cost;

    /**
     * 发起人id
     */
    @TableField(value = "publisher")
    private Integer publisher;

    /**
     * 跑腿人id
     */
    @TableField(value = "order_taker")
    private Integer orderTaker;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 发布时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    @TableField(value = "finish_time")
    private LocalDateTime finishTime;

    /**
     * 商品id
     */
    @TableField(value = "item_id")
    private Integer itemId;

    /**
     * 跑腿内容描述
     */
    @TableField(value = "description")
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}