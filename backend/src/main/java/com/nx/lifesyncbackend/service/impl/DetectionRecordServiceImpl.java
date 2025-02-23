package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.ImageProcessing;
import com.nx.lifesyncbackend.common.utils.math.Fft;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.mapper.DetectionRecordMapper;
import com.nx.lifesyncbackend.model.entity.BasicHealth;
import com.nx.lifesyncbackend.model.entity.DetectionRecord;
import com.nx.lifesyncbackend.model.entity.User;
import com.nx.lifesyncbackend.service.DetectionRecordService;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * detection record service impl
 *
 * @author nx-xn2002
 */
@Service
@Slf4j
public class DetectionRecordServiceImpl extends ServiceImpl<DetectionRecordMapper, DetectionRecord>
        implements DetectionRecordService {
    @Resource
    private UserService userService;
    /**
     * 采样时间30秒
     */
    private static final int TOTAL_TIME = 30;

    /**
     * 正常心率范围的下限
     */
    private static final int MIN_BPM = 45;

    /**
     * 正常心率范围的上限
     */
    private static final int MAX_BPM = 200;

    @Override
    public DetectionRecord analyse(List<String> imageBase64List, BasicHealth basicHealth, String username) {
        if (imageBase64List == null || imageBase64List.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无图像");
        }
        int bpm = 0;
        try {
            bpm = analyseBloodPressure(imageBase64List);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图像处理异常");
        }
        DetectionRecord detectionRecord = new DetectionRecord();
        detectionRecord.setHeartRate(bpm);
        if (basicHealth != null) {
            Integer height = basicHealth.getHeight();
            Double weight = basicHealth.getWeight();
            Integer age = basicHealth.getAge();
            Integer gender = basicHealth.getGender();
            BloodPressureResult bloodPressureResult = calculateBloodPressure(bpm, height, weight, age, gender);
            if (bloodPressureResult != null) {
                detectionRecord.setDiastolicBp(bloodPressureResult.diastolicPressure());
                detectionRecord.setSystolicBp(bloodPressureResult.systolicPressure());
            }
            // 将身高从厘米转换为米
            double heightInMeters = height / 100.0;
            detectionRecord.setBmi(weight / (heightInMeters * heightInMeters));
        }
        detectionRecord.setCreateTime(new Date());
        detectionRecord.setUpdateTime(new Date());
        if (username != null) {
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if (user != null) {
                detectionRecord.setUserId(user.getId());
            }
            this.save(detectionRecord);
        }
        return detectionRecord;
    }

    @Override
    public List<DetectionRecord> listByUsername(String username) {
        if (username == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "NOT LOGIN");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "NO SUCH USER");
        }
        return this.list(new QueryWrapper<DetectionRecord>().eq("user_id", user.getId()));
    }

    private int analyseBloodPressure(List<String> imageBase64List) throws IOException {
        double[] greenAvgArray = new double[imageBase64List.size()];
        double[] redAvgArray = new double[imageBase64List.size()];
        // 将 base64 编码的图像解码并处理
        log.info("处理中，共{}张图片", imageBase64List.size());
        for (int i = 0; i < imageBase64List.size(); i++) {
            String base64Image = imageBase64List.get(i);
            ImageProcessing.RgbAvg rgbAvg = ImageProcessing.processBase64Image(base64Image);
            greenAvgArray[i] = rgbAvg.g() + 20;
            redAvgArray[i] = rgbAvg.r() + 20;
        }
        // 计算 FFT，获取心率
        double bpm = calculateHeartRateFromImages(greenAvgArray, redAvgArray);
        log.info("计算结果：{}", bpm);
        if (bpm < MIN_BPM || bpm > MAX_BPM) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测量结果异常，请重新测量");
        }
        return (int) bpm;
    }

    private double calculateHeartRateFromImages(double[] greenAvg, double[] redAvg) {
        int frames = greenAvg.length;
        // 计算采样频率：帧数/总时间
        double samplingFreq = frames / (double) TOTAL_TIME;

        // 将数组转换为 Double[]
        Double[] green = new Double[frames];
        Double[] red = new Double[frames];
        for (int i = 0; i < frames; i++) {
            green[i] = greenAvg[i];
            red[i] = redAvg[i];
        }

        // 计算绿色通道的心率
        double hrFreqGreen = Fft.fft(green, frames, samplingFreq);
        double bpmGreen = Math.ceil(hrFreqGreen * 60);

        // 计算红色通道的心率
        double hrFreqRed = Fft.fft(red, frames, samplingFreq);
        double bpmRed = Math.ceil(hrFreqRed * 60);

        // 根据两个通道的结果计算最终心率
        double finalBpm = 0;

        // 检查两个通道的结果是否在合理范围内
        boolean isGreenValid = bpmGreen > MIN_BPM && bpmGreen < MAX_BPM;
        boolean isRedValid = bpmRed > MIN_BPM && bpmRed < MAX_BPM;

        if (isGreenValid && isRedValid) {
            // 如果两个通道都有效，取平均值
            finalBpm = (bpmGreen + bpmRed) / 2;
        } else if (isGreenValid) {
            // 如果只有绿色通道有效
            finalBpm = bpmGreen;
        } else if (isRedValid) {
            // 如果只有红色通道有效
            finalBpm = bpmRed;
        }

        log.info("Green BPM: {}, Red BPM: {}, Final BPM: {}", bpmGreen, bpmRed, finalBpm);
        return bpmGreen + bpmRed;
    }

    private BloodPressureResult calculateBloodPressure(double heartRate, Integer height,
                                                       Double weight, Integer age, Integer gender) {
        // 验证输入参数
        if (heartRate < MIN_BPM || heartRate > MAX_BPM) {
            return null;
        }
        if (height <= 0 || weight <= 0 || age <= 0) {
            return null;
        }
        // Q值初始化（男性为5，女性为4.5）
        double q;
        if (gender == null || gender == BasicHealth.MALE) {
            q = 5.0;
        } else {
            q = 4.5;
        }

        // 计算血压相关参数
        double rob = 18.5;
        double et = 364.5 - 1.23 * heartRate;

        // 体表面积计算 (BSA)
        double bsa = 0.007184 * Math.pow(weight, 0.425) * Math.pow(height, 0.725);

        // 每搏输出量 (SV)
        double sv = -6.6 + (0.25 * (et - 35)) - (0.62 * heartRate)
                + (40.4 * bsa) - (0.51 * age);

        // 脉压 (PP)
        double pp = sv / ((0.013 * weight - 0.007 * age - 0.004 * heartRate) + 1.307);

        // 平均动脉压 (MPP)
        double mpp = q * rob;

        // 计算收缩压和舒张压
        int systolicPressure = (int) (mpp + 3.0 / 2.0 * pp);
        int diastolicPressure = (int) (mpp - pp / 3.0);

        log.info("Blood pressure calculation completed - Systolic: {}, Diastolic: {}",
                systolicPressure, diastolicPressure);
        return new BloodPressureResult(systolicPressure - 5, diastolicPressure + 5);
    }

    /**
     * blood pressure result
     *
     * @author nx-xn2002
     */
    private record BloodPressureResult(Integer systolicPressure, Integer diastolicPressure) {
    }
}




