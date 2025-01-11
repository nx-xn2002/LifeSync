package com.nx.lifesyncbackend.domain;

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
 * @date 2025-01-11
 */
@TableName(value ="basic_health")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicHealth {
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
    private BigDecimal weight;

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