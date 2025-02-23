package com.nx.lifesyncbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * basic_health
 *
 * @author nx-xn2002
 */
@TableName(value ="basic_health")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicHealth {
    public static final int FEMALE = 0;
    public static final int MALE = 1;
    public static final int SECRET = 2;
    /**
     * basic health id
     */
    @TableId(type = IdType.AUTO)
    private Long healthId;

    /**
     * user id
     */
    private Long userId;

    /**
     * height(cm)
     */
    private Integer height;

    /**
     * weight(m)
     */
    private Double weight;

    /**
     * age
     */
    private Integer age;

    /**
     * gender 0 - female, 1 - male, 2 - secret
     */
    private Integer gender;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * logic delete 0 - not deleted
     */
    @TableLogic
    private Integer isDeleted;
}