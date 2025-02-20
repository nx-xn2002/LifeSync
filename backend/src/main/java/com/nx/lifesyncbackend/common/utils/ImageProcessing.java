package com.nx.lifesyncbackend.common.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * image processing
 *
 * @author nx-xn2002
 */
public class ImageProcessing {

    public static RgbAvg processBase64Image(String base64Image) throws IOException {
        // 移除base64字符串的前缀
        String base64Data = base64Image.replaceAll("^data:image/\\w+;base64,", "");

        // 解码base64为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // 转换为BufferedImage
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // 计算RGB平均值
        int width = image.getWidth();
        int height = image.getHeight();
        long redSum = 0, greenSum = 0, blueSum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                redSum += (rgb >> 16) & 0xff;
                greenSum += (rgb >> 8) & 0xff;
                blueSum += rgb & 0xff;
            }
        }
        int pixelCount = width * height;
        return new RgbAvg((double) redSum / pixelCount, (double) greenSum / pixelCount, (double) blueSum / pixelCount);
    }

    public static record RgbAvg(double r, double g, double b) {
    }
}