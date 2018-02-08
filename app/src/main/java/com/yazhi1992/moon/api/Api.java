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
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.util.MyLog;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.HistoryItemDataFromApi;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<HistoryItemDataFromApi>> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        meQuery.whereEqualTo(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        loverQuery.whereEqualTo(TableConstant.LoveHistory.USER, getUserObj(currentUser.getString(TableConstant.AVUserClass.LOVER_ID)));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.include(TableConstant.LoveHistory.USER);
        query.include(TableConstant.LoveHistory.MEMORIAL_DAY);
        query.include(TableConstant.LoveHistory.HOPE);
        query.include(TableConstant.LoveHistory.TEXT);
        query.orderByDescending(TableConstant.Common.CREATE_TIME);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(TableConstant.LoveHistory.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, dataCallback, () -> {
                    List<HistoryItemDataFromApi> dataList = new ArrayList<>();
                    for (AVObject object : list) {
                        HistoryItemDataFromApi historyItemDataFromApi = new HistoryItemDataFromApi(object.getInt(TableConstant.LoveHistory.TYPE), object);
                        dataList.add(historyItemDataFromApi);
                    }
                    dataCallback.onSuccess(dataList);
                });
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
        AVObject memorialDayObj = new AVObject(TableConstant.MemorialDay.CLAZZ_NAME);
        memorialDayObj.put(TableConstant.MemorialDay.TITLE, title);
        memorialDayObj.put(TableConstant.MemorialDay.TIME, time);
        memorialDayObj.put(TableConstant.MemorialDay.USER_ID, currentUser.getObjectId());

        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.MEMORIAL_DAY, memorialDayObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_MEMORIAL_DAY);
        loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
            }
        });
    }

    /**
     * 修改纪念日
     *
     * @param title        标题
     * @param time         时间
     * @param dataCallback
     */
    public void editMemorialDay(String id, String title, long time, final DataCallback<Boolean> dataCallback) {
        AVObject memorialDayData = AVObject.createWithoutData(TableConstant.MemorialDay.CLAZZ_NAME, id);
        memorialDayData.put(TableConstant.MemorialDay.TITLE, title);
        memorialDayData.put(TableConstant.MemorialDay.TIME, time);
        memorialDayData.saveInBackground(new SaveCallback() {
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
                        peerUserId[0] = bindLoverItemData.getString(TableConstant.BindLover.USER_ID);
                        return updateMyBindLover(userObjId, peerUserId[0], bindLoverItemData.getString(TableConstant.BindLover.LOVER_ID));
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
                AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(TableConstant.BindLover.INVITE_NUMBER, inviteNum);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exc) {
                        if (exc == null) {
                            if (list != null && list.size() > 0) {
                                AVObject bindLoverItemData = list.get(0);
                                String loverId = bindLoverItemData.getString(TableConstant.BindLover.LOVER_ID);
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
                AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                query.whereEqualTo(TableConstant.BindLover.USER_ID, userObjId);
                AVObject account = query.getFirst();
                account.put(TableConstant.BindLover.LOVER_ID, loverId);
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
                AVQuery<AVObject> query = new AVQuery<>(TableConstant.AVUserClass.CLAZZ_NAME);
                try {
                    AVObject peerUser = query.get(peerId);
                    AVUser currentUser = AVUser.getCurrentUser();
                    currentUser.put(TableConstant.AVUserClass.HAVE_LOVER, true);
                    currentUser.put(TableConstant.AVUserClass.LOVER_ID, peerUser.getObjectId());
                    currentUser.put(TableConstant.AVUserClass.LOVER_NAME, peerUser.getString(TableConstant.AVUserClass.USER_NAME));
                    currentUser.put(TableConstant.AVUserClass.LOVER_HEAD_URL, peerUser.getString(TableConstant.AVUserClass.HEAD_URL));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException exc) {
                            if (exc == null) {
                                BindLoverBean bindLoverBean = new BindLoverBean();
                                bindLoverBean.setBindComplete(true);
                                bindLoverBean.setLoverHeadUrl(peerUser.getString(TableConstant.AVUserClass.HEAD_URL));
                                bindLoverBean.setLoverId(peerUser.getObjectId());
                                bindLoverBean.setLoverName(peerUser.getString(TableConstant.AVUserClass.USER_NAME));
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
                    if (avObject.getBoolean(TableConstant.AVUserClass.HAVE_LOVER)) {
                        CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                        checkBindStateBean.setPeerObjId(avObject.getString(TableConstant.AVUserClass.LOVER_ID));
                        callback.onSuccess(checkBindStateBean);
                    } else {
                        //查询是否自己绑定了人
                        AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                        query.whereEqualTo(TableConstant.BindLover.USER_ID, userId);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        AVObject bindLoverItemData = list.get(0);
                                        String loverId = bindLoverItemData.getString(TableConstant.BindLover.LOVER_ID);
                                        if (LibUtils.isNullOrEmpty(loverId)) {
                                            callback.onSuccess(new CheckBindStateBean(2));
                                        } else {
                                            //查询对方是否绑定你
                                            AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                                            query.whereEqualTo(TableConstant.BindLover.USER_ID, loverId);
                                            query.findInBackground(new FindCallback<AVObject>() {
                                                @Override
                                                public void done(List<AVObject> list, AVException e) {
                                                    if (e == null) {
                                                        if (list != null && list.size() > 0) {
                                                            AVObject bindLoverItemData = list.get(0);
                                                            String loverId = bindLoverItemData.getString(TableConstant.BindLover.LOVER_ID);
                                                            if (LibUtils.notNullNorEmpty(loverId) && loverId.equals(userId)) {
                                                                CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                                                                checkBindStateBean.setPeerObjId(bindLoverItemData.getString(TableConstant.BindLover.USER_ID));
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
    public void deleteMemorialDayData(String objId, @TypeConstant.DataTypeInHistory int type, String dayObjId, DataCallback<Boolean> callback) {
        final int[] num = {2};
        final AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryQuery.whereEqualTo(TableConstant.Common.OBJECT_ID, objId);
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
                clazzName = TableConstant.MemorialDay.CLAZZ_NAME;
                break;
            case TypeConstant.TYPE_HOPE:
                clazzName = TableConstant.Hope.CLAZZ_NAME;
                break;
            case TypeConstant.TYPE_TEXT:
                clazzName = TableConstant.Text.CLAZZ_NAME;
                break;
            default:
                break;
        }
        final AVQuery<AVObject> dayQuery = new AVQuery<>(clazzName);
        dayQuery.whereEqualTo(TableConstant.Common.OBJECT_ID, dayObjId);
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
     * 删除纪念日数据（不知道love_history ID）
     *
     * @param callback
     */
    public void deleteMemorialDayData(String memorialDayId, DataCallback<Boolean> callback) {
        //先查该纪念日在history表中的对应数据
        AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        AVObject memorialDay = AVObject.createWithoutData(TableConstant.MemorialDay.CLAZZ_NAME, memorialDayId);
        memorialDay.deleteInBackground();
        loveHistoryQuery.whereEqualTo(TableConstant.LoveHistory.MEMORIAL_DAY, memorialDay);
        ArrayList<AVObject> todos = new ArrayList<>();
        todos.add(memorialDay);
        loveHistoryQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        //开始删除
                        AVObject.deleteAllInBackground(list, new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                handleResult(e, callback, new onResultSuc() {
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


    /**
     * 获取纪念日列表
     *
     * @param callback
     */
    public void getMemorialDayList(int lastItemId, int size, DataCallback<List<MemorialDayBean>> callback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.MemorialDay.CLAZZ_NAME);
        meQuery.whereEqualTo(TableConstant.MemorialDay.USER_ID, currentUser.getObjectId());

        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.MemorialDay.CLAZZ_NAME);
        loverQuery.whereEqualTo(TableConstant.MemorialDay.USER_ID, currentUser.getString(TableConstant.AVUserClass.LOVER_ID));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.orderByDescending(TableConstant.MemorialDay.TIME);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(TableConstant.LoveHistory.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    List<MemorialDayBean> dataList = new ArrayList<>();
                    for (AVObject object : list) {
                        MemorialDayBean memorialDayBean = new MemorialDayBean(object.getString(TableConstant.MemorialDay.TITLE), object.getLong(TableConstant.MemorialDay.TIME));
                        memorialDayBean.setObjectId(object.getObjectId());
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
     * 添加心愿
     *
     * @param title        标题
     * @param level        等级
     * @param dataCallback
     */
    public void addHope(String title, int level, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到心愿表 + 首页历史列表
        AVObject hopeObj = new AVObject(TableConstant.Hope.CLAZZ_NAME);
        hopeObj.put(TableConstant.Hope.TITLE, title);
        hopeObj.put(TableConstant.Hope.LEVEL, level);
        hopeObj.put(TableConstant.Hope.USER, getUserObj(currentUser.getObjectId()));

        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.HOPE, hopeObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_HOPE);
        loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

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
        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.Hope.CLAZZ_NAME);
        meQuery.whereEqualTo(TableConstant.Hope.USER, getUserObj(currentUser.getObjectId()));

        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.Hope.CLAZZ_NAME);
        loverQuery.whereEqualTo(TableConstant.Hope.USER, getUserObj(currentUser.getString(TableConstant.AVUserClass.LOVER_ID)));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.addAscendingOrder(TableConstant.Hope.STATUS); //已完成排在最下面
        query.addDescendingOrder(TableConstant.Hope.LEVEL); //等级优先级高于时间
        query.addDescendingOrder(TableConstant.Common.CREATE_TIME);
        query.include(TableConstant.Hope.USER);
        query.limit(size);
        if (lastItemId != -1) {
            query.whereLessThan(TableConstant.Hope.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    List<HopeItemDataBean> dataList = new ArrayList<>();
                    for (AVObject object : list) {
                        HopeItemDataBean hopeData = new HopeItemDataBean(object.getString(TableConstant.Hope.TITLE), object.getInt(TableConstant.Hope.LEVEL));
                        hopeData.setObjectId(object.getObjectId());
                        AVObject user = object.getAVObject(TableConstant.Hope.USER);
                        if (user != null) {
                            hopeData.setUserName(user.getString(TableConstant.AVUserClass.USER_NAME));
                            hopeData.setUserHeadUrl(user.getString(TableConstant.AVUserClass.HEAD_URL));
                        }
                        hopeData.setCreateTime(object.getDate(TableConstant.Common.CREATE_TIME));
                        hopeData.setUpdateTime(object.getDate(TableConstant.Common.UPDATE_TIME));
                        hopeData.setStatus(object.getInt(TableConstant.Hope.STATUS));
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
        AVQuery<AVObject> query = new AVQuery<>(TableConstant.Hope.CLAZZ_NAME);
        query.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        object.put(TableConstant.Hope.STATUS, 1);
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


    /**
     * 添加文本
     *
     * @param content      内容
     * @param dataCallback
     */
    public void addText(String content, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到文本表 + 首页历史列表
        AVObject hopeObj = new AVObject(TableConstant.Text.CLAZZ_NAME);
        hopeObj.put(TableConstant.Text.CONTENT, content);
        hopeObj.put(TableConstant.Text.USER, AVObject.createWithoutData(TableConstant.AVUserClass.CLAZZ_NAME, currentUser.getObjectId()));

        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.TEXT, hopeObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_TEXT);
        loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
            }
        });
    }

    /**
     * 添加评论
     */
    public void addComment(String content, String parentObjId, final DataCallback<CommentBean> dataCallback) {
        AVObject commentItemData = AVObject.createWithoutData(TableConstant.LoveHistory.CLAZZ_NAME, parentObjId);
        AVUser currentUser = AVUser.getCurrentUser();

        CommentBean commentBean = new CommentBean(content, currentUser.getUsername());
        commentBean.setUserId(currentUser.getObjectId());
        commentBean.setId(new Date().getTime());
        commentBean.setParentId(parentObjId);

        commentItemData.add(TableConstant.LoveHistory.COMMENT_LIST, commentBean);

        commentItemData.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(commentBean));
            }
        });
    }

    public void replyComment(String content, String parentObjId, String peerId, String peerName, final DataCallback<CommentBean> dataCallback) {
        AVObject commentItemData = AVObject.createWithoutData(TableConstant.LoveHistory.CLAZZ_NAME, parentObjId);
        AVUser currentUser = AVUser.getCurrentUser();

        CommentBean commentBean = new CommentBean(content, currentUser.getUsername());
        commentBean.setUserId(currentUser.getObjectId());
        commentBean.setId(new Date().getTime());
        commentBean.setReplyId(peerId);
        commentBean.setReplyName(peerName);
        commentBean.setParentId(parentObjId);

        commentItemData.add(TableConstant.LoveHistory.COMMENT_LIST, commentBean);

        commentItemData.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(commentBean));
            }
        });
    }

    public void deleteComment(String parentObjId, long commentId, DataCallback<Boolean> callback) {
        AVObject object = AVObject.createWithoutData(TableConstant.LoveHistory.CLAZZ_NAME, parentObjId);
        object.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException exc) {
                handleResult(exc, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        //先更新数据再删除
                        JSONArray jsonArray = avObject.getJSONArray(TableConstant.LoveHistory.COMMENT_LIST);
                        if(jsonArray != null && jsonArray.length() > 0) {
                            //评论有数据
                            List<CommentBean> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if(jsonObject.getLong(CommentBean.ID) == commentId) {
                                        continue;
                                    }
                                    CommentBean commentBean = new CommentBean(jsonObject.getString(CommentBean.CONTENT), jsonObject.getString(CommentBean.USER_NAME));
                                    commentBean.setUserId(jsonObject.getString(CommentBean.USER_ID));
                                    commentBean.setReplyName(jsonObject.getString(CommentBean.REPLAY_NAME));
                                    commentBean.setReplyId(jsonObject.getString(CommentBean.REPLAY_ID));
                                    commentBean.setId(jsonObject.getLong(CommentBean.ID));
                                    commentBean.setParentId(jsonObject.getString(CommentBean.PARENT_ID));
                                    list.add(commentBean);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            object.put(TableConstant.LoveHistory.COMMENT_LIST, list);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    handleResult(e, callback, () -> callback.onSuccess(true));
                                }
                            });
                        } else {
                            callback.onSuccess(true);
                        }
                    }
                });
            }
        });

    }

    private AVObject getUserObj(String objId) {
        return AVObject.createWithoutData(TableConstant.AVUserClass.CLAZZ_NAME, objId);
    }


}
