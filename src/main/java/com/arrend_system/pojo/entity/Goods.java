package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName goods
 */
@TableName(value ="goods")
@Data
public class Goods implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;

    /**
     * 名称
     */
    @TableField(value = "item_name")
    private String itemName;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 单价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 所属商店id
     */
    @TableField(value = "shop_id")
    private Integer shopId;

    /**
     * 图片
     */
    @TableField(value = "img")
    private String img;

    /**
     * 销量
     */
    @TableField(value = "sales")
    private Integer sales;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}