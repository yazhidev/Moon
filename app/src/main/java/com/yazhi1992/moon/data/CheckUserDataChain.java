package com.yazhi1992.moon.data;

import com.yazhi1992.moon.ActivityRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengyazhi on 2018/2/12.
 * <p>
 * 数据监测类，进入app，获取远端user数据后检查
 * 1. 是否绑定另一半，没有则跳转绑定页面
 * 2. 是否设置性别，没有跳转设置性别
 */

public class CheckUserDataChain {

    private CheckUserDataChain() {
    }

    private static class CheckUserDataChainHolder {
        private static CheckUserDataChain INSTANCE = new CheckUserDataChain();
    }

    public static CheckUserDataChain getInstance() {
        return CheckUserDataChainHolder.INSTANCE;
    }

    private List<ICheckDataFilter> mChainList = new ArrayList<>();
    private int mChainIndex;

    public void add(ICheckDataFilter filter) {
        mChainList.add(filter);
    }

    public void processChain() {
        if (mChainList.size() > 0 && mChainList.size() > mChainIndex) {
            ICheckDataFilter iCheckDataFilter = mChainList.get(mChainIndex);
            mChainIndex++;
            iCheckDataFilter.check(new ICheckDataCallBack() {
                @Override
                public void doContinue() {
                    processChain();
                }

                @Override
                public void onErr() {
                    ActivityRouter.gotoLogin();
                }
            });
        } else{
            //全部通过
            ActivityRouter.gotoHomePage();
        }
    }

}
