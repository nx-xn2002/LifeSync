package com.nx.lifesyncbackend.controller;

import cn.hutool.core.codec.Base64;
import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ImageProcessing;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.model.dto.HeartRateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * monitor controller
 *
 * @author nx-xn2002
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @PostMapping("/analyse")
    public BaseResponse<Double> calculateHeartRate(@RequestBody HeartRateRequest request) {
        List<String> imageBase64List = request.getImages();
//        double[] greenAvgArray = new double[imageBase64List.size()];
//        double[] redAvgArray = new double[imageBase64List.size()];
//
//        // 将 base64 编码的图像解码并处理
//        for (int i = 0; i < imageBase64List.size(); i++) {
//            String base64Image = imageBase64List.get(i);
//            byte[] imageBytes = Base64.decode(base64Image);
//            // TODO:假设所有图像的尺寸都是固定的，直接使用预设的宽高（例如 640x480）
//            int width = 640;
//            int height = 480;
//            // 处理图像并提取红绿光平均值
//            greenAvgArray[i] = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(imageBytes, height, width, 3);
//            redAvgArray[i] = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(imageBytes, height, width, 1);
//        }
//
//        // 计算 FFT，获取心率
//        double bpm = calculateHeartRateFromImages(greenAvgArray, redAvgArray);

        return ResultUtils.success((double) imageBase64List.size());
    }

    private double calculateHeartRateFromImages(double[] greenAvg, double[] redAvg) {
        // TODO: 用 FFT 计算心率的逻辑
        // 这里可以调用 FFT 的实现，类似前面提到的算法
        return 72.0;
    }
}
