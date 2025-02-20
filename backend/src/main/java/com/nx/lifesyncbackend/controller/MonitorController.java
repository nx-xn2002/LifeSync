package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ImageProcessing;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
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
            greenAvgArray[i] = rgbAvg.g();
            redAvgArray[i] = rgbAvg.r();
        }
        // 计算 FFT，获取心率
        double bpm = calculateHeartRateFromImages(greenAvgArray, redAvgArray);
        return ResultUtils.success(bpm);
    }

    private double calculateHeartRateFromImages(double[] greenAvg, double[] redAvg) {
        // TODO: 用 FFT 计算心率的逻辑
        // 这里可以调用 FFT 的实现，类似前面提到的算法
        return 72.0;
    }
}
