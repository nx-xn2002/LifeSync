package com.nx.lifesyncbackend.common.utils.math;

import java.util.concurrent.Future;

import static com.nx.lifesyncbackend.common.utils.math.Cft.*;

/**
 * double fft1d
 *
 * @author nx-xn2002
 */
public class DoubleFft1d {

    private enum Plans {
        /**
         * Plans
         */
        SPLIT_RADIX, MIXED_RADIX, BLUESTEIN
    }

    private final int n;

    private int nBluestein;

    private int[] ip;

    private double[] w;

    private int nw;

    private int nc;

    private double[] wtable;

    private double[] wtable_r;

    private double[] bk1;

    private double[] bk2;

    private final Plans plan;

    private static final int[] factors = {4, 2, 3, 5};

    private static final double PI = 3.14159265358979311599796346854418516;

    private static final double TWO_PI = 6.28318530717958623199592693708837032;

    /**
     * Creates new instance of DoubleFFT_1D.
     *
     * @param n size of data
     */
    public DoubleFft1d(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        this.n = n;

        if (!ConcurrencyUtils.isPowerOf2(n)) {
            if (getReminder(n, factors) >= 211) {
                plan = Plans.BLUESTEIN;
                nBluestein = ConcurrencyUtils.nextPow2(n * 2 - 1);
                bk1 = new double[2 * nBluestein];
                bk2 = new double[2 * nBluestein];
                this.ip = new int[2 + (int) Math.ceil(2 + (1 << (int) (Math.log(nBluestein + 0.5) / Math.log(2)) / 2))];
                this.w = new double[nBluestein];
                int twon = 2 * nBluestein;
                nw = ip[0];
                if (twon > (nw << 2)) {
                    nw = twon >> 2;
                    makewt(nw);
                }
                nc = ip[1];
                if (nBluestein > (nc << 2)) {
                    nc = nBluestein >> 2;
                    makect(nc, w, nw);
                }
                bluesteini();
            } else {
                plan = Plans.MIXED_RADIX;
                wtable = new double[4 * n + 15];
                wtable_r = new double[2 * n + 15];
                cffti();
                rffti();
            }
        } else {
            plan = Plans.SPLIT_RADIX;
            this.ip = new int[2 + (int) Math.ceil(2 + (1 << (int) (Math.log(n + 0.5) / Math.log(2)) / 2))];
            this.w = new double[n];
            int twon = 2 * n;
            nw = ip[0];
            if (twon > (nw << 2)) {
                nw = twon >> 2;
                makewt(nw);
            }
            nc = ip[1];
            if (n > (nc << 2)) {
                nc = n >> 2;
                makect(nc, w, nw);
            }
        }
    }

    /**
     * real forward
     *
     * @param a a
     */
    public void realForward(double[] a) {
        realForward(a, 0);
    }

    /**
     * Computes 1D forward DFT of real data leaving the result in <code>a</code>
     * . The physical layout of the output data is as follows:<br>
     * <p>
     * if n is even then
     *
     * <pre>
     * a[offa+2*k] = Re[k], 0&lt;=k&lt;n/2
     * a[offa+2*k+1] = Im[k], 0&lt;k&lt;n/2
     * a[offa+1] = Re[n/2]
     * </pre>
     * <p>
     * if n is odd then
     *
     * <pre>
     * a[offa+2*k] = Re[k], 0&lt;=k&lt;(n+1)/2
     * a[offa+2*k+1] = Im[k], 0&lt;k&lt;(n-1)/2
     * a[offa+1] = Im[(n-1)/2]
     * </pre>
     * <p>
     * This method computes only half of the elements of the real transform. The
     * other half satisfies the symmetry condition. If you want the full real
     * forward transform, use <code>realForwardFull</code>. To get back the
     * original data, use <code>realInverse</code> on the output of this method.
     *
     * @param a    data to transform
     * @param offa index of the first element in array <code>a</code>
     */
    public void realForward(double[] a, int offa) {
        if (n == 1) {
            return;
        }

        switch (plan) {
            case SPLIT_RADIX:
                double xi;

                if (n > 4) {
                    cftfsub(n, a, offa, ip, nw, w);
                    rftfsub(n, a, offa, nc, w, nw);
                } else if (n == 4) {
                    cftx020(a, offa);
                }
                xi = a[offa] - a[offa + 1];
                a[offa] += a[offa + 1];
                a[offa + 1] = xi;
                break;
            case MIXED_RADIX:
                rfftf(a, offa);
                for (int k = n - 1; k >= 2; k--) {
                    int idx = offa + k;
                    double tmp = a[idx];
                    a[idx] = a[idx - 1];
                    a[idx - 1] = tmp;
                }
                break;
            case BLUESTEIN:
                bluestein_real_forward(a, offa);
                break;
        }
    }

    private static int getReminder(int n, int[] factors) {
        int reminder = n;

        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive integer");
        }

        for (int i = 0; i < factors.length && reminder != 1; i++) {
            int factor = factors[i];
            while ((reminder % factor) == 0) {
                reminder /= factor;
            }
        }
        return reminder;
    }

    void cffti() {
        if (n == 1) {
            return;
        }

        final int twon = 2 * n;
        final int fourn = 4 * n;
        double argh;
        int idot, ntry = 0, i, j;
        double argld;
        int i1, k1, l1, l2, ib;
        double fi;
        int ld, ii, nf, ip, nl, nq, nr;
        double arg;
        int ido, ipm;

        nl = n;
        nf = 0;
        j = 0;

        factorize_loop:
        while (true) {
            j++;
            if (j <= 4) {
                ntry = factors[j - 1];
            } else {
                ntry += 2;
            }
            do {
                nq = nl / ntry;
                nr = nl - ntry * nq;
                if (nr != 0) {
                    continue factorize_loop;
                }
                nf++;
                wtable[nf + 1 + fourn] = ntry;
                nl = nq;
                if (ntry == 2 && nf != 1) {
                    for (i = 2; i <= nf; i++) {
                        ib = nf - i + 2;
                        int idx = ib + fourn;
                        wtable[idx + 1] = wtable[idx];
                    }
                    wtable[2 + fourn] = 2;
                }
            } while (nl != 1);
            break factorize_loop;
        }
        wtable[fourn] = n;
        wtable[1 + fourn] = nf;
        argh = TWO_PI / (double) n;
        i = 1;
        l1 = 1;
        for (k1 = 1; k1 <= nf; k1++) {
            ip = (int) wtable[k1 + 1 + fourn];
            ld = 0;
            l2 = l1 * ip;
            ido = n / l2;
            idot = ido + ido + 2;
            ipm = ip - 1;
            for (j = 1; j <= ipm; j++) {
                i1 = i;
                wtable[i - 1 + twon] = 1;
                wtable[i + twon] = 0;
                ld += l1;
                fi = 0;
                argld = ld * argh;
                for (ii = 4; ii <= idot; ii += 2) {
                    i += 2;
                    fi += 1;
                    arg = fi * argld;
                    int idx = i + twon;
                    wtable[idx - 1] = Math.cos(arg);
                    wtable[idx] = Math.sin(arg);
                }
                if (ip > 5) {
                    int idx1 = i1 + twon;
                    int idx2 = i + twon;
                    wtable[idx1 - 1] = wtable[idx2 - 1];
                    wtable[idx1] = wtable[idx2];
                }
            }
            l1 = l2;
        }

    }

    void rffti() {

        if (n == 1) {
            return;
        }
        final int twon = 2 * n;
        double argh;
        int ntry = 0, i, j;
        double argld;
        int k1, l1, l2, ib;
        double fi;
        int ld, ii, nf, ip, nl, is, nq, nr;
        double arg;
        int ido, ipm;
        int nfm1;

        nl = n;
        nf = 0;
        j = 0;

        factorize_loop:
        while (true) {
            ++j;
            if (j <= 4) {
                ntry = factors[j - 1];
            } else {
                ntry += 2;
            }
            do {
                nq = nl / ntry;
                nr = nl - ntry * nq;
                if (nr != 0) {
                    continue factorize_loop;
                }
                ++nf;
                wtable_r[nf + 1 + twon] = ntry;

                nl = nq;
                if (ntry == 2 && nf != 1) {
                    for (i = 2; i <= nf; i++) {
                        ib = nf - i + 2;
                        int idx = ib + twon;
                        wtable_r[idx + 1] = wtable_r[idx];
                    }
                    wtable_r[2 + twon] = 2;
                }
            } while (nl != 1);
            break factorize_loop;
        }
        wtable_r[twon] = n;
        wtable_r[1 + twon] = nf;
        argh = TWO_PI / (double) (n);
        is = 0;
        nfm1 = nf - 1;
        l1 = 1;
        if (nfm1 == 0) {
            return;
        }
        for (k1 = 1; k1 <= nfm1; k1++) {
            ip = (int) wtable_r[k1 + 1 + twon];
            ld = 0;
            l2 = l1 * ip;
            ido = n / l2;
            ipm = ip - 1;
            for (j = 1; j <= ipm; ++j) {
                ld += l1;
                i = is;
                argld = (double) ld * argh;

                fi = 0;
                for (ii = 3; ii <= ido; ii += 2) {
                    i += 2;
                    fi += 1;
                    arg = fi * argld;
                    int idx = i + n;
                    wtable_r[idx - 2] = Math.cos(arg);
                    wtable_r[idx - 1] = Math.sin(arg);
                }
                is += ido;
            }
            l1 = l2;
        }
    }

    private void bluesteini() {
        int k = 0;
        double arg;
        double pi_n = PI / n;
        bk1[0] = 1;
        bk1[1] = 0;
        for (int i = 1; i < n; i++) {
            k += 2 * i - 1;
            if (k >= 2 * n) {
                k -= 2 * n;
            }
            arg = pi_n * k;
            bk1[2 * i] = Math.cos(arg);
            bk1[2 * i + 1] = Math.sin(arg);
        }
        double scale = 1.0 / nBluestein;
        bk2[0] = bk1[0] * scale;
        bk2[1] = bk1[1] * scale;
        for (int i = 2; i < 2 * n; i += 2) {
            bk2[i] = bk1[i] * scale;
            bk2[i + 1] = bk1[i + 1] * scale;
            bk2[2 * nBluestein - i] = bk2[i];
            bk2[2 * nBluestein - i + 1] = bk2[i + 1];
        }
        cftbsub(2 * nBluestein, bk2, 0, ip, nw, w);
    }

    private void makewt(int nw) {
        int j, nwh, nw0, nw1;
        double delta, wn4r, wk1r, wk1i, wk3r, wk3i;
        double delta2, deltaj, deltaj3;

        ip[0] = nw;
        ip[1] = 1;
        if (nw > 2) {
            nwh = nw >> 1;
            delta = 0.785398163397448278999490867136046290 / nwh;
            delta2 = delta * 2;
            wn4r = Math.cos(delta * nwh);
            w[0] = 1;
            w[1] = wn4r;
            if (nwh == 4) {
                w[2] = Math.cos(delta2);
                w[3] = Math.sin(delta2);
            } else if (nwh > 4) {
                makeipt(nw);
                w[2] = 0.5 / Math.cos(delta2);
                w[3] = 0.5 / Math.cos(delta * 6);
                for (j = 4; j < nwh; j += 4) {
                    deltaj = delta * j;
                    deltaj3 = 3 * deltaj;
                    w[j] = Math.cos(deltaj);
                    w[j + 1] = Math.sin(deltaj);
                    w[j + 2] = Math.cos(deltaj3);
                    w[j + 3] = -Math.sin(deltaj3);
                }
            }
            nw0 = 0;
            while (nwh > 2) {
                nw1 = nw0 + nwh;
                nwh >>= 1;
                w[nw1] = 1;
                w[nw1 + 1] = wn4r;
                if (nwh == 4) {
                    wk1r = w[nw0 + 4];
                    wk1i = w[nw0 + 5];
                    w[nw1 + 2] = wk1r;
                    w[nw1 + 3] = wk1i;
                } else if (nwh > 4) {
                    wk1r = w[nw0 + 4];
                    wk3r = w[nw0 + 6];
                    w[nw1 + 2] = 0.5 / wk1r;
                    w[nw1 + 3] = 0.5 / wk3r;
                    for (j = 4; j < nwh; j += 4) {
                        int idx1 = nw0 + 2 * j;
                        int idx2 = nw1 + j;
                        wk1r = w[idx1];
                        wk1i = w[idx1 + 1];
                        wk3r = w[idx1 + 2];
                        wk3i = w[idx1 + 3];
                        w[idx2] = wk1r;
                        w[idx2 + 1] = wk1i;
                        w[idx2 + 2] = wk3r;
                        w[idx2 + 3] = wk3i;
                    }
                }
                nw0 = nw1;
            }
        }
    }

    private void makeipt(int nw) {
        int j, l, m, m2, p, q;

        ip[2] = 0;
        ip[3] = 16;
        m = 2;
        for (l = nw; l > 32; l >>= 2) {
            m2 = m << 1;
            q = m2 << 3;
            for (j = m; j < m2; j++) {
                p = ip[j] << 2;
                ip[m + j] = p;
                ip[m2 + j] = p + q;
            }
            m = m2;
        }
    }

    private void makect(int nc, double[] c, int startc) {
        int j, nch;
        double delta, deltaj;

        ip[1] = nc;
        if (nc > 1) {
            nch = nc >> 1;
            delta = 0.785398163397448278999490867136046290 / nch;
            c[startc] = Math.cos(delta * nch);
            c[startc + nch] = 0.5 * c[startc];
            for (j = 1; j < nch; j++) {
                deltaj = delta * j;
                c[startc + j] = 0.5 * Math.cos(deltaj);
                c[startc + nc - j] = 0.5 * Math.sin(deltaj);
            }
        }
    }

    private void bluestein_real_forward(final double[] a, final int offa) {
        final double[] ak = new double[2 * nBluestein];
        int nthreads = ConcurrencyUtils.getNumberOfThreads();
        if ((nthreads > 1) && (n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads())) {
            nthreads = 2;
            if ((nthreads >= 4) && (n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_4Threads())) {
                nthreads = 4;
            }
            Future<?>[] futures = new Future[nthreads];
            int k = n / nthreads;
            for (int i = 0; i < nthreads; i++) {
                final int firstIdx = i * k;
                final int lastIdx = (i == (nthreads - 1)) ? n : firstIdx + k;
                futures[i] = ConcurrencyUtils.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = firstIdx; i < lastIdx; i++) {
                            int idx1 = 2 * i;
                            int idx2 = idx1 + 1;
                            int idx3 = offa + i;
                            ak[idx1] = a[idx3] * bk1[idx1];
                            ak[idx2] = -a[idx3] * bk1[idx2];
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);

            cftbsub(2 * nBluestein, ak, 0, ip, nw, w);

            k = nBluestein / nthreads;
            for (int i = 0; i < nthreads; i++) {
                final int firstIdx = i * k;
                final int lastIdx = (i == (nthreads - 1)) ? nBluestein : firstIdx + k;
                futures[i] = ConcurrencyUtils.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = firstIdx; i < lastIdx; i++) {
                            int idx1 = 2 * i;
                            int idx2 = idx1 + 1;
                            double im = ak[idx1] * bk2[idx2] + ak[idx2] * bk2[idx1];
                            ak[idx1] = ak[idx1] * bk2[idx1] - ak[idx2] * bk2[idx2];
                            ak[idx2] = im;
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);

        } else {

            for (int i = 0; i < n; i++) {
                int idx1 = 2 * i;
                int idx2 = idx1 + 1;
                int idx3 = offa + i;
                ak[idx1] = a[idx3] * bk1[idx1];
                ak[idx2] = -a[idx3] * bk1[idx2];
            }

            cftbsub(2 * nBluestein, ak, 0, ip, nw, w);

            for (int i = 0; i < nBluestein; i++) {
                int idx1 = 2 * i;
                int idx2 = idx1 + 1;
                double im = ak[idx1] * bk2[idx2] + ak[idx2] * bk2[idx1];
                ak[idx1] = ak[idx1] * bk2[idx1] - ak[idx2] * bk2[idx2];
                ak[idx2] = im;
            }
        }

        cftfsub(2 * nBluestein, ak, 0, ip, nw, w);

        if (n % 2 == 0) {
            a[offa] = bk1[0] * ak[0] + bk1[1] * ak[1];
            a[offa + 1] = bk1[n] * ak[n] + bk1[n + 1] * ak[n + 1];
            for (int i = 1; i < n / 2; i++) {
                int idx1 = 2 * i;
                int idx2 = idx1 + 1;
                a[offa + idx1] = bk1[idx1] * ak[idx1] + bk1[idx2] * ak[idx2];
                a[offa + idx2] = -bk1[idx2] * ak[idx1] + bk1[idx1] * ak[idx2];
            }
        } else {
            a[offa] = bk1[0] * ak[0] + bk1[1] * ak[1];
            a[offa + 1] = -bk1[n] * ak[n - 1] + bk1[n - 1] * ak[n];
            for (int i = 1; i < (n - 1) / 2; i++) {
                int idx1 = 2 * i;
                int idx2 = idx1 + 1;
                a[offa + idx1] = bk1[idx1] * ak[idx1] + bk1[idx2] * ak[idx2];
                a[offa + idx2] = -bk1[idx2] * ak[idx1] + bk1[idx1] * ak[idx2];
            }
            a[offa + n - 1] = bk1[n - 1] * ak[n - 1] + bk1[n] * ak[n];
        }

    }

    void rfftf(final double[] a, final int offa) {
        if (n == 1) {
            return;
        }
        int l1, l2, na, kh, nf, ip, iw, ido, idl1;

        final double[] ch = new double[n];
        final int twon = 2 * n;
        nf = (int) wtable_r[1 + twon];
        na = 1;
        l2 = n;
        iw = twon - 1;
        for (int k1 = 1; k1 <= nf; ++k1) {
            kh = nf - k1;
            ip = (int) wtable_r[kh + 2 + twon];
            l1 = l2 / ip;
            ido = n / l2;
            idl1 = ido * l1;
            iw -= (ip - 1) * ido;
            na = 1 - na;
            switch (ip) {
                case 2:
                    if (na == 0) {
                        radf2(ido, l1, a, offa, ch, 0, iw);
                    } else {
                        radf2(ido, l1, ch, 0, a, offa, iw);
                    }
                    break;
                case 3:
                    if (na == 0) {
                        radf3(ido, l1, a, offa, ch, 0, iw);
                    } else {
                        radf3(ido, l1, ch, 0, a, offa, iw);
                    }
                    break;
                case 4:
                    if (na == 0) {
                        radf4(ido, l1, a, offa, ch, 0, iw);
                    } else {
                        radf4(ido, l1, ch, 0, a, offa, iw);
                    }
                    break;
                case 5:
                    if (na == 0) {
                        radf5(ido, l1, a, offa, ch, 0, iw);
                    } else {
                        radf5(ido, l1, ch, 0, a, offa, iw);
                    }
                    break;
                default:
                    if (ido == 1) {
                        na = 1 - na;
                    }
                    if (na == 0) {
                        radfg(ido, ip, l1, idl1, a, offa, ch, 0, iw);
                        na = 1;
                    } else {
                        radfg(ido, ip, l1, idl1, ch, 0, a, offa, iw);
                        na = 0;
                    }
                    break;
            }
            l2 = l1;
        }
        if (na == 1) {
            return;
        }
        System.arraycopy(ch, 0, a, offa, n);
    }

    void radf2(final int ido, final int l1, final double[] in, final int in_off, final double[] out,
               final int out_off, final int offset) {
        int i, ic, idx0, idx1, idx2, idx3, idx4;
        double t1i, t1r, w1r, w1i;
        int iw1;
        iw1 = offset;
        idx0 = l1 * ido;
        idx1 = 2 * ido;
        for (int k = 0; k < l1; k++) {
            int oidx1 = out_off + k * idx1;
            int oidx2 = oidx1 + idx1 - 1;
            int iidx1 = in_off + k * ido;
            int iidx2 = iidx1 + idx0;

            double i1r = in[iidx1];
            double i2r = in[iidx2];

            out[oidx1] = i1r + i2r;
            out[oidx2] = i1r - i2r;
        }
        if (ido < 2) {
            return;
        }
        if (ido != 2) {
            for (int k = 0; k < l1; k++) {
                idx1 = k * ido;
                idx2 = 2 * idx1;
                idx3 = idx2 + ido;
                idx4 = idx1 + idx0;
                for (i = 2; i < ido; i += 2) {
                    ic = ido - i;
                    int widx1 = i - 1 + iw1;
                    int oidx1 = out_off + i + idx2;
                    int oidx2 = out_off + ic + idx3;
                    int iidx1 = in_off + i + idx1;
                    int iidx2 = in_off + i + idx4;

                    double a1i = in[iidx1 - 1];
                    double a1r = in[iidx1];
                    double a2i = in[iidx2 - 1];
                    double a2r = in[iidx2];

                    w1r = wtable_r[widx1 - 1];
                    w1i = wtable_r[widx1];

                    t1r = w1r * a2i + w1i * a2r;
                    t1i = w1r * a2r - w1i * a2i;

                    out[oidx1] = a1r + t1i;
                    out[oidx1 - 1] = a1i + t1r;

                    out[oidx2] = t1i - a1r;
                    out[oidx2 - 1] = a1i - t1r;
                }
            }
            if (ido % 2 == 1) {
                return;
            }
        }
        idx2 = 2 * idx1;
        for (int k = 0; k < l1; k++) {
            idx1 = k * ido;
            int oidx1 = out_off + idx2 + ido;
            int iidx1 = in_off + ido - 1 + idx1;

            out[oidx1] = -in[iidx1 + idx0];
            out[oidx1 - 1] = in[iidx1];
        }
    }

    void radf3(final int ido, final int l1, final double[] in, final int in_off, final double[] out,
               final int out_off, final int offset) {
        final double taur = -0.5;
        final double taui = 0.866025403784438707610604524234076962;
        int i, ic;
        double ci2, di2, di3, cr2, dr2, dr3, ti2, ti3, tr2, tr3, w1r, w2r, w1i, w2i;
        int iw1, iw2;
        iw1 = offset;
        iw2 = iw1 + ido;

        int idx0 = l1 * ido;
        for (int k = 0; k < l1; k++) {
            int idx1 = k * ido;
            int idx3 = 2 * idx0;
            int idx4 = (3 * k + 1) * ido;
            int iidx1 = in_off + idx1;
            int iidx2 = iidx1 + idx0;
            int iidx3 = iidx1 + idx3;
            double i1r = in[iidx1];
            double i2r = in[iidx2];
            double i3r = in[iidx3];
            cr2 = i2r + i3r;
            out[out_off + 3 * idx1] = i1r + cr2;
            out[out_off + idx4 + ido] = taui * (i3r - i2r);
            out[out_off + ido - 1 + idx4] = i1r + taur * cr2;
        }
        if (ido == 1) {
            return;
        }
        for (int k = 0; k < l1; k++) {
            int idx3 = k * ido;
            int idx4 = 3 * idx3;
            int idx5 = idx3 + idx0;
            int idx6 = idx5 + idx0;
            int idx7 = idx4 + ido;
            int idx8 = idx7 + ido;
            for (i = 2; i < ido; i += 2) {
                ic = ido - i;
                int widx1 = i - 1 + iw1;
                int widx2 = i - 1 + iw2;

                w1r = wtable_r[widx1 - 1];
                w1i = wtable_r[widx1];
                w2r = wtable_r[widx2 - 1];
                w2i = wtable_r[widx2];

                int idx9 = in_off + i;
                int idx10 = out_off + i;
                int idx11 = out_off + ic;
                int iidx1 = idx9 + idx3;
                int iidx2 = idx9 + idx5;
                int iidx3 = idx9 + idx6;

                double i1i = in[iidx1 - 1];
                double i1r = in[iidx1];
                double i2i = in[iidx2 - 1];
                double i2r = in[iidx2];
                double i3i = in[iidx3 - 1];
                double i3r = in[iidx3];

                dr2 = w1r * i2i + w1i * i2r;
                di2 = w1r * i2r - w1i * i2i;
                dr3 = w2r * i3i + w2i * i3r;
                di3 = w2r * i3r - w2i * i3i;
                cr2 = dr2 + dr3;
                ci2 = di2 + di3;
                tr2 = i1i + taur * cr2;
                ti2 = i1r + taur * ci2;
                tr3 = taui * (di2 - di3);
                ti3 = taui * (dr3 - dr2);

                int oidx1 = idx10 + idx4;
                int oidx2 = idx11 + idx7;
                int oidx3 = idx10 + idx8;

                out[oidx1 - 1] = i1i + cr2;
                out[oidx1] = i1r + ci2;
                out[oidx2 - 1] = tr2 - tr3;
                out[oidx2] = ti3 - ti2;
                out[oidx3 - 1] = tr2 + tr3;
                out[oidx3] = ti2 + ti3;
            }
        }
    }

    void radf4(final int ido, final int l1, final double[] in, final int in_off, final double[] out,
               final int out_off, final int offset) {
        final double hsqt2 = 0.707106781186547572737310929369414225;
        int i, ic;
        double ci2, ci3, ci4, cr2, cr3, cr4, ti1, ti2, ti3, ti4, tr1, tr2, tr3, tr4, w1r, w1i, w2r, w2i, w3r, w3i;
        int iw1, iw2, iw3;
        iw1 = offset;
        iw2 = offset + ido;
        iw3 = iw2 + ido;
        int idx0 = l1 * ido;
        for (int k = 0; k < l1; k++) {
            int idx1 = k * ido;
            int idx2 = 4 * idx1;
            int idx3 = idx1 + idx0;
            int idx4 = idx3 + idx0;
            int idx5 = idx4 + idx0;
            int idx6 = idx2 + ido;
            double i1r = in[in_off + idx1];
            double i2r = in[in_off + idx3];
            double i3r = in[in_off + idx4];
            double i4r = in[in_off + idx5];

            tr1 = i2r + i4r;
            tr2 = i1r + i3r;

            int oidx1 = out_off + idx2;
            int oidx2 = out_off + idx6 + ido;

            out[oidx1] = tr1 + tr2;
            out[oidx2 - 1 + ido + ido] = tr2 - tr1;
            out[oidx2 - 1] = i1r - i3r;
            out[oidx2] = i4r - i2r;
        }
        if (ido < 2) {
            return;
        }
        if (ido != 2) {
            for (int k = 0; k < l1; k++) {
                int idx1 = k * ido;
                int idx2 = idx1 + idx0;
                int idx3 = idx2 + idx0;
                int idx4 = idx3 + idx0;
                int idx5 = 4 * idx1;
                int idx6 = idx5 + ido;
                int idx7 = idx6 + ido;
                int idx8 = idx7 + ido;
                for (i = 2; i < ido; i += 2) {
                    ic = ido - i;
                    int widx1 = i - 1 + iw1;
                    int widx2 = i - 1 + iw2;
                    int widx3 = i - 1 + iw3;
                    w1r = wtable_r[widx1 - 1];
                    w1i = wtable_r[widx1];
                    w2r = wtable_r[widx2 - 1];
                    w2i = wtable_r[widx2];
                    w3r = wtable_r[widx3 - 1];
                    w3i = wtable_r[widx3];

                    int idx9 = in_off + i;
                    int idx10 = out_off + i;
                    int idx11 = out_off + ic;
                    int iidx1 = idx9 + idx1;
                    int iidx2 = idx9 + idx2;
                    int iidx3 = idx9 + idx3;
                    int iidx4 = idx9 + idx4;

                    double i1i = in[iidx1 - 1];
                    double i1r = in[iidx1];
                    double i2i = in[iidx2 - 1];
                    double i2r = in[iidx2];
                    double i3i = in[iidx3 - 1];
                    double i3r = in[iidx3];
                    double i4i = in[iidx4 - 1];
                    double i4r = in[iidx4];

                    cr2 = w1r * i2i + w1i * i2r;
                    ci2 = w1r * i2r - w1i * i2i;
                    cr3 = w2r * i3i + w2i * i3r;
                    ci3 = w2r * i3r - w2i * i3i;
                    cr4 = w3r * i4i + w3i * i4r;
                    ci4 = w3r * i4r - w3i * i4i;
                    tr1 = cr2 + cr4;
                    tr4 = cr4 - cr2;
                    ti1 = ci2 + ci4;
                    ti4 = ci2 - ci4;
                    ti2 = i1r + ci3;
                    ti3 = i1r - ci3;
                    tr2 = i1i + cr3;
                    tr3 = i1i - cr3;

                    int oidx1 = idx10 + idx5;
                    int oidx2 = idx11 + idx6;
                    int oidx3 = idx10 + idx7;
                    int oidx4 = idx11 + idx8;

                    out[oidx1 - 1] = tr1 + tr2;
                    out[oidx4 - 1] = tr2 - tr1;
                    out[oidx1] = ti1 + ti2;
                    out[oidx4] = ti1 - ti2;
                    out[oidx3 - 1] = ti4 + tr3;
                    out[oidx2 - 1] = tr3 - ti4;
                    out[oidx3] = tr4 + ti3;
                    out[oidx2] = tr4 - ti3;
                }
            }
            if (ido % 2 == 1) {
                return;
            }
        }
        for (int k = 0; k < l1; k++) {
            int idx1 = k * ido;
            int idx2 = 4 * idx1;
            int idx3 = idx1 + idx0;
            int idx4 = idx3 + idx0;
            int idx5 = idx4 + idx0;
            int idx6 = idx2 + ido;
            int idx7 = idx6 + ido;
            int idx8 = idx7 + ido;
            int idx9 = in_off + ido;
            int idx10 = out_off + ido;

            double i1i = in[idx9 - 1 + idx1];
            double i2i = in[idx9 - 1 + idx3];
            double i3i = in[idx9 - 1 + idx4];
            double i4i = in[idx9 - 1 + idx5];

            ti1 = -hsqt2 * (i2i + i4i);
            tr1 = hsqt2 * (i2i - i4i);

            out[idx10 - 1 + idx2] = tr1 + i1i;
            out[idx10 - 1 + idx7] = i1i - tr1;
            out[out_off + idx6] = ti1 - i3i;
            out[out_off + idx8] = ti1 + i3i;
        }
    }

    void radf5(final int ido, final int l1, final double[] in, final int in_off, final double[] out,
               final int out_off, final int offset) {
        final double tr11 = 0.309016994374947451262869435595348477;
        final double ti11 = 0.951056516295153531181938433292089030;
        final double tr12 = -0.809016994374947340240566973079694435;
        final double ti12 = 0.587785252292473248125759255344746634;
        int i, ic;
        double ci2, di2, ci4, ci5, di3, di4, di5, ci3, cr2, cr3, dr2, dr3, dr4, dr5, cr5, cr4, ti2, ti3, ti5, ti4,
                tr2, tr3, tr4, tr5, w1r, w1i, w2r, w2i, w3r, w3i, w4r, w4i;
        int iw1, iw2, iw3, iw4;
        iw1 = offset;
        iw2 = iw1 + ido;
        iw3 = iw2 + ido;
        iw4 = iw3 + ido;

        int idx0 = l1 * ido;
        for (int k = 0; k < l1; k++) {
            int idx1 = k * ido;
            int idx2 = 5 * idx1;
            int idx3 = idx2 + ido;
            int idx4 = idx3 + ido;
            int idx5 = idx4 + ido;
            int idx6 = idx5 + ido;
            int idx7 = idx1 + idx0;
            int idx8 = idx7 + idx0;
            int idx9 = idx8 + idx0;
            int idx10 = idx9 + idx0;
            int idx11 = out_off + ido - 1;

            double i1r = in[in_off + idx1];
            double i2r = in[in_off + idx7];
            double i3r = in[in_off + idx8];
            double i4r = in[in_off + idx9];
            double i5r = in[in_off + idx10];

            cr2 = i5r + i2r;
            ci5 = i5r - i2r;
            cr3 = i4r + i3r;
            ci4 = i4r - i3r;

            out[out_off + idx2] = i1r + cr2 + cr3;
            out[idx11 + idx3] = i1r + tr11 * cr2 + tr12 * cr3;
            out[out_off + idx4] = ti11 * ci5 + ti12 * ci4;
            out[idx11 + idx5] = i1r + tr12 * cr2 + tr11 * cr3;
            out[out_off + idx6] = ti12 * ci5 - ti11 * ci4;
        }
        if (ido == 1) {
            return;
        }
        for (int k = 0; k < l1; ++k) {
            int idx1 = k * ido;
            int idx2 = 5 * idx1;
            int idx3 = idx2 + ido;
            int idx4 = idx3 + ido;
            int idx5 = idx4 + ido;
            int idx6 = idx5 + ido;
            int idx7 = idx1 + idx0;
            int idx8 = idx7 + idx0;
            int idx9 = idx8 + idx0;
            int idx10 = idx9 + idx0;
            for (i = 2; i < ido; i += 2) {
                int widx1 = i - 1 + iw1;
                int widx2 = i - 1 + iw2;
                int widx3 = i - 1 + iw3;
                int widx4 = i - 1 + iw4;
                w1r = wtable_r[widx1 - 1];
                w1i = wtable_r[widx1];
                w2r = wtable_r[widx2 - 1];
                w2i = wtable_r[widx2];
                w3r = wtable_r[widx3 - 1];
                w3i = wtable_r[widx3];
                w4r = wtable_r[widx4 - 1];
                w4i = wtable_r[widx4];

                ic = ido - i;
                int idx15 = in_off + i;
                int idx16 = out_off + i;
                int idx17 = out_off + ic;

                int iidx1 = idx15 + idx1;
                int iidx2 = idx15 + idx7;
                int iidx3 = idx15 + idx8;
                int iidx4 = idx15 + idx9;
                int iidx5 = idx15 + idx10;

                double i1i = in[iidx1 - 1];
                double i1r = in[iidx1];
                double i2i = in[iidx2 - 1];
                double i2r = in[iidx2];
                double i3i = in[iidx3 - 1];
                double i3r = in[iidx3];
                double i4i = in[iidx4 - 1];
                double i4r = in[iidx4];
                double i5i = in[iidx5 - 1];
                double i5r = in[iidx5];

                dr2 = w1r * i2i + w1i * i2r;
                di2 = w1r * i2r - w1i * i2i;
                dr3 = w2r * i3i + w2i * i3r;
                di3 = w2r * i3r - w2i * i3i;
                dr4 = w3r * i4i + w3i * i4r;
                di4 = w3r * i4r - w3i * i4i;
                dr5 = w4r * i5i + w4i * i5r;
                di5 = w4r * i5r - w4i * i5i;

                cr2 = dr2 + dr5;
                ci5 = dr5 - dr2;
                cr5 = di2 - di5;
                ci2 = di2 + di5;
                cr3 = dr3 + dr4;
                ci4 = dr4 - dr3;
                cr4 = di3 - di4;
                ci3 = di3 + di4;

                tr2 = i1i + tr11 * cr2 + tr12 * cr3;
                ti2 = i1r + tr11 * ci2 + tr12 * ci3;
                tr3 = i1i + tr12 * cr2 + tr11 * cr3;
                ti3 = i1r + tr12 * ci2 + tr11 * ci3;
                tr5 = ti11 * cr5 + ti12 * cr4;
                ti5 = ti11 * ci5 + ti12 * ci4;
                tr4 = ti12 * cr5 - ti11 * cr4;
                ti4 = ti12 * ci5 - ti11 * ci4;

                int oidx1 = idx16 + idx2;
                int oidx2 = idx17 + idx3;
                int oidx3 = idx16 + idx4;
                int oidx4 = idx17 + idx5;
                int oidx5 = idx16 + idx6;

                out[oidx1 - 1] = i1i + cr2 + cr3;
                out[oidx1] = i1r + ci2 + ci3;
                out[oidx3 - 1] = tr2 + tr5;
                out[oidx2 - 1] = tr2 - tr5;
                out[oidx3] = ti2 + ti5;
                out[oidx2] = ti5 - ti2;
                out[oidx5 - 1] = tr3 + tr4;
                out[oidx4 - 1] = tr3 - tr4;
                out[oidx5] = ti3 + ti4;
                out[oidx4] = ti4 - ti3;
            }
        }
    }

    void radfg(final int ido, final int ip, final int l1, final int idl1, final double[] in, final int in_off,
               final double[] out, final int out_off, final int offset) {
        int idij, ipph, j2, ic, jc, lc, is, nbd;
        double dc2, ai1, ai2, ar1, ar2, ds2, dcp, arg, dsp, ar1h, ar2h, w1r, w1i;
        int iw1 = offset;

        arg = TWO_PI / (double) ip;
        dcp = Math.cos(arg);
        dsp = Math.sin(arg);
        ipph = (ip + 1) / 2;
        nbd = (ido - 1) / 2;
        if (ido != 1) {
            for (int ik = 0; ik < idl1; ik++) {
                out[out_off + ik] = in[in_off + ik];
            }
            for (int j = 1; j < ip; j++) {
                int idx1 = j * l1 * ido;
                for (int k = 0; k < l1; k++) {
                    int idx2 = k * ido + idx1;
                    out[out_off + idx2] = in[in_off + idx2];
                }
            }
            if (nbd <= l1) {
                is = -ido;
                for (int j = 1; j < ip; j++) {
                    is += ido;
                    idij = is - 1;
                    int idx1 = j * l1 * ido;
                    for (int i = 2; i < ido; i += 2) {
                        idij += 2;
                        int idx2 = idij + iw1;
                        int idx4 = in_off + i;
                        int idx5 = out_off + i;
                        w1r = wtable_r[idx2 - 1];
                        w1i = wtable_r[idx2];
                        for (int k = 0; k < l1; k++) {
                            int idx3 = k * ido + idx1;
                            int oidx1 = idx5 + idx3;
                            int iidx1 = idx4 + idx3;
                            double i1i = in[iidx1 - 1];
                            double i1r = in[iidx1];

                            out[oidx1 - 1] = w1r * i1i + w1i * i1r;
                            out[oidx1] = w1r * i1r - w1i * i1i;
                        }
                    }
                }
            } else {
                is = -ido;
                for (int j = 1; j < ip; j++) {
                    is += ido;
                    int idx1 = j * l1 * ido;
                    for (int k = 0; k < l1; k++) {
                        idij = is - 1;
                        int idx3 = k * ido + idx1;
                        for (int i = 2; i < ido; i += 2) {
                            idij += 2;
                            int idx2 = idij + iw1;
                            w1r = wtable_r[idx2 - 1];
                            w1i = wtable_r[idx2];
                            int oidx1 = out_off + i + idx3;
                            int iidx1 = in_off + i + idx3;
                            double i1i = in[iidx1 - 1];
                            double i1r = in[iidx1];

                            out[oidx1 - 1] = w1r * i1i + w1i * i1r;
                            out[oidx1] = w1r * i1r - w1i * i1i;
                        }
                    }
                }
            }
            if (nbd >= l1) {
                for (int j = 1; j < ipph; j++) {
                    jc = ip - j;
                    int idx1 = j * l1 * ido;
                    int idx2 = jc * l1 * ido;
                    for (int k = 0; k < l1; k++) {
                        int idx3 = k * ido + idx1;
                        int idx4 = k * ido + idx2;
                        for (int i = 2; i < ido; i += 2) {
                            int idx5 = in_off + i;
                            int idx6 = out_off + i;
                            int iidx1 = idx5 + idx3;
                            int iidx2 = idx5 + idx4;
                            int oidx1 = idx6 + idx3;
                            int oidx2 = idx6 + idx4;
                            double o1i = out[oidx1 - 1];
                            double o1r = out[oidx1];
                            double o2i = out[oidx2 - 1];
                            double o2r = out[oidx2];

                            in[iidx1 - 1] = o1i + o2i;
                            in[iidx1] = o1r + o2r;

                            in[iidx2 - 1] = o1r - o2r;
                            in[iidx2] = o2i - o1i;
                        }
                    }
                }
            } else {
                for (int j = 1; j < ipph; j++) {
                    jc = ip - j;
                    int idx1 = j * l1 * ido;
                    int idx2 = jc * l1 * ido;
                    for (int i = 2; i < ido; i += 2) {
                        int idx5 = in_off + i;
                        int idx6 = out_off + i;
                        for (int k = 0; k < l1; k++) {
                            int idx3 = k * ido + idx1;
                            int idx4 = k * ido + idx2;
                            int iidx1 = idx5 + idx3;
                            int iidx2 = idx5 + idx4;
                            int oidx1 = idx6 + idx3;
                            int oidx2 = idx6 + idx4;
                            double o1i = out[oidx1 - 1];
                            double o1r = out[oidx1];
                            double o2i = out[oidx2 - 1];
                            double o2r = out[oidx2];

                            in[iidx1 - 1] = o1i + o2i;
                            in[iidx1] = o1r + o2r;
                            in[iidx2 - 1] = o1r - o2r;
                            in[iidx2] = o2i - o1i;
                        }
                    }
                }
            }
        } else {
            System.arraycopy(out, out_off, in, in_off, idl1);
        }
        for (int j = 1; j < ipph; j++) {
            jc = ip - j;
            int idx1 = j * l1 * ido;
            int idx2 = jc * l1 * ido;
            for (int k = 0; k < l1; k++) {
                int idx3 = k * ido + idx1;
                int idx4 = k * ido + idx2;
                int oidx1 = out_off + idx3;
                int oidx2 = out_off + idx4;
                double o1r = out[oidx1];
                double o2r = out[oidx2];

                in[in_off + idx3] = o1r + o2r;
                in[in_off + idx4] = o2r - o1r;
            }
        }

        ar1 = 1;
        ai1 = 0;
        int idx0 = (ip - 1) * idl1;
        for (int l = 1; l < ipph; l++) {
            lc = ip - l;
            ar1h = dcp * ar1 - dsp * ai1;
            ai1 = dcp * ai1 + dsp * ar1;
            ar1 = ar1h;
            int idx1 = l * idl1;
            int idx2 = lc * idl1;
            for (int ik = 0; ik < idl1; ik++) {
                int idx3 = out_off + ik;
                int idx4 = in_off + ik;
                out[idx3 + idx1] = in[idx4] + ar1 * in[idx4 + idl1];
                out[idx3 + idx2] = ai1 * in[idx4 + idx0];
            }
            dc2 = ar1;
            ds2 = ai1;
            ar2 = ar1;
            ai2 = ai1;
            for (int j = 2; j < ipph; j++) {
                jc = ip - j;
                ar2h = dc2 * ar2 - ds2 * ai2;
                ai2 = dc2 * ai2 + ds2 * ar2;
                ar2 = ar2h;
                int idx3 = j * idl1;
                int idx4 = jc * idl1;
                for (int ik = 0; ik < idl1; ik++) {
                    int idx5 = out_off + ik;
                    int idx6 = in_off + ik;
                    out[idx5 + idx1] += ar2 * in[idx6 + idx3];
                    out[idx5 + idx2] += ai2 * in[idx6 + idx4];
                }
            }
        }
        for (int j = 1; j < ipph; j++) {
            int idx1 = j * idl1;
            for (int ik = 0; ik < idl1; ik++) {
                out[out_off + ik] += in[in_off + ik + idx1];
            }
        }

        if (ido >= l1) {
            for (int k = 0; k < l1; k++) {
                int idx1 = k * ido;
                int idx2 = idx1 * ip;
                for (int i = 0; i < ido; i++) {
                    in[in_off + i + idx2] = out[out_off + i + idx1];
                }
            }
        } else {
            for (int i = 0; i < ido; i++) {
                for (int k = 0; k < l1; k++) {
                    int idx1 = k * ido;
                    in[in_off + i + idx1 * ip] = out[out_off + i + idx1];
                }
            }
        }
        int idx01 = ip * ido;
        for (int j = 1; j < ipph; j++) {
            jc = ip - j;
            j2 = 2 * j;
            int idx1 = j * l1 * ido;
            int idx2 = jc * l1 * ido;
            int idx3 = j2 * ido;
            for (int k = 0; k < l1; k++) {
                int idx4 = k * ido;
                int idx5 = idx4 + idx1;
                int idx6 = idx4 + idx2;
                int idx7 = k * idx01;
                in[in_off + ido - 1 + idx3 - ido + idx7] = out[out_off + idx5];
                in[in_off + idx3 + idx7] = out[out_off + idx6];
            }
        }
        if (ido == 1) {
            return;
        }
        if (nbd >= l1) {
            for (int j = 1; j < ipph; j++) {
                jc = ip - j;
                j2 = 2 * j;
                int idx1 = j * l1 * ido;
                int idx2 = jc * l1 * ido;
                int idx3 = j2 * ido;
                for (int k = 0; k < l1; k++) {
                    int idx4 = k * idx01;
                    int idx5 = k * ido;
                    for (int i = 2; i < ido; i += 2) {
                        ic = ido - i;
                        int idx6 = in_off + i;
                        int idx7 = in_off + ic;
                        int idx8 = out_off + i;
                        int iidx1 = idx6 + idx3 + idx4;
                        int iidx2 = idx7 + idx3 - ido + idx4;
                        int oidx1 = idx8 + idx5 + idx1;
                        int oidx2 = idx8 + idx5 + idx2;
                        double o1i = out[oidx1 - 1];
                        double o1r = out[oidx1];
                        double o2i = out[oidx2 - 1];
                        double o2r = out[oidx2];

                        in[iidx1 - 1] = o1i + o2i;
                        in[iidx2 - 1] = o1i - o2i;
                        in[iidx1] = o1r + o2r;
                        in[iidx2] = o2r - o1r;
                    }
                }
            }
        } else {
            for (int j = 1; j < ipph; j++) {
                jc = ip - j;
                j2 = 2 * j;
                int idx1 = j * l1 * ido;
                int idx2 = jc * l1 * ido;
                int idx3 = j2 * ido;
                for (int i = 2; i < ido; i += 2) {
                    ic = ido - i;
                    int idx6 = in_off + i;
                    int idx7 = in_off + ic;
                    int idx8 = out_off + i;
                    for (int k = 0; k < l1; k++) {
                        int idx4 = k * idx01;
                        int idx5 = k * ido;
                        int iidx1 = idx6 + idx3 + idx4;
                        int iidx2 = idx7 + idx3 - ido + idx4;
                        int oidx1 = idx8 + idx5 + idx1;
                        int oidx2 = idx8 + idx5 + idx2;
                        double o1i = out[oidx1 - 1];
                        double o1r = out[oidx1];
                        double o2i = out[oidx2 - 1];
                        double o2r = out[oidx2];

                        in[iidx1 - 1] = o1i + o2i;
                        in[iidx2 - 1] = o1i - o2i;
                        in[iidx1] = o1r + o2r;
                        in[iidx2] = o2r - o1r;
                    }
                }
            }
        }
    }

    private void rftfsub(int n, double[] a, int offa, int nc, double[] c, int startc) {
        int k, kk, ks, m;
        double wkr, wki, xr, xi, yr, yi;
        int idx1, idx2;

        m = n >> 1;
        ks = 2 * nc / m;
        kk = 0;
        for (int j = 2; j < m; j += 2) {
            k = n - j;
            kk += ks;
            wkr = 0.5 - c[startc + nc - kk];
            wki = c[startc + kk];
            idx1 = offa + j;
            idx2 = offa + k;
            xr = a[idx1] - a[idx2];
            xi = a[idx1 + 1] + a[idx2 + 1];
            yr = wkr * xr - wki * xi;
            yi = wkr * xi + wki * xr;
            a[idx1] -= yr;
            a[idx1 + 1] = yi - a[idx1 + 1];
            a[idx2] += yr;
            a[idx2 + 1] = yi - a[idx2 + 1];
        }
        a[offa + m + 1] = -a[offa + m + 1];
    }
}
