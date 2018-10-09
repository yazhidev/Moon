package com.yazhi1992.moon.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.DialogCompleteTravelListBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class CompleteTravelListDialog extends DialogFragment {

    DialogCompleteTravelListBinding mBinding;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_complete_travel_list, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0.5f;
        window.setAttributes(lp);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.hookIcon.setColor(getResources().getColor(R.color.colorPrimary));

        Observable.just(300)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mBinding.hookIcon.startAnim();

//                        mBinding.content.setVisibility(View.VISIBLE);
//                        float distance = LibCalcUtil.dp2px(view.getContext(), 250);
//                        ValueAnimator animator = ValueAnimator.ofFloat(distance, 0);
//                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator animation) {
//                                mBinding.content.setTranslationY(((float) animation.getAnimatedValue()));
//                            }
//                        });
//                        animator.setDuration(500);
//                        animator.start();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
//        getDialog().setCanceledOnTouchOutside(false);
    }

    public void showDialog(FragmentManager manager) {
        manager.executePendingTransactions();
        if (!isAdded()) {
            show(manager, CompleteTravelListDialog.class.getName());
        }
    }
}
