package com.yazhi1992.moon.ui.mc;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityMcBinding;
import com.yazhi1992.moon.dialog.ItemsDialog;
import com.yazhi1992.moon.event.OnMcStatusChanged;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.widget.calendarview.CalendarInfoCache;
import com.yazhi1992.moon.widget.calendarview.Calendarview;
import com.yazhi1992.moon.widget.calendarview.DateBean;
import com.yazhi1992.moon.widget.calendarview.InitCallback;
import com.yazhi1992.moon.widget.calendarview.OnPagerChangeListener;
import com.yazhi1992.moon.widget.calendarview.OnSingleChooseListener;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@Route(path = ActivityRouter.NEW_MC_DETAIL)
public class McActivity extends BaseActivity {

    private ActivityMcBinding mBinding;
    private DateBean mDateBean;
    private Calendarview mCalendarView;
    private McDetailPresenter mPresenter = new McDetailPresenter();
    private McModel mModel = new McModel();
    private ItemsDialog mAddDialog;
    // TODO: 2018/4/6

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mc);
        EventBus.getDefault().register(this);
        initToolBar(mBinding.toolbar);

        mModel.mGender.set(new UserDaoUtil().getUserDao().getGender());
        mBinding.setItem(mModel);

        mCalendarView = findViewById(R.id.calendar);
        mCalendarView.setClickble(mModel.mGender.get() == TypeConstant.WOMEN);
        mCalendarView.setInitCallback(new InitCallback() {
            @Override
            public void onInit(int[] date, int movePx) {
                mBinding.tv.setText(date[0] + "年" + date[1] + "月");
                anim(movePx);
                CalendarInfoCache.getInstance().getData(date[0], date[1], new DataCallback<List<McDataFromApi>>() {
                    @Override
                    public void onSuccess(List<McDataFromApi> data) {
                        mCalendarView.rebuildView();
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            }
        });

        mCalendarView.setSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date, int position) {
                mBinding.mcFab.setVisibility(View.VISIBLE);
                if(!mBinding.mcFab.isShown()) {
                    mBinding.mcFab.show();
                }
                mDateBean = date;
                mModel.data.set(mDateBean);
            }
        });

        mCalendarView.setPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                mBinding.tv.setText(date[0] + "年" + date[1] + "月");
            }

            @Override
            public void onPageScrollStateChanged(int state, int movePx) {
                anim(movePx);
            }

        });

        mBinding.mcFab.setOnClickListener(v -> {
            switch (mDateBean.getMcType()) {
                case TypeConstant.MC_COME:
                case TypeConstant.MC_GO:
                    //删除
                    String showMsg = String.format(getString(R.string.comfirm_delete_mc), mDateBean.getDate()[0] + "年" + mDateBean.getDate()[1] + "月" + mDateBean.getDate()[2] + "日");
                    TipDialogHelper.getInstance().showDialog(this, showMsg, new TipDialogHelper.OnComfirmListener() {
                        @Override
                        public void comfirm() {
                            //本来就是来或去状态，则点击按钮移除现有状态
                            mPresenter.removeMcAction(mDateBean.getDate()[0], mDateBean.getDate()[1], mDateBean.getDate()[2], new DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean data) {
                                    // TODO: 2018/4/25 修改为不需要每次 rebuild 都重新构造数据、重新addview
                                    mCalendarView.rebuildView();
                                }

                                @Override
                                public void onFailed(int code, String msg) {
                                    LibUtils.showToast("remove onFailed");
                                }
                            });
                        }
                    });
                    break;
                default:
                    if(mAddDialog == null) {
                        ArrayList<String> items = new ArrayList<>();
                        items.add(getString(R.string.mc_come_item));
                        items.add(getString(R.string.mc_go_item));
                        mAddDialog = ItemsDialog.getInstance(items, new ItemsDialog.OnClickItemListener() {
                            @Override
                            public void onClick(int position) {
                                String showMsg = String.format(getString(R.string.comfirm_update_mc), mDateBean.getDate()[0] + "年" + mDateBean.getDate()[1] + "月" + mDateBean.getDate()[2] + "日");
                                TipDialogHelper.getInstance().showDialog(McActivity.this, showMsg, new TipDialogHelper.OnComfirmListener() {
                                    @Override
                                    public void comfirm() {
                                        //根据radiobutton提交状态
                                        mPresenter.addMcAction(position == 0 ? TypeConstant.MC_COME: TypeConstant.MC_GO
                                                , mDateBean.getDate()[0]
                                                , mDateBean.getDate()[1]
                                                , mDateBean.getDate()[2]
                                                , mDateBean.getTime(), new DataCallback<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean data) {
                                                        mCalendarView.rebuildView();
                                                        LibUtils.showToast(McActivity.this, position == 1 ? getString(R.string.add_mc_go_suc) : getString(R.string.add_mc_come_suc));
                                                        PushManager.getInstance().pushAction(ActionConstant.UPDATE_MC);
                                                    }

                                                    @Override
                                                    public void onFailed(int code, String msg) {
                                                        LibUtils.showToast("add onFailed");
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                    mAddDialog.show(getFragmentManager());
                    break;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CalendarInfoCache.getInstance().reset();
        EventBus.getDefault().unregister(this);
    }

    private void anim(int movePx) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(mBinding.rl, "translationY", movePx);
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMcStatusChanged(OnMcStatusChanged bean) {
        ViewBindingUtils.srcCompat(mBinding.mcFab, mDateBean);
    }
}
