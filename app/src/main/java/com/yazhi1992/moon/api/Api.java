package com.yazhi1992.moon.api;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.api.bean.CheckBindStateBean;
import com.yazhi1992.moon.constant.NameConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.util.MyLog;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.ArrayList;
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
            LibUtils.showToast(BaseApplication.getInstance(), e.getCode() + e.getMessage());
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
        final AVQuery<AVObject> meQuery = new AVQuery<>(NameConstant.LoveHistory.CLAZZ_NAME);
        meQuery.whereEqualTo(NameConstant.LoveHistory.USER_ID, currentUser.getObjectId());

        final AVQuery<AVObject> loverQuery = new AVQuery<>(NameConstant.LoveHistory.CLAZZ_NAME);
        loverQuery.whereEqualTo(NameConstant.LoveHistory.USER_ID, currentUser.getString(NameConstant.AVUserClass.LOVER_ID));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.include(NameConstant.MemorialDay.CLAZZ_NAME);
        query.include(NameConstant.Hope.CLAZZ_NAME);
        query.orderByDescending(NameConstant.Common.UPDATE_TIME);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(NameConstant.LoveHistory.ID, lastItemId);
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
        AVObject memorialDayObj = new AVObject(NameConstant.MemorialDay.CLAZZ_NAME);
        memorialDayObj.put(NameConstant.MemorialDay.TITLE, title);
        memorialDayObj.put(NameConstant.MemorialDay.TIME, time);
        memorialDayObj.put(NameConstant.MemorialDay.USER_ID, currentUser.getObjectId());

        AVObject loveHistoryObj = new AVObject(NameConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(NameConstant.LoveHistory.MEMORIAL_DAY, memorialDayObj);
        loveHistoryObj.put(NameConstant.LoveHistory.USER_ID, currentUser.getObjectId());
        loveHistoryObj.put(NameConstant.LoveHistory.TYPE, TypeConstant.TYPE_MEMORIAL_DAY);
        loveHistoryObj.put(NameConstant.LoveHistory.USER_NAME, currentUser.getUsername());
        loveHistoryObj.put(NameConstant.LoveHistory.USER_HEAD_URL, currentUser.getString(NameConstant.AVUserClass.HEAD_URL));

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
    public void invite(String inviteNum, String userObjId, DataCallback<BindLoverBean> dataCallback) {
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
                        peerUserId[0] = bindLoverItemData.getString(NameConstant.BindLover.USER_ID);
                        return updateMyBindLover(userObjId, peerUserId[0], bindLoverItemData.getString(NameConstant.BindLover.LOVER_ID));
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
                AVQuery<AVObject> query = new AVQuery<>(NameConstant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(NameConstant.BindLover.INVITE_NUMBER, inviteNum);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exc) {
                        if (exc == null) {
                            if (list != null && list.size() > 0) {
                                AVObject bindLoverItemData = list.get(0);
                                String loverId = bindLoverItemData.getString(NameConstant.BindLover.LOVER_ID);
                                if (LibUtils.isNullOrEmpty(loverId) || loverId.equals(userObjId)) {
                                    e.onNext(bindLoverItemData);
                                } else {
                                    e.onError(new Throwable(BaseApplication.getInstance().getString(R.string.error_invite_num)));
                                }
                            } else {
                                e.onError(new Throwable(BaseApplication.getInstance().getString(R.string.error_invite_num)));

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
                AVQuery<AVObject> query = new AVQuery<>(NameConstant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(NameConstant.BindLover.USER_ID, userObjId);
                AVObject account = query.getFirst();
                account.put(NameConstant.BindLover.LOVER_ID, loverId);
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
                AVQuery<AVObject> query = new AVQuery<>(NameConstant.AVUserClass.CLAZZ_NAME);
                try {
                    AVObject peerUser = query.get(peerId);
                    AVUser currentUser = AVUser.getCurrentUser();
                    currentUser.put(NameConstant.AVUserClass.HAVE_LOVER, true);
                    currentUser.put(NameConstant.AVUserClass.LOVER_ID, peerUser.getObjectId());
                    currentUser.put(NameConstant.AVUserClass.LOVER_NAME, peerUser.getString(NameConstant.AVUserClass.USER_NAME));
                    currentUser.put(NameConstant.AVUserClass.LOVER_HEAD_URL, peerUser.getString(NameConstant.AVUserClass.HEAD_URL));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException exc) {
                            if (exc == null) {
                                BindLoverBean bindLoverBean = new BindLoverBean();
                                bindLoverBean.setBindComplete(true);
                                bindLoverBean.setLoverHeadUrl(peerUser.getString(NameConstant.AVUserClass.HEAD_URL));
                                bindLoverBean.setLoverId(peerUser.getObjectId());
                                bindLoverBean.setLoverName(peerUser.getString(NameConstant.AVUserClass.USER_NAME));
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
                        MyLog.log("updateUserLoverInfo accept");
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
    public void checkBindState(String userId, DataCallback<CheckBindStateBean> callback) {
        AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject.getBoolean(NameConstant.AVUserClass.HAVE_LOVER)) {
                        CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                        checkBindStateBean.setPeerObjId(avObject.getString(NameConstant.AVUserClass.LOVER_ID));
                        callback.onSuccess(checkBindStateBean);
                    } else {
                        //查询是否自己绑定了人
                        AVQuery<AVObject> query = new AVQuery<>(NameConstant.BindLover.CLAZZ_NAME);
                        query.whereEqualTo(NameConstant.BindLover.USER_ID, userId);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        AVObject bindLoverItemData = list.get(0);
                                        String loverId = bindLoverItemData.getString(NameConstant.BindLover.LOVER_ID);
                                        if (LibUtils.isNullOrEmpty(loverId)) {
                                            callback.onSuccess(new CheckBindStateBean(2));
                                        } else {
                                            //查询对方是否绑定你
                                            AVQuery<AVObject> query = new AVQuery<>(NameConstant.BindLover.CLAZZ_NAME);
                                            query.whereEqualTo(NameConstant.BindLover.USER_ID, loverId);
                                            query.findInBackground(new FindCallback<AVObject>() {
                                                @Override
                                                public void done(List<AVObject> list, AVException e) {
                                                    if (e == null) {
                                                        if (list != null && list.size() > 0) {
                                                            AVObject bindLoverItemData = list.get(0);
                                                            String loverId = bindLoverItemData.getString(NameConstant.BindLover.LOVER_ID);
                                                            if (LibUtils.notNullNorEmpty(loverId) && loverId.equals(userId)) {
                                                                CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                                                                checkBindStateBean.setPeerObjId(bindLoverItemData.getString(NameConstant.BindLover.USER_ID));
                                                                callback.onSuccess(checkBindStateBean);
                                                            } else {
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

    /**
     * 删除纪念日数据
     *
     * @param objId
     * @param callback
     */
    public void deleteMemorialDay(String objId, @TypeConstant.DataTypeInHistory int type, String dayObjId, DataCallback<Boolean> callback) {
        final int[] num = {2};
        final AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(NameConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryQuery.whereEqualTo(NameConstant.Common.OBJECT_ID, objId);
        loveHistoryQuery.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                num[0]--;
                if (num[0] == 0) {
                    handleResult(e, callback, () -> callback.onSuccess(true));
                }
            }
        });

        String clazzName = null;
        switch (type) {
            case TypeConstant.TYPE_MEMORIAL_DAY:
                clazzName = NameConstant.MemorialDay.CLAZZ_NAME;
                break;
            case TypeConstant.TYPE_HOPE:
                clazzName = NameConstant.Hope.CLAZZ_NAME;
                break;
            case TypeConstant.TYPE_NORMAL_TEXT:
                break;
            default:
                break;
        }
        final AVQuery<AVObject> dayQuery = new AVQuery<>(clazzName);
        dayQuery.whereEqualTo(NameConstant.Common.OBJECT_ID, dayObjId);
        dayQuery.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                num[0]--;
                if (num[0] == 0) {
                    handleResult(e, callback, () -> callback.onSuccess(true));
                }
            }
        });
    }

    /**
     * 获取纪念日列表
     *
     * @param callback
     */
    public void getMemorialDayList(int lastItemId, int size, DataCallback<List<MemorialDayBean>> callback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(NameConstant.MemorialDay.CLAZZ_NAME);
        meQuery.whereEqualTo(NameConstant.MemorialDay.USER_ID, currentUser.getObjectId());

        final AVQuery<AVObject> loverQuery = new AVQuery<>(NameConstant.MemorialDay.CLAZZ_NAME);
        loverQuery.whereEqualTo(NameConstant.MemorialDay.USER_ID, currentUser.getString(NameConstant.AVUserClass.LOVER_ID));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.orderByDescending(NameConstant.MemorialDay.TIME);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(NameConstant.LoveHistory.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    List<MemorialDayBean> dataList = new ArrayList<>();
                    for (AVObject object : list) {
                        MemorialDayBean memorialDayBean = new MemorialDayBean(object.getString(NameConstant.MemorialDay.TITLE), object.getLong(NameConstant.MemorialDay.TIME));
                        dataList.add(memorialDayBean);
                    }
                    callback.onSuccess(dataList);
                } else {
                    callback.onFailed(e.getCode(), e.getMessage());
                }
            }
        });
    }

    /**
     * 许愿
     *
     * @param title        标题
     * @param level        等级
     * @param dataCallback
     */
    public void addHope(String title, int level, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到心愿表 + 首页历史列表
        AVObject hopeObj = new AVObject(NameConstant.Hope.CLAZZ_NAME);
        hopeObj.put(NameConstant.Hope.TITLE, title);
        hopeObj.put(NameConstant.Hope.LEVEL, level);
        hopeObj.put(NameConstant.Hope.USER_ID, currentUser.getObjectId());
        hopeObj.put(NameConstant.Hope.USER_NAME, currentUser.getUsername());
        hopeObj.put(NameConstant.Hope.USER_HEAD_URL, currentUser.getString(NameConstant.AVUserClass.HEAD_URL));

        AVObject loveHistoryObj = new AVObject(NameConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(NameConstant.LoveHistory.HOPE, hopeObj);
        loveHistoryObj.put(NameConstant.LoveHistory.USER_ID, currentUser.getObjectId());
        loveHistoryObj.put(NameConstant.LoveHistory.TYPE, TypeConstant.TYPE_HOPE);
        loveHistoryObj.put(NameConstant.LoveHistory.USER_NAME, currentUser.getUsername());
        loveHistoryObj.put(NameConstant.LoveHistory.USER_HEAD_URL, currentUser.getString(NameConstant.AVUserClass.HEAD_URL));

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
            }
        });
    }

    /**
     * 获取愿望列表
     *
     * @param callback
     */
    public void getHopeList(int lastItemId, int size, DataCallback<List<HopeItemDataBean>> callback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(NameConstant.Hope.CLAZZ_NAME);
        meQuery.whereEqualTo(NameConstant.Hope.USER_ID, currentUser.getObjectId());

        final AVQuery<AVObject> loverQuery = new AVQuery<>(NameConstant.Hope.CLAZZ_NAME);
        loverQuery.whereEqualTo(NameConstant.Hope.USER_ID, currentUser.getString(NameConstant.AVUserClass.LOVER_ID));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.addAscendingOrder(NameConstant.Hope.STATUS); //已完成排在最下面
        query.addDescendingOrder(NameConstant.Hope.LEVEL); //等级优先级高于时间
        query.addDescendingOrder(NameConstant.Common.CREATE_TIME);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(NameConstant.Hope.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    List<HopeItemDataBean> dataList = new ArrayList<>();
                    for (AVObject object : list) {
                        HopeItemDataBean hopeData = new HopeItemDataBean(object.getString(NameConstant.Hope.TITLE), object.getInt(NameConstant.Hope.LEVEL));
                        hopeData.setObjectId(object.getObjectId());
                        hopeData.setUserName(object.getString(NameConstant.Hope.USER_NAME));
                        hopeData.setUserHeadUrl(object.getString(NameConstant.Hope.USER_HEAD_URL));
                        hopeData.setCreateTime(object.getDate(NameConstant.Common.CREATE_TIME));
                        hopeData.setUpdateTime(object.getDate(NameConstant.Common.UPDATE_TIME));
                        hopeData.setStatus(object.getInt(NameConstant.Hope.STATUS));
                        dataList.add(hopeData);
                    }
                    callback.onSuccess(dataList);
                } else {
                    callback.onFailed(e.getCode(), e.getMessage());
                }
            }
        });
    }

    /**
     * 心愿达成
     */
    public void finishHope(String objectId, DataCallback<Boolean> callback) {
        AVQuery<AVObject> query = new AVQuery<>(NameConstant.Hope.CLAZZ_NAME);
        query.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        object.put(NameConstant.Hope.STATUS, 1);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException exc) {
                                handleResult(exc, callback, new onResultSuc() {
                                    @Override
                                    public void onSuc() {
                                        callback.onSuccess(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }
}
