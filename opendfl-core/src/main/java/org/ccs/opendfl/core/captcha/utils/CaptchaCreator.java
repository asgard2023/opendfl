package org.ccs.opendfl.core.captcha.utils;

import org.ccs.opendfl.core.captcha.constant.CaptchaType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 图片验证码
 */
public class CaptchaCreator {
    // 图片的宽度。
    private int width = 160;
    // 图片的高度。
    private int height = 40;
    // 验证码字符个数
    private int codeCount = 4;
    // 验证码干扰线数
    private int lineCount = 10;
    // 验证码
    private String code = null;
    // 验证码图片Buffer
    private BufferedImage buffImg = null;
    private String type = CaptchaType.NUM.getCode();
    private static Random random = new Random();
    private static final String RANDOM_NUM = "1234567890";
    private static final String RANDOM_STRING = "1234567890abcdefghijkmnpkrstuvwxyz";

    public CaptchaCreator() {
        creatImage();
    }

    public CaptchaCreator(int width, int height) {
        this.width = width;
        this.height = height;
        creatImage();
    }

    public CaptchaCreator(int width, int height, int codeCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        creatImage();
    }

    public CaptchaCreator(int width, int height, int codeCount, int lineCount, String type) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        this.type = type;
        creatImage();
    }

    // 生成图片
    private void creatImage() {
        int fontWidth = width / codeCount;// 字体的宽度
        int fontHeight = height - 5;// 字体的高度
        int codeY = height - 8;

        // 图像buffer
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        // 设置背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);


        // 设置字体
        Font font = getFont(fontHeight);
        g.setFont(font);

        // 设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }

        // 添加噪点
        float yawpRate = 0.01f;// 噪声率
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            buffImg.setRGB(x, y, random.nextInt(255));
        }


        String str1 = randomStr(codeCount);// 得到随机字符
        this.code = str1;
        for (int i = 0; i < codeCount; i++) {
            String strRand = str1.substring(i, i + 1);
            g.setColor(getRandColor(1, 255));
            // a为要画出来的东西，x和y表示要画的东西最左侧字符的基线位于此图形上下文坐标系的 (x, y) 位置处
            g.drawString(strRand, i * fontWidth + 3, codeY);
        }


    }

    // 得到随机字符
    private String randomStr(int n) {
        String str1 = RANDOM_NUM;
        if (CaptchaType.STRING.getCode().equals(type)) {
            str1 = RANDOM_STRING;
        }
        StringBuilder sb = new StringBuilder();
        int len = str1.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            sb.append(str1.charAt((int) r));
        }
        return sb.toString();
    }

    // 得到随机颜色
    private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 产生随机字体
     */
    private Font getFont(int size) {
        Font[] fonts = new Font[5];
        fonts[0] = new Font("Ravie", Font.PLAIN, size);
        fonts[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
        fonts[2] = new Font("Fixedsys", Font.PLAIN, size);
        fonts[3] = new Font("Wide Latin", Font.PLAIN, size);
        fonts[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
        return fonts[random.nextInt(5)];
    }

    // 扭曲方法
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (period >> 1)
                    * Math.sin((double) i / period
                    + (6.2831853071795862D * phase)
                    / frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (period >> 1)
                    * Math.sin((double) i / period
                    + (6.2831853071795862D * phase)
                    / frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }


    public void write(OutputStream sos) throws IOException {
        ImageIO.write(buffImg, "png", sos);
        sos.close();
    }

    public BufferedImage getBuffImg() {
        return buffImg;
    }

    public String getCode() {
        return code.toLowerCase();
    }
}