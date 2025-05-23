package com.arrend_system.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName comment
 */
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    /**
     * 评价所在的订单id
     */
    @TableField(value = "order_id")
    private Integer orderId;

    /**
     * 评价
     */
    @TableField(value = "description")
    private String description;

    @TableField(value = "user_id")
    private Integer userId;

    @TableField(value = "parent_id")
    private Integer parentId;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}