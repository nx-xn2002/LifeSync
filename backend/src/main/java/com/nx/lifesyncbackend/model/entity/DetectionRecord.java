package com.nx.lifesyncbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * detection record
 *
 * @author nx-xn2002
 */
@TableName(value ="detection_record")
@Data
public class DetectionRecord {
    /**
     * record id
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * user id
     */
    private Long userId;

    /**
     * systolic blood pressure value(mmHg)
     */
    private Integer systolicBp;

    /**
     * diastolic blood pressure value(mmHg)
     */
    private Integer diastolicBp;

    /**
     * heart rate(BPM)
     */
    private Integer heartRate;

    /**
     * bmi
     */
    private Double bmi;

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