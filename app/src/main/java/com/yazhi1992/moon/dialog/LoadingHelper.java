package com.yazhi1992.moon.dialog;

import android.app.Activity;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class LoadingHelper {
    private LoadingHelper() {}

    private static class LoadingHelperHolder{
        private static LoadingHelper INSTANCE = new LoadingHelper();
    }

    public static LoadingHelper getInstance() {
        return LoadingHelperHolder.INSTANCE;
    }

    private LoadingDialog mLoadingDialog;

    public void showLoading(Activity activity) {
        if(activity == null) return;
        closeLoading();
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(activity.getFragmentManager());
    }

    public void closeLoading() {
        if(mLoadingDialog == null) return;
        Activity activity = mLoadingDialog.getActivity();
        if(activity != null && activity.isFinishing()) return;
        mLoadingDialog.dismiss();
        mLoadingDialog = null;
    }
}
