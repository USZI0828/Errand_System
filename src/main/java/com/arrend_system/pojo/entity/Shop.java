package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName shop
 */
@TableName(value ="shop")
@Data
public class Shop implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "shop_id", type = IdType.AUTO)
    private Integer shopId;

    /**
     * 商店名
     */
    @TableField(value = "shop_name")
    private String shopName;

    /**
     * 所属人id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 地区
     */
    @TableField(value = "area")
    private String area;

    /**
     * 详细地址
     */
    @TableField(value = "detail_address")
    private String detailAddress;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}