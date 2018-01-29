package com.yazhi1992.moon.api;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.yazhi1992.moon.AppApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.api.bean.CheckBindStateBean;
import com.yazhi1992.moon.constant.NameContant;
import com.yazhi1992.moon.util.MyLogger;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class Api {
    private Api() {

    }

    private static class ApiHolder {
        private static Api INSTANCE = new Api();
    }

    public static Api getInstance() {
        return ApiHolder.INSTANCE;
    }

    private interface onResultSuc {
        void onSuc();
    }

    private void handleResult(AVException e, final DataCallback dataCallback, onResultSuc onResultSuc) {
        if (e == null) {
            onResultSuc.onSuc();
        } else {
            dataCallback.onFailed(e.getCode(), e.getMessage());
        }
    }

    /**
     * 获取历史列表数据
     *
     * @param lastItemId   当前列表末尾数据 id
     * @param size         每页个数
     * @param dataCallback
     */
    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<AVObject>> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(NameContant.LoveHistory.CLAZZ_NAME);
        meQuery.whereEqualTo(NameContant.LoveHistory.USER_ID, currentUser.getObjectId());

        final AVQuery<AVObject> loverQuery = new AVQuery<>(NameContant.LoveHistory.CLAZZ_NAME);
        loverQuery.whereEqualTo(NameContant.LoveHistory.USER_ID, currentUser.getString(NameContant.AVUserClass.LOVER_ID));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.include(NameContant.MemorialDay.CLAZZ_NAME);
        query.orderByDescending(NameContant.LoveHistory.ID);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(NameContant.LoveHistory.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(list));
            }
        });
    }

    /**
     * 添加纪念日
     *
     * @param title        标题
     * @param time         时间
     * @param dataCallback
     */
    public void addMemorialDay(String title, long time, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到纪念日表 + 首页历史列表
        AVObject memorialDayObj = new AVObject(NameContant.MemorialDay.CLAZZ_NAME);
        memorialDayObj.put(NameContant.MemorialDay.TITLE, title);
        memorialDayObj.put(NameContant.MemorialDay.TIME, time);
        memorialDayObj.put(NameContant.MemorialDay.USER_ID, currentUser.getObjectId());

        AVObject loveHistoryObj = new AVObject(NameContant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(NameContant.LoveHistory.MEMORIAL_DAY, memorialDayObj);
        loveHistoryObj.put(NameContant.LoveHistory.USER_ID, currentUser.getObjectId());
        loveHistoryObj.put(NameContant.LoveHistory.TYPE, NameContant.LoveHistory.TYPE_MEMORIAL_DAY);
        loveHistoryObj.put(NameContant.LoveHistory.USER_NAME, currentUser.getUsername());
        loveHistoryObj.put(NameContant.LoveHistory.USER_HEAD_URL, currentUser.getString(NameContant.LoveHistory.USER_HEAD_URL));

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
            }
        });
    }

    /**
     * 与另一半绑定
     *
     * @param inviteNum    对方邀请码
     * @param userObjId    自己的id
     * @param dataCallback
     */
    public void invite(String inviteNum, String userObjId, @NotNull DataCallback<BindLoverBean> dataCallback) {
        final String[] peerUserId = new String[1];
        Observable.just(inviteNum)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<String, ObservableSource<AVObject>>() {
                    @Override
                    public ObservableSource<AVObject> apply(String s) throws Exception {
                        return checkInviteNum(inviteNum, userObjId);
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<AVObject, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(AVObject bindLoverItemData) throws Exception {
                        peerUserId[0] = bindLoverItemData.getString(NameContant.BindLover.USER_ID);
                        return updateMyBindLover(userObjId, peerUserId[0], bindLoverItemData.getString(NameContant.BindLover.LOVER_ID));
                    }
                })
                .takeWhile(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        //如果为空终止，直接进入 doOnComplete
                        return !LibUtils.isNullOrEmpty(s);
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<String, ObservableSource<BindLoverBean>>() {
                    @Override
                    public ObservableSource<BindLoverBean> apply(String s) throws Exception {
                        return updateUserLoverInfo(userObjId, peerUserId[0]);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (dataCallback != null) {
                            dataCallback.onSuccess(new BindLoverBean());
                        }
                    }
                })
                .subscribe(new Consumer<BindLoverBean>() {
                    @Override
                    public void accept(BindLoverBean aBoolean) throws Exception {
                        if (dataCallback != null) {
                            dataCallback.onSuccess(aBoolean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (dataCallback != null) {
                            dataCallback.onFailed(0, throwable.getMessage());
                        }
                    }
                });
    }

    //1. 检测邀请码是否可用，查bind表，该邀请码的用户的lover_id须不为空 || 等于自己
    private ObservableSource<AVObject> checkInviteNum(String inviteNum, String userObjId) {
        return Observable.create(new ObservableOnSubscribe<AVObject>() {
            @Override
            public void subscribe(ObservableEmitter<AVObject> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NameContant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(NameContant.BindLover.INVITE_NUMBER, inviteNum);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exc) {
                        if (exc == null) {
                            if (list != null && list.size() > 0) {
                                AVObject bindLoverItemData = list.get(0);
                                String loverId = bindLoverItemData.getString(NameContant.BindLover.LOVER_ID);
                                if (LibUtils.isNullOrEmpty(loverId) || loverId.equals(userObjId)) {
                                    e.onNext(bindLoverItemData);
                                } else {
                                    e.onError(new Throwable(AppApplication.getInstance().getString(R.string.error_invite_num)));
                                }
                            } else {
                                e.onError(new Throwable(AppApplication.getInstance().getString(R.string.error_invite_num)));

                            }
                        } else {
                            e.onError(new Throwable(exc.getCode() + exc.getMessage()));
                        }
                    }
                });
            }
        });
    }

    //2. 邀请码可用，修改bind表中自己的loverId为邀请码所属用户id
    private ObservableSource<String> updateMyBindLover(String userObjId, String loverId, String loverBindLoverId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NameContant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(NameContant.BindLover.USER_ID, userObjId);
                AVObject account = query.getFirst();
                account.put(NameContant.BindLover.LOVER_ID, loverId);
                account.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException exc) {
                        if (exc == null) {
                            if (LibUtils.notNullNorEmpty(loverBindLoverId) && loverBindLoverId.equals(userObjId)) {
                                //如果绑定的对方的另一半id就是自己，则双方user表中lover填写对方，完成绑定
                                e.onNext(loverBindLoverId);
                            } else {
                                e.onNext("");
                            }
                        } else {
                            e.onError(new Throwable(exc.getCode() + exc.getMessage()));
                        }
                    }
                });
            }
        });
    }

    //3. 如果对方bind表中loverId为自己，则A user表中填写lover内容，
    private ObservableSource<BindLoverBean> updateUserLoverInfo(String userId, String peerId) {
        return Observable.create(new ObservableOnSubscribe<BindLoverBean>() {
            @Override
            public void subscribe(ObservableEmitter<BindLoverBean> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NameContant.AVUserClass.CLAZZ_NAME);
                try {
                    AVObject peerUser = query.get(peerId);
                    AVUser currentUser = AVUser.getCurrentUser();
                    currentUser.put(NameContant.AVUserClass.HAVE_LOVER, true);
                    currentUser.put(NameContant.AVUserClass.LOVER_ID, peerUser.getObjectId());
                    currentUser.put(NameContant.AVUserClass.LOVER_NAME, peerUser.getString(NameContant.AVUserClass.USER_NAME));
                    currentUser.put(NameContant.AVUserClass.LOVER_HEAD_URL, peerUser.getString(NameContant.AVUserClass.HEAD_URL));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException exc) {
                            if (exc == null) {
                                BindLoverBean bindLoverBean = new BindLoverBean();
                                bindLoverBean.setBindComplete(true);
                                bindLoverBean.setLoverHeadUrl(peerUser.getString(NameContant.AVUserClass.HEAD_URL));
                                bindLoverBean.setLoverId(peerUser.getObjectId());
                                bindLoverBean.setLoverName(peerUser.getString(NameContant.AVUserClass.USER_NAME));
                                e.onNext(bindLoverBean);
                            } else {
                                e.onError(new Throwable(exc.getCode() + exc.getMessage()));
                            }
                        }
                    });
                } catch (AVException exc) {
                    exc.printStackTrace();
                    e.onError(new Throwable(exc.getCode() + exc.getMessage()));
                }
            }
        });
    }

    public void updateUserLoverInfo(String userId, String peerId, DataCallback<BindLoverBean> callback) {
        Observable.just(0)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<Integer, ObservableSource<BindLoverBean>>() {
                    @Override
                    public ObservableSource<BindLoverBean> apply(Integer integer) throws Exception {
                        return updateUserLoverInfo(userId, peerId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BindLoverBean>() {
                    @Override
                    public void accept(BindLoverBean bindLoverBean) throws Exception {
                        MyLogger.log("updateUserLoverInfo accept");
                        callback.onSuccess(bindLoverBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onFailed(0, throwable.getMessage());
                    }
                });
    }

    /**
     * 查询绑定状态
     *
     * @param userId
     * @param callback
     */
    public void checkBindState(String userId, @NotNull DataCallback<CheckBindStateBean> callback) {
        AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject.getBoolean(NameContant.AVUserClass.HAVE_LOVER)) {
                        CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                        checkBindStateBean.setPeerObjId(avObject.getString(NameContant.AVUserClass.LOVER_ID));
                        callback.onSuccess(checkBindStateBean);
                    } else {
                        //查询是否自己绑定了人
                        AVQuery<AVObject> query = new AVQuery<>(NameContant.BindLover.CLAZZ_NAME);
                        query.whereEqualTo(NameContant.BindLover.USER_ID, userId);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if(list != null && list.size() > 0) {
                                        AVObject bindLoverItemData = list.get(0);
                                        String loverId = bindLoverItemData.getString(NameContant.BindLover.LOVER_ID);
                                        if(LibUtils.isNullOrEmpty(loverId)) {
                                            callback.onSuccess(new CheckBindStateBean(2));
                                        } else {
                                            //查询对方是否绑定你
                                            AVQuery<AVObject> query = new AVQuery<>(NameContant.BindLover.CLAZZ_NAME);
                                            query.whereEqualTo(NameContant.BindLover.USER_ID, loverId);
                                            query.findInBackground(new FindCallback<AVObject>() {
                                                @Override
                                                public void done(List<AVObject> list, AVException e) {
                                                    if(e == null) {
                                                        if(list != null && list.size() > 0) {
                                                            AVObject bindLoverItemData = list.get(0);
                                                            String loverId = bindLoverItemData.getString(NameContant.BindLover.LOVER_ID);
                                                            if(LibUtils.notNullNorEmpty(loverId) && loverId.equals(userId)) {
                                                                CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                                                                checkBindStateBean.setPeerObjId(bindLoverItemData.getString(NameContant.BindLover.USER_ID));
                                                                callback.onSuccess(checkBindStateBean);
                                                            } else{
                                                                callback.onSuccess(new CheckBindStateBean(1));
                                                            }
                                                        } else {
                                                            //对方未生成配对码
                                                            callback.onSuccess(new CheckBindStateBean(2));
                                                        }
                                                    } else {
                                                        callback.onFailed(e.getCode(), e.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        callback.onSuccess(new CheckBindStateBean(2));
                                    }
                                } else {
                                    callback.onFailed(e.getCode(), e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    callback.onFailed(e.getCode(), e.getMessage());
                }
            }
        });
    }

}
