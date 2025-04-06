package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName address
 */
@TableName(value ="address")
@Data
public class Address implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "address_id", type = IdType.AUTO)
    private Integer addressId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 收货人
     */
    @TableField(value = "consignee")
    private String consignee;

    /**
     * 详细地址
     */
    @TableField(value = "detail_address")
    private String detailAddress;

    /**
     * 区域
     */
    @TableField(value = "area")
    private String area;

    /**
     * 收货人手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 标签
     */
    @TableField(value = "label")
    private String label;

    /**
     * 删除标志位
     */
    @TableField(value = "default_flag")
    private Integer defaultFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}