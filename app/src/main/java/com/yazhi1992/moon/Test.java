package com.yazhi1992.moon;

import android.view.View;
import android.widget.TextView;

/**
 * Created by zengyazhi on 2017/12/27.
 */

public class Test {
    public static final String test = "aadf";

    public static void gotoA() {

    }

    private void test() {
        TextView textView = new TextView(BaseApplication.instance);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
