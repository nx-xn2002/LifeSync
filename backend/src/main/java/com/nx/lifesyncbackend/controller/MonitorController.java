package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ImageProcessing;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.common.utils.math.Fft;
import com.nx.lifesyncbackend.model.dto.HeartRateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * monitor controller
 *
 * @author nx-xn2002
 */
@RestController
@RequestMapping("/monitor")
@Slf4j
public class MonitorController {
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

    @PostMapping("/analyse")
    public BaseResponse<Double> calculateHeartRate(@RequestBody HeartRateRequest request) throws IOException {
        List<String> imageBase64List = request.getImages();
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
            return ResultUtils.error(40000, "测量结果异常，请重新测量", "");
        }

        return ResultUtils.success(bpm);
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

}