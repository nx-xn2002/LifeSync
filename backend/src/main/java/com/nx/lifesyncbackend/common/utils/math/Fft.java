package com.nx.lifesyncbackend.common.utils.math;

/**
 * fft
 *
 * @author nx-xn2002
 */
public class Fft {
    public static double fft(Double[] in, int size, double samplingFrequency) {
        double temp = 0;
        double pomp = 0;
        double frequency;
        double[] output = new double[2 * size];
        for (int x = 0; x < size; x++) {
            output[x] = in[x];
        }
        DoubleFft1d fft = new DoubleFft1d(size);
        fft.realForward(output);
        for (int x = 0; x < 2 * size; x++) {
            output[x] = Math.abs(output[x]);
        }
        // 12 was chosen because it is a minimum frequency that we think people can get to determine heart rate.
        for (int p = 35; p < size; p++) {
            if (temp < output[p]) {
                temp = output[p];
                pomp = p;
            }
        }
        if (pomp < 35) {
            pomp = 0;
        }
        frequency = pomp * samplingFrequency / (2 * size);
        return frequency;
    }
}
