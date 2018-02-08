package com.yazhi1992.moon.ui.home.history;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.FinishedHopeInHistoryViewBinder;
import com.yazhi1992.moon.adapter.HopeInHistoryViewBinder;
import com.yazhi1992.moon.adapter.MemorialDayInHistoryViewBinder;
import com.yazhi1992.moon.adapter.TextInHistoryViewBinder;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.FragmentHistoryBinding;
import com.yazhi1992.moon.dialog.AddDialog;
import com.yazhi1992.moon.dialog.DeleteDialog;
import com.yazhi1992.moon.event.AddHistoryDataEvent;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.HistoryItemDataFromApi;
import com.yazhi1992.moon.viewmodel.HopeItemDataWrapper;
import com.yazhi1992.moon.viewmodel.IHistoryBean;
import com.yazhi1992.moon.viewmodel.MemorialBeanWrapper;
import com.yazhi1992.moon.viewmodel.TextBeanWrapper;
import com.yazhi1992.yazhilib.utils.KeyBoardHeightUtil;
import com.yazhi1992.yazhilib.utils.LibCalcUtil;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.TypePool;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding mBinding;
    private CustomMultitypeAdapter mMultiTypeAdapter;
    private Items mItems;
    private final int SIZE = 20;
    private int lastItemId = -1;
    private HistoryPresenter mPresenter = new HistoryPresenter();
    private AddDialog mAddDialog;
    private DeleteDialog mDeletaDialog;
    private int mDeletePosition;
    private boolean mShowKeyboard = false;
    private Disposable mShowBottomInpulSubscribe;
    private int mKeyBoardHeight = 0;
    private int mAddCommentPosition;
    private int mInputType; //0添加评论，1回复
    private String mAddCommentInput;
    private String mReplyCommentInput;
    private String mReplyCommentPeerId;
    private String mReplyCommentPeerName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMultiTypeAdapter = new CustomMultitypeAdapter();
        //纪念日
        mMultiTypeAdapter.register(MemorialBeanWrapper.class, new MemorialDayInHistoryViewBinder());
        //愿望清单
        mMultiTypeAdapter.register(HopeItemDataWrapper.class).to(
                new HopeInHistoryViewBinder(),
                new FinishedHopeInHistoryViewBinder()
        ).withClassLinker((position, hopeItemDataWrapper) -> {
            if (hopeItemDataWrapper.getData().getStatus() == 0) {
                //未完成
                return HopeInHistoryViewBinder.class;
            } else {
                //已完成
                return FinishedHopeInHistoryViewBinder.class;
            }
        });
        //文本
        mMultiTypeAdapter.register(TextBeanWrapper.class, new TextInHistoryViewBinder());

        //添加长按删除功能
        CustomItemViewBinder.OnItemLongClickListener onItemLongClickListener = new CustomItemViewBinder.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //删除弹窗
                mDeletePosition = position;
                showDeleteDialog();
            }
        };
        CustomItemViewBinder.OnItemClickListener onItemClickListener = new CustomItemViewBinder.OnItemClickListener() {

            @Override
            public void onClick(int position) {
                //跳转内容详情页
                IHistoryBean obj = (IHistoryBean) mItems.get(position);
                int type = obj.getType();
                switch (type) {
                case TypeConstant.TYPE_MEMORIAL_DAY:
//                    ActivityRouter.gotoMemorialDayDetail();
                    break;
                default:
                    break;
                }
            }
        };
        CustomItemViewBinder.OnItemClickCommentListener onItemClickCommentListener = new CustomItemViewBinder.OnItemClickCommentListener() {
            @Override
            public void onClick(int position) {
                if (mShowKeyboard) {
                    LibUtils.hideKeyboard(mBinding.root);
                } else {
                    //添加评论
                    if (mAddCommentInput != null || mInputType != 0) {
                        //缓存添加评论输入的字
                        mReplyCommentInput = mBinding.etInput.getText().toString();
                        mBinding.etInput.setText(mAddCommentInput == null ? "" : mAddCommentInput);
                        mBinding.etInput.setSelection(mBinding.etInput.getText().toString().length());
                        mAddCommentInput = null;
                    }
                    mBinding.etInput.setHint(getString(R.string.title_add_text));
                    mInputType = 0;
                    mAddCommentPosition = position;
                    LibUtils.showKeyoard(getActivity(), mBinding.etInput);
                }
            }

            @Override
            public void onReplyComment(int position, String peerName, String peerId) {
                if (mReplyCommentInput != null || mInputType != 1) {
                    //缓存添加评论输入的字
                    mAddCommentInput = mBinding.etInput.getText().toString();
                    mBinding.etInput.setText(mReplyCommentInput == null ? "" : mReplyCommentInput);
                    mBinding.etInput.setSelection(mBinding.etInput.getText().toString().length());
                    mReplyCommentInput = null;
                }
                mInputType = 1;
                //回复对方
                mBinding.etInput.setHint("回复 " + peerName);
                mAddCommentPosition = position;
                mReplyCommentPeerId = peerId;
                mReplyCommentPeerName = peerName;
                LibUtils.showKeyoard(getActivity(), mBinding.etInput);
            }
        };
        TypePool typePool = mMultiTypeAdapter.getTypePool();
        for (int i = 0; i < typePool.size(); i++) {
            CustomItemViewBinder<?, ?> itemViewBinder = (CustomItemViewBinder<?, ?>) typePool.getItemViewBinder(i);
            itemViewBinder.setOnItemClickListener(onItemClickListener);
            itemViewBinder.setOnItemLongClickListener(onItemLongClickListener);
            itemViewBinder.setOnItemClickCommentListener(onItemClickCommentListener);
        }

        mBinding.smartRefresh.setOnRefreshListener(refreshlayout -> getDatas(false));
        mBinding.smartRefresh.setOnLoadmoreListener(refreshlayout -> getDatas(true));

        mItems = new Items();
        mMultiTypeAdapter.setItems(mItems);
        mBinding.ryHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.ryHistory.setAdapter(mMultiTypeAdapter);
        mBinding.ryHistory.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mBinding.ryHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mShowKeyboard) {
                    LibUtils.hideKeyboard(mBinding.root);
                }
                if (dy > 0) {
                    // Scroll Down
                    if (mBinding.fab.isShown()) {
                        mBinding.fab.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!mBinding.fab.isShown()) {
                        mBinding.fab.show();
                    }
                }
            }
        });

        mBinding.fab.setOnClickListener(v -> showAddDialog());
        mBinding.smartRefresh.autoRefresh();

        KeyBoardHeightUtil.getKeyBoardHeight(mBinding.root, new KeyBoardHeightUtil.KeyBoardHeightListener() {

            @Override
            public void onLayoutListener(int keyboardHeight, boolean isShowing) {
                //监听屏幕尺寸变化
                if (isShowing) {
                    mShowKeyboard = true;
                    mShowBottomInpulSubscribe = Observable.timer(225, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    mBinding.rlInput.setVisibility(View.VISIBLE);
                                    if (mKeyBoardHeight == 0 || mKeyBoardHeight != keyboardHeight) {
                                        mKeyBoardHeight = keyboardHeight;
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.rlInput.getLayoutParams();
                                        layoutParams.setMargins(0, 0, 0, (int) (keyboardHeight - LibCalcUtil.dp2px(getActivity(), 56))); //需减去底部tab栏高度
                                    }
                                    mShowBottomInpulSubscribe = null;
                                }
                            });

                } else {
                    mShowKeyboard = false;
                    if (mShowBottomInpulSubscribe != null) {
                        mShowBottomInpulSubscribe.dispose();
                        mShowBottomInpulSubscribe = null;
                    }
                    mBinding.rlInput.setVisibility(View.GONE);
                }
            }
        });

        mBinding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送评论
                String comment = mBinding.etInput.getText().toString();
                if (LibUtils.isNullOrEmpty(comment)) {
                    LibUtils.showToast(getActivity(), getString(R.string.history_comment_empty));
                    return;
                }
                IHistoryBean data = (IHistoryBean) mItems.get(mAddCommentPosition);
                mBinding.tvSend.setLoading(true);
                DataCallback<CommentBean> callback = new DataCallback<CommentBean>() {
                    @Override
                    public void onSuccess(CommentBean result) {
                        data.getCommentDatas().add(result);
                        mMultiTypeAdapter.notifyItemChanged(mAddCommentPosition);

                        LibUtils.showToast(getActivity(), getString(R.string.comment_suc));
                        mBinding.tvSend.setLoading(false);
                        mBinding.etInput.setText("");
                        LibUtils.hideKeyboard(mBinding.root);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.tvSend.setLoading(false);
                        LibUtils.hideKeyboard(mBinding.root);
                    }
                };
                if (mInputType == 1) {
                    //回复，则有对方的姓名
                    mPresenter.replyComment(comment, data.getObjectId(), mReplyCommentPeerId, mReplyCommentPeerName, callback);
                } else {
                    mPresenter.addComment(comment, data.getObjectId(), callback);
                }
            }
        });
    }

    private Object transformData(@TypeConstant.DataTypeInHistory int type, HistoryItemDataFromApi itemData) {
        Object data = null;
        switch (type) {
            case TypeConstant.TYPE_MEMORIAL_DAY:
                data = new MemorialBeanWrapper(itemData);
                break;
            case TypeConstant.TYPE_HOPE:
                data = new HopeItemDataWrapper(itemData);
                break;
            case TypeConstant.TYPE_TEXT:
                data = new TextBeanWrapper(itemData);
                break;
            default:
                break;
        }
        return data;
    }

    private void showDeleteDialog() {
        if (mDeletaDialog == null) {
            mDeletaDialog = new DeleteDialog();
            mDeletaDialog.setOnClickDeleteListener(() -> {
                IHistoryBean data = (IHistoryBean) mItems.get(mDeletePosition);
                TipDialogHelper.getInstance().showDialog(getActivity(), getString(R.string.delete_data), new TipDialogHelper.OnComfirmListener() {
                    @Override
                    public void comfirm() {
                        mPresenter.delete(data.getObjectId(), data.getType(), data.getData().getObjectId(), new DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                mMultiTypeAdapter.remove(mDeletePosition);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                LibUtils.showToast(getActivity(), msg);
                            }
                        });
                    }
                });
            });
        }
        if (!mDeletaDialog.isAdded() && getActivity() != null) {
            mDeletaDialog.show(getActivity().getFragmentManager());
        }
    }

    private void showAddDialog() {
        if (mAddDialog == null) {
            mAddDialog = new AddDialog();
        }
        if (!mAddDialog.isAdded() && getActivity() != null) {
            mAddDialog.show(getActivity().getFragmentManager());
        }
    }

    // TODO: 2018/1/25 使用 dagger 分层
    private void getDatas(final boolean loadMore) {
        if (loadMore) {
            //获取末尾id
            if (mItems.size() > 0) {
                Object item = mItems.get(mItems.size() - 1);
                if (item != null && item instanceof IHistoryBean) {
                    lastItemId = ((IHistoryBean) item).getId();
                }
            }
        } else {
            lastItemId = -1;
        }
        mPresenter.getLoveHistory(lastItemId, SIZE, new DataCallback<List<HistoryItemDataFromApi>>() {
            @Override
            public void onSuccess(List<HistoryItemDataFromApi> data) {
                if (loadMore) {
                    mBinding.smartRefresh.finishLoadmore();
                } else {
                    mItems.clear();
                    mBinding.smartRefresh.finishRefresh();
                }
                if (data.size() > 0) {
                    for (HistoryItemDataFromApi loveHistoryItemData : data) {
                        mItems.add(transformData(loveHistoryItemData.getType(), loveHistoryItemData));
                    }
                    mMultiTypeAdapter.notifyDataSetChanged();
                }
                mBinding.smartRefresh.setEnableLoadmore(data != null && data.size() == SIZE);
            }

            @Override
            public void onFailed(int code, String msg) {
                if (loadMore) {
                    mBinding.smartRefresh.finishLoadmore();
                } else {
                    mBinding.smartRefresh.finishRefresh();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void addMemorial(AddHistoryDataEvent bean) {
        mBinding.smartRefresh.autoRefresh();
    }

}
