package com.jing.app.jjgallery.gdb.presenter.game;

import com.jing.app.jjgallery.gdb.view.game.IRandomNumView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 16:49
 */
public class RandomNumPresenter {

    private IRandomNumView view;
    private Random random;

    public RandomNumPresenter(IRandomNumView view) {
        this.view = view;
        random = new Random();
    }

    public void randomNum(String number, String min, String max, boolean isOnly) {
        int nNumber = 1;
        try {
            nNumber = Integer.parseInt(number);
        } catch (Exception e) {
            view.onErrorMessage("输入数量有误");
            return;
        }
        if (nNumber == 0) {
            view.onErrorMessage("输入数量不能为0");
            return;
        }
        int nMin = 0;
        try {
            nMin = Integer.parseInt(min);
        } catch (Exception e) {
            view.onErrorMessage("输入最小值有误");
            return;
        }
        int nMax = 0;
        try {
            nMax = Integer.parseInt(max);
        } catch (Exception e) {
            view.onErrorMessage("输入最大值有误");
            return;
        }
        if (nMin > nMax) {
            view.onErrorMessage("输入最小值大于最大值");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        if (isOnly) {
            if (nNumber > nMax - nMin + 1) {
                view.onErrorMessage("无法产生指定个数的随机数");
            }
            else {
                List<Integer> list = new ArrayList<>();
                for (int i = nMin; i <= nMax; i ++) {
                    list.add(i);
                }
                Collections.shuffle(list);

                for (int i = 0; i < nNumber; i ++) {
                    buffer.append(list.get(i)).append(" ");
                }
                view.showRandomResult(buffer.toString());
            }
        }
        else {
            for (int i = 0; i < nNumber; i ++) {
                int num = nMin + Math.abs(random.nextInt()) % (nMax - nMin);
                buffer.append(num).append(" ");
            }
            view.showRandomResult(buffer.toString());
        }
    }
}
