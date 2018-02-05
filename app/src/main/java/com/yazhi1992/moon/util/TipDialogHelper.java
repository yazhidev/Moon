package com.yazhi1992.moon.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.yazhi1992.moon.R;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class TipDialogHelper {

    private TipDialogHelper() {}

    private static class TipDialogHelperHolder {
        private static TipDialogHelper INSTANCE = new TipDialogHelper();
    }

    public static TipDialogHelper getInstance() {
        return TipDialogHelperHolder.INSTANCE;
    }

    public interface OnComfirmListener {
        void comfirm();
    }

    public void showDialog(Context context, String msg, OnComfirmListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .setPositiveButton(context.getString(R.string.comfirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null) {
                            listener.comfirm();
                        }
                    }
                })
                .show();
    }
}
