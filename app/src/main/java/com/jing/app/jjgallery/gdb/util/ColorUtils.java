package com.jing.app.jjgallery.gdb.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.jing.app.jjgallery.gdb.model.bean.HsvColorBean;

import java.util.Random;

/**
 * Created by JingYang on 2016/7/13 0013.
 * Description:
 */
public class ColorUtils {

    private static final String TAG = "ColorUtils";
    private static Random random;

    /**
     * 随机亮色
     * 利用hsv模型，v大于0.5, h范围在out of 210-270(滤除青到蓝一半至蓝到红一半之间的颜色)定义为亮色
     * H是色彩,S是深浅,V是明暗
     * 关于HSV模型可以参考http://blog.csdn.net/viewcode/article/details/8203728
     *
     * @return
     */
    public static int randomLightColor() {
        if (random == null) {
            random = new Random();
        }
        float[] hsv = new float[3];
        hsv[0] = random.nextFloat() * 360;
        if (hsv[0] > 210 && hsv[0] < 240) {
            hsv[0] -= 30;
        } else if (hsv[0] >= 240 && hsv[0] < 270) {
            hsv[0] += 30;
        }
        hsv[1] = random.nextFloat();
        hsv[2] = random.nextFloat();
        if (hsv[2] < 0.5f) {
            hsv[2] += 0.5f;
        }

//        Log.d(TAG, "hsv[" + hsv[0] + "," + hsv[1] + "," + hsv[2] + "]");
        return Color.HSVToColor(hsv);
    }

    /**
     * 随机暗色，作为背景色，配合白色文字
     * 利用hsv模型，s大于0.7, v大于0.5
     * H是色彩,S是深浅,V是明暗
     * 关于HSV模型可以参考http://blog.csdn.net/viewcode/article/details/8203728
     *
     * @return
     */
    public static int randomWhiteTextBgColor() {
        if (random == null) {
            random = new Random();
        }
        float[] hsv = new float[3];
        hsv[0] = random.nextFloat() * 360;
        hsv[1] = random.nextFloat();
        if (hsv[1] < 0.7f) {
            hsv[1] = 0.7f + hsv[1] / 0.7f * 0.3f;
        }
        hsv[2] = random.nextFloat();
        if (hsv[2] < 0.5f) {
            hsv[2] += 0.5f;
        }

//        Log.d(TAG, "hsv[" + hsv[0] + "," + hsv[1] + "," + hsv[2] + "]");
        return Color.HSVToColor(hsv);
    }

    /**
     * H是色彩,S是深浅,V是明暗
     *
     * @param color
     * @return
     */
    public static int generateForgroundColorForBg(int color) {
        if (isDeepColor(color)) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
    }

    /**
     * 判断是不是深色
     *
     * @return
     */
    public static boolean isDeepColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    /**
     * 随机暗色，作为背景色，配合白色文字
     * 利用hsv模型，s大于0.7, v大于0.5
     * hStart>0, hStart+hArg < 360
     *
     * @param hStart hsv模型中h的开始值
     * @param hArg   hsv模型中从hStart扫过的角度
     * @return
     */
    public static int randomBgColor(int hStart, int hArg) {
        if (random == null) {
            random = new Random();
        }
        float[] hsv = new float[3];
        hsv[0] = hStart + random.nextFloat() * hArg;
        hsv[1] = random.nextFloat();
        if (hsv[1] < 0.7f) {
            hsv[1] = 0.7f + hsv[1] / 0.7f * 0.3f;
        }
        hsv[2] = random.nextFloat();
        if (hsv[2] < 0.5f) {
            hsv[2] += 0.5f;
        }

//        Log.d(TAG, "hsv[" + hsv[0] + "," + hsv[1] + "," + hsv[2] + "]");
        return Color.HSVToColor(hsv);
    }

    /**
     * 如果s v随机，则随机暗色，作为背景色，配合白色文字
     *
     * @param bean
     * @return
     */
    public static int randomColorBy(HsvColorBean bean) {
        if (random == null) {
            random = new Random();
        }
        float[] hsv = new float[3];
        if (bean.gethStart() >= 0 && bean.gethArg() >= 0) {
            hsv[0] = bean.gethStart() + random.nextFloat() * bean.gethArg();
        } else {
            hsv[0] = random.nextFloat() * 360;
        }
        if (bean.getS() >= 0) {
            hsv[1] = bean.getS();
        } else {
            hsv[1] = random.nextFloat();
            if (bean.getType() == 1) {
                if (hsv[1] < 0.7f) {
                    hsv[1] = 0.7f + hsv[1] * 0.3f;
                }
            } else if (bean.getType() == 2) {
                if (hsv[1] > 0.3f) {
                    hsv[1] = 0.3f - hsv[1] * 0.3f;
                }
            }
        }
        if (bean.getV() >= 0) {
            hsv[2] = bean.getV();
        } else {
            hsv[2] = random.nextFloat();
            if (bean.getType() == 1) {
                if (hsv[2] < 0.5f) {
                    hsv[2] += 0.5f;
                }
            } else if (bean.getType() == 2) {
                if (hsv[2] > 0.5f) {
                    hsv[2] -= 0.5f;
                }
            }
        }
        return Color.HSVToColor(hsv);
    }

    /**
     * http://blog.csdn.net/janice0529/article/details/49207939
     * 通过改变颜色分量来改变纯色图片的颜色
     * 要求bitmap的有色区域是纯黑色的
     *
     * @param bitmap original bitmap
     * @param color  target color
     * @return
     */
    private Drawable getUpdatedDrawable(Bitmap bitmap, int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        /**
         * 第一行决定红色 R
         第二行决定绿色 G
         第三行决定蓝色 B
         第四行决定了透明度 A
         第五列是颜色的偏移量
         */
        float[] src = new float[]{
                1, 0, 0, 0, red,
                0, 1, 0, 0, green,
                0, 0, 1, 0, blue,
                0, 0, 0, 1, 0};
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(src);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(src));
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        // 5.通过指定了RGBA矩阵的Paint把原图画到空白图片上
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return new BitmapDrawable(newBitmap);
    }

    public static void updateIconColor(ImageView icon, int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        icon.setColorFilter(Color.argb(255, red, green, blue));
    }

    public static void updateIconColorWithAlpha(ImageView icon, int color) {
        int alpha = (color & 0xff000000) >> 24;
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        setIconColor(icon, red, green, blue, alpha);
    }

    private static void setIconColor(ImageView icon, int r, int g, int b, int a) {
        float[] colorMatrix = new float[]{
                0, 0, 0, 0, r,
                0, 0, 0, 0, g,
                0, 0, 0, 0, b,
                0, 0, 0, (float) a / 255, 0
        };
        icon.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    /**
     * 获取targetView所在bitmap区域的平均色值
     * @param bitmap
     * @param targetView
     * @return
     */
    public static int averageImageColor(Bitmap bitmap, View targetView) {
        int[] pixels = new int[targetView.getWidth() * targetView.getHeight()];
        int offsetX = targetView.getLeft();
        int offsetY = targetView.getTop();
        long red = 0;
        long green = 0;
        long blue = 0;
        for (int i = 0; i < targetView.getWidth(); i ++) {
            for (int j = 0; j < targetView.getHeight(); j ++) {
                int color = bitmap.getPixel(offsetX + i , offsetY + j);
                red += Color.red(color);
                green += Color.red(color);
                blue += Color.red(color);
            }
        }
        red = red / pixels.length;
        green = green / pixels.length;
        blue = blue / pixels.length;
        return (int) ((0xFF << 24) | (red << 16) | (green << 8) | blue);
    }
}
