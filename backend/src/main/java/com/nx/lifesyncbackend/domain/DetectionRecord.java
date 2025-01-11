package com.nx.lifesyncbackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * detection record
 *
 * @author nx-xn2002
 * @date 2025-01-11
 */
@TableName(value = "detection_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
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