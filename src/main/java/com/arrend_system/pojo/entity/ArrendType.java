package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName arrend_type
 */
@TableName(value ="arrend_type")
@Data
public class ArrendType implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "type_id", type = IdType.AUTO)
    private Integer typeId;

    /**
     * 跑腿类型
     */
    @TableField(value = "type_name")
    private String typeName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}