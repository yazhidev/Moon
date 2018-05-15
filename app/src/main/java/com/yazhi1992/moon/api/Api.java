package com.yazhi1992.moon.api;

import android.content.Context;
import android.content.pm.PackageManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.api.bean.CheckBindStateBean;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.home.set.LoverInfo;
import com.yazhi1992.moon.ui.mc.McData;
import com.yazhi1992.moon.ui.mc.McDataFromApi;
import com.yazhi1992.moon.util.MyLog;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.ConfigBean;
import com.yazhi1992.moon.viewmodel.HistoryItemDataFromApi;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibDeviceUtils;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
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
            // TODO: 2018/2/21 不应该直接加这里，应该有类似过滤器
            //如果因为之前网络原因注册推送失败，则重新注册
            onResultSuc.onSuc();
        } else {
            LibUtils.showToast(e.getCode() + " - " + e.getMessage());
            dataCallback.onFailed(e.getCode(), e.getMessage());
        }
    }

    private void handleRxjavaResult(AVException e, ObservableEmitter emitter, onResultSuc onResultSuc) {
        if (e == null) {
            onResultSuc.onSuc();
        } else {
            LibUtils.showToast(BaseApplication.getInstance(), e.getCode() + e.getMessage());
            emitter.onError(new Throwable(e.getCode() + e.getMessage()));
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
        int gender = new UserDaoUtil().getUserDao().getGender();

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
        query.include(TableConstant.LoveHistory.MC);
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
                        int type = object.getInt(TableConstant.LoveHistory.TYPE);
                        if (type == TypeConstant.TYPE_MC && gender == TypeConstant.WOMEN) {
                            //女性不需要显示mc信息
                            continue;
                        }
                        HistoryItemDataFromApi historyItemDataFromApi = new HistoryItemDataFromApi(type, object);
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
                        AVObject avObject = bindLoverItemData.getAVObject(TableConstant.BindLover.USER);
                        if (avObject != null) {
                            peerUserId[0] = avObject.getObjectId();
                        }
                        AVObject loverObject = bindLoverItemData.getAVObject(TableConstant.BindLover.LOVER);
                        return updateMyBindLover(userObjId, peerUserId[0], loverObject == null ? null : loverObject.getObjectId());
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
                query.include(TableConstant.BindLover.USER);
                query.include(TableConstant.BindLover.LOVER);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exc) {
                        if (exc == null) {
                            if (list != null && list.size() > 0) {
                                AVObject bindLoverItemData = list.get(0);
                                String loverId = null;
                                AVObject avObject = bindLoverItemData.getAVObject(TableConstant.BindLover.LOVER);
                                if (avObject != null) {
                                    loverId = avObject.getObjectId();
                                }
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
                query.whereEqualTo(TableConstant.BindLover.USER, getUserObj(userObjId));
                AVObject account = query.getFirst();
                account.put(TableConstant.BindLover.LOVER, getUserObj(loverId));
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
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException exc) {
                            if (exc == null) {
                                BindLoverBean bindLoverBean = new BindLoverBean();
                                bindLoverBean.setBindComplete(true);
                                bindLoverBean.setLoverHeadUrl(peerUser.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE).getUrl());
                                bindLoverBean.setLoverId(peerUser.getObjectId());
                                bindLoverBean.setLoverName(peerUser.getString(TableConstant.AVUserClass.NICK_NAME));
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
                        query.whereEqualTo(TableConstant.BindLover.USER, getUserObj(userId));
                        query.include(TableConstant.BindLover.USER);
                        query.include(TableConstant.BindLover.LOVER);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        AVObject bindLoverItemData = list.get(0);
                                        String loverId = null;
                                        AVObject avObject1 = bindLoverItemData.getAVObject(TableConstant.BindLover.LOVER);
                                        if (avObject1 != null) {
                                            loverId = avObject1.getObjectId();
                                        }
                                        if (LibUtils.isNullOrEmpty(loverId)) {
                                            callback.onSuccess(new CheckBindStateBean(2));
                                        } else {
                                            //查询对方是否绑定你
                                            AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                                            query.whereEqualTo(TableConstant.BindLover.USER, getUserObj(loverId));
                                            query.include(TableConstant.BindLover.USER);
                                            query.include(TableConstant.BindLover.LOVER);
                                            query.findInBackground(new FindCallback<AVObject>() {
                                                @Override
                                                public void done(List<AVObject> list, AVException e) {
                                                    if (e == null) {
                                                        if (list != null && list.size() > 0) {
                                                            AVObject bindLoverItemData = list.get(0);
                                                            String loverId = null;
                                                            AVObject avObject1 = bindLoverItemData.getAVObject(TableConstant.BindLover.LOVER);
                                                            if (avObject1 != null) {
                                                                loverId = avObject1.getObjectId();
                                                            }
                                                            if (LibUtils.notNullNorEmpty(loverId) && loverId.equals(userId)) {
                                                                CheckBindStateBean checkBindStateBean = new CheckBindStateBean(0);
                                                                AVObject avObject2 = bindLoverItemData.getAVObject(TableConstant.BindLover.USER);
                                                                checkBindStateBean.setPeerObjId(avObject2.getObjectId());
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
     * 删除回忆列表数据
     *
     * @param callback
     */
    public void deleteMemorialDayData(@TypeConstant.DataTypeInHistory int type, String dataObjId, DataCallback<Boolean> callback) {
        //先删除对应类型的数据
        String clazzName = null;
        String clazzInhistoryName = null;
        switch (type) {
            case TypeConstant.TYPE_MEMORIAL_DAY:
                clazzName = TableConstant.MemorialDay.CLAZZ_NAME;
                clazzInhistoryName = TableConstant.LoveHistory.MEMORIAL_DAY;
                break;
            case TypeConstant.TYPE_HOPE:
            case TypeConstant.TYPE_HOPE_FINISHED:
                clazzName = TableConstant.Hope.CLAZZ_NAME;
                clazzInhistoryName = TableConstant.LoveHistory.HOPE;
                break;
            case TypeConstant.TYPE_TEXT:
                clazzName = TableConstant.Text.CLAZZ_NAME;
                clazzInhistoryName = TableConstant.LoveHistory.TEXT;
                break;
            case TypeConstant.TYPE_MC:
                clazzName = TableConstant.MC.CLAZZ_NAME;
                clazzInhistoryName = TableConstant.LoveHistory.MC;
                break;
            default:
                break;
        }
        if (type == TypeConstant.TYPE_TEXT) {
            //删除图文消息，如果有图片，一起删除图片，节约空间
            AVObject data = AVObject.createWithoutData(clazzName, dataObjId);
            String finalClazzName = clazzName;
            String finalClazzInhistoryName = clazzInhistoryName;
            data.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    handleResult(e, callback, new onResultSuc() {
                        @Override
                        public void onSuc() {
                            AVFile avFile = avObject.getAVFile(TableConstant.Text.IMG_FILE);
                            if (avFile != null) {
                                //如果原来有图片文件，则先删除原来的图片，节约空间
                                avFile.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        doRealDelete(type, dataObjId, callback, finalClazzName, finalClazzInhistoryName);
                                    }
                                });
                            } else {
                                doRealDelete(type, dataObjId, callback, finalClazzName, finalClazzInhistoryName);
                            }
                        }
                    });
                }
            });
        } else {
            doRealDelete(type, dataObjId, callback, clazzName, clazzInhistoryName);
        }
    }

    private void doRealDelete(@TypeConstant.DataTypeInHistory int type, String dataObjId, DataCallback<Boolean> callback, String clazzName, String clazzInhistoryName) {
        AVObject data = AVObject.createWithoutData(clazzName, dataObjId);
        String finalClazzInhistoryName = clazzInhistoryName;
        if (type == TypeConstant.TYPE_MC) {
            //男方无权删除女方相关信息，只删除history表数据
            deleteHistoryItemData(finalClazzInhistoryName, data, callback, type);
        } else {
            //先删除关联的表数据，再删除history表数据
            data.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(AVException e) {
                    handleResult(e, callback, new onResultSuc() {
                        @Override
                        public void onSuc() {
                            deleteHistoryItemData(finalClazzInhistoryName, data, callback, type);
                        }
                    });
                }
            });
        }
    }

    /**
     * 删除history表数据
     *
     * @param finalClazzInhistoryName
     * @param data
     * @param callback
     * @param type
     */
    private void deleteHistoryItemData(String finalClazzInhistoryName, AVObject data, DataCallback<Boolean> callback, @TypeConstant.DataTypeInHistory int type) {
        //删除history表数据
        final AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryQuery.whereEqualTo(finalClazzInhistoryName, data);
        loveHistoryQuery.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        if (type == TypeConstant.TYPE_HOPE || type == TypeConstant.TYPE_HOPE_FINISHED) {
                            callback.onSuccess(true);
                        } else {
                            callback.onSuccess(false);
                        }
                    }
                });
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
     * 删除心愿（不知道love_history ID）
     *
     * @param callback
     */
    public void deleteHopeData(String hopeId, DataCallback<Boolean> callback) {
        //先查在history表中的对应数据
        AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
        AVObject hope = AVObject.createWithoutData(TableConstant.Hope.CLAZZ_NAME, hopeId);
        hope.deleteInBackground();
        loveHistoryQuery.whereEqualTo(TableConstant.LoveHistory.HOPE, hope);
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
     * @param link         链接
     * @param dataCallback
     */
    public void addHope(String title, int level, String link, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到心愿表 + 首页历史列表
        AVObject hopeObj = new AVObject(TableConstant.Hope.CLAZZ_NAME);
        hopeObj.put(TableConstant.Hope.TITLE, title);
        hopeObj.put(TableConstant.Hope.LEVEL, level);
        hopeObj.put(TableConstant.Hope.LINK, link);
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
     * 修改心愿
     */
    public void editHope(String objId, String title, int level, String link, final DataCallback<Boolean> dataCallback) {
        AVObject hopeData = AVObject.createWithoutData(TableConstant.Hope.CLAZZ_NAME, objId);
        hopeData.put(TableConstant.Hope.TITLE, title);
        hopeData.put(TableConstant.Hope.LEVEL, level);
        hopeData.put(TableConstant.Hope.LINK, link);
        hopeData.saveInBackground(new SaveCallback() {
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
    public void getHopeList(@TypeConstant.HopeType int type, int lastItemId, int size, DataCallback<List<HopeItemDataBean>> callback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //查询自己或另一半的
        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.Hope.CLAZZ_NAME);
        meQuery.whereEqualTo(TableConstant.Hope.USER, getUserObj(currentUser.getObjectId()));
        meQuery.whereEqualTo(TableConstant.Hope.STATUS, type);

        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.Hope.CLAZZ_NAME);
        loverQuery.whereEqualTo(TableConstant.Hope.USER, getUserObj(currentUser.getString(TableConstant.AVUserClass.LOVER_ID)));
        loverQuery.whereEqualTo(TableConstant.Hope.STATUS, type);

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
                            hopeData.setUserName(user.getString(TableConstant.AVUserClass.NICK_NAME));
                            hopeData.setUserHeadUrl(user.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE).getUrl());
                        }
                        hopeData.setCreateTime(object.getDate(TableConstant.Common.CREATE_TIME));
                        hopeData.setUpdateTime(object.getDate(TableConstant.Common.UPDATE_TIME));
                        hopeData.setStatus(object.getInt(TableConstant.Hope.STATUS));
                        hopeData.setLink(object.getString(TableConstant.Hope.LINK));
                        hopeData.setFinishContent(object.getString(TableConstant.Hope.FINISH_WORD));
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
     *
     * @param objectId
     * @param content
     * @param dataCallback
     */
    public void finishHope(String objectId, String content, final DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        AVObject hopeObj = AVObject.createWithoutData(TableConstant.Hope.CLAZZ_NAME, objectId);
        //更改心愿状态
        hopeObj.put(TableConstant.Hope.STATUS, TypeConstant.HOPE_DONE);
        hopeObj.put(TableConstant.Hope.FINISH_WORD, content);

        hopeObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {

            }
        });

        //首页历史列表插入一条心愿达成数据
        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.HOPE, hopeObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_HOPE_FINISHED);
        loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

        ArrayList<AVObject> todos = new ArrayList<>();
        todos.add(hopeObj);
        todos.add(loveHistoryObj);
        AVObject.saveAllInBackground(todos, new SaveCallback() {
            @Override
            public void done(AVException e) {
                Observable.just(0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
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
    public void addText(String content, String filePath, final DataCallback<String> dataCallback) {
        if (LibUtils.isNullOrEmpty(filePath)) {
            //保存到text表
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
                    handleResult(e, dataCallback, () -> dataCallback.onSuccess(null));
                }
            });
        } else {
            String name = "img.jpg";
            if (filePath.contains("/")) {
                name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
            }
            try {
                AVFile file = AVFile.withAbsoluteLocalPath(name, filePath);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            //保存到text表
                            AVUser currentUser = AVUser.getCurrentUser();

                            //存到文本表 + 首页历史列表
                            AVObject hopeObj = new AVObject(TableConstant.Text.CLAZZ_NAME);
                            hopeObj.put(TableConstant.Text.CONTENT, content);
                            hopeObj.put(TableConstant.Text.IMG_FILE, file);
                            hopeObj.put(TableConstant.Text.USER, AVObject.createWithoutData(TableConstant.AVUserClass.CLAZZ_NAME, currentUser.getObjectId()));

                            AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
                            loveHistoryObj.put(TableConstant.LoveHistory.TEXT, hopeObj);
                            loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_TEXT);
                            loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

                            //保存关联对象的同时，被关联的对象也会随之被保存到云端。
                            loveHistoryObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    handleResult(e, dataCallback, () -> dataCallback.onSuccess(file.getUrl()));
                                }
                            });
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                dataCallback.onFailed(-1, e.toString());
            }
        }
    }

    /**
     * 添加评论
     */
    public void addComment(String content, String parentObjId, final DataCallback<CommentBean> dataCallback) {
        AVObject commentItemData = AVObject.createWithoutData(TableConstant.LoveHistory.CLAZZ_NAME, parentObjId);
        AVUser currentUser = AVUser.getCurrentUser();

        CommentBean commentBean = new CommentBean(content);
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

    public void replyComment(String content, String parentObjId, String peerId, final DataCallback<CommentBean> dataCallback) {
        AVObject commentItemData = AVObject.createWithoutData(TableConstant.LoveHistory.CLAZZ_NAME, parentObjId);
        AVUser currentUser = AVUser.getCurrentUser();

        CommentBean commentBean = new CommentBean(content);
        commentBean.setUserId(currentUser.getObjectId());
        commentBean.setId(new Date().getTime());
        commentBean.setReplyId(peerId);
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
                        if (jsonArray != null && jsonArray.length() > 0) {
                            //评论有数据
                            List<CommentBean> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getLong(CommentBean.ID) == commentId) {
                                        continue;
                                    }
                                    CommentBean commentBean = new CommentBean(jsonObject.getString(CommentBean.CONTENT));
                                    commentBean.setUserId(jsonObject.getString(CommentBean.USER_ID));
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

    public void uploadHomeImg(String filePath, DataCallback<String> callback) {
        String name = "img.jpg";
        if (filePath.contains("/")) {
            name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        }
        try {
            AVFile file = AVFile.withAbsoluteLocalPath(name, filePath);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        //保存到home表
                        User userDao = new UserDaoUtil().getUserDao();
                        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.Home.CLAZZ_NAME);
                        meQuery.whereEqualTo(TableConstant.Home.UPLOADER, getUserObj(userDao.getObjectId()));
                        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.Home.CLAZZ_NAME);
                        loverQuery.whereEqualTo(TableConstant.Home.UPLOADER, getUserObj(userDao.getLoverId()));
                        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                handleResult(e, callback, new onResultSuc() {
                                    @Override
                                    public void onSuc() {
                                        AVObject object = null;
                                        if (list == null || list.size() == 0) {
                                            //没找到，添加
                                            object = new AVObject(TableConstant.Home.CLAZZ_NAME);
                                            object.put(TableConstant.Home.UPLOADER, getUserObj(AVUser.getCurrentUser().getObjectId()));
                                            object.put(TableConstant.Home.HOME_IMG_FILE, file);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    handleResult(e, callback, new onResultSuc() {
                                                        @Override
                                                        public void onSuc() {
                                                            callback.onSuccess(file.getUrl());
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            object = list.get(0);
                                            //先删除旧数据，节约空间
                                            AVFile avFile = object.getAVFile(TableConstant.Home.HOME_IMG_FILE);
                                            AVObject finalObject = object;
                                            avFile.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    //再存储
                                                    finalObject.put(TableConstant.Home.UPLOADER, getUserObj(AVUser.getCurrentUser().getObjectId()));
                                                    finalObject.put(TableConstant.Home.HOME_IMG_FILE, file);
                                                    finalObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(AVException e) {
                                                            handleResult(e, callback, new onResultSuc() {
                                                                @Override
                                                                public void onSuc() {
                                                                    callback.onSuccess(file.getUrl());
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });

                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.onFailed(-1, e.toString());
        }
    }

    /**
     * 获得首页图片地址
     *
     * @param callback
     */
    public void getHomeImg(DataCallback<String> callback) {
        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.Home.CLAZZ_NAME);
        meQuery.whereEqualTo(TableConstant.Home.UPLOADER, getUserObj(AVUser.getCurrentUser().getObjectId()));

        User userDao = new UserDaoUtil().getUserDao();

        final AVQuery<AVObject> loverQuery = new AVQuery<>(TableConstant.Home.CLAZZ_NAME);
        loverQuery.whereEqualTo(TableConstant.Home.UPLOADER, getUserObj(userDao.getLoverId()));

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(meQuery, loverQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        if (list != null && list.size() > 0) {
                            handleResult(e, callback, new onResultSuc() {
                                @Override
                                public void onSuc() {
                                    AVObject object = list.get(0);
                                    callback.onSuccess(object.getAVFile(TableConstant.Home.HOME_IMG_FILE).getUrl());
                                }
                            });
                        } else {
                            callback.onSuccess("");
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置性别
     *
     * @param gender
     * @param callback
     */
    public void setGender(@TypeConstant.Gender int gender, DataCallback<Integer> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser.getInt(TableConstant.AVUserClass.GENDER) == 0) {
            //刚注册时设置性别，则设置默认头像
            currentUser.put(TableConstant.AVUserClass.HEAD_IMG_FILE, gender == 1
                    ? currentUser.getAVFile(TableConstant.AVUserClass.DEFAULT_MAN_HEAD)
                    : currentUser.getAVFile(TableConstant.AVUserClass.DEFAULT_WOMAN_HEAD));
        }
        currentUser.put(TableConstant.AVUserClass.GENDER, gender);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        callback.onSuccess(gender);
                    }
                });
            }
        });
    }

    /**
     * mc详情页信息获取
     * 先获取最后一次来mc的时间，用于显示。
     * 再获取最近一条mc更新状态，用于判断按钮要显示的问题是来还是走
     *
     * @param callback
     */
    public void getMcDetailInitData(DataCallback<McData> callback) {
        final McData[] data = {null};
        Observable.just(1)
                .observeOn(Schedulers.io())
                .concatMap(new Function<Integer, ObservableSource<McData>>() {
                    @Override
                    public ObservableSource<McData> apply(Integer integer) throws Exception {
                        //获取情侣中女性最近一条mc记录，用来设置按钮状态
                        return Observable.create(new ObservableOnSubscribe<McData>() {
                            @Override
                            public void subscribe(ObservableEmitter<McData> emitter) throws Exception {
                                User userDao = new UserDaoUtil().getUserDao();
                                AVObject userObj = null;
                                if (userDao.getGender() == TypeConstant.MEN) {
                                    //获取另一半
                                    String loverId = userDao.getLoverId();
                                    userObj = getUserObj(loverId);
                                } else {
                                    //获取自己
                                    userObj = getUserObj(userDao.getObjectId());
                                }
                                AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
                                mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);
                                mcQuery.addDescendingOrder(TableConstant.MC.TIME); //先按照更新状态的时间排序
                                mcQuery.addDescendingOrder(TableConstant.Common.CREATE_TIME);
                                mcQuery.getFirstInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        //获取最新一条
                                        handleRxjavaResult(e, emitter, new onResultSuc() {
                                            @Override
                                            public void onSuc() {
                                                if (avObject != null) {
                                                    data[0] = new McData();
                                                    data[0].setStatus(avObject.getInt(TableConstant.MC.STATUS));
                                                    emitter.onNext(data[0]);
                                                } else {
                                                    emitter.onComplete();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                })
                .filter(new Predicate<McData>() {
                    @Override
                    public boolean test(McData mcData) throws Exception {
                        return mcData != null;
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<McData, ObservableSource<McData>>() {
                    @Override
                    public ObservableSource<McData> apply(McData mcData) throws Exception {
                        //获取情侣中女性最近一次来mc的时间
                        return Observable.create(new ObservableOnSubscribe<McData>() {
                            @Override
                            public void subscribe(ObservableEmitter<McData> emitter) throws Exception {
                                User userDao = new UserDaoUtil().getUserDao();
                                AVObject userObj = null;
                                if (userDao.getGender() == TypeConstant.MEN) {
                                    //获取另一半
                                    String loverId = userDao.getLoverId();
                                    userObj = getUserObj(loverId);
                                } else {
                                    //获取自己
                                    userObj = getUserObj(userDao.getObjectId());
                                }

                                AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
                                mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);

                                AVQuery<AVObject> statusQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
                                statusQuery.whereEqualTo(TableConstant.MC.STATUS, 1);

                                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(mcQuery, statusQuery));

                                query.addDescendingOrder(TableConstant.MC.TIME); //先按照更新状态的时间排序
                                query.addDescendingOrder(TableConstant.Common.CREATE_TIME);
                                query.getFirstInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        //获取最新一条
                                        handleRxjavaResult(e, emitter, new onResultSuc() {
                                            @Override
                                            public void onSuc() {
                                                if (avObject != null && data[0] != null) {
                                                    data[0].setTime(avObject.getLong(TableConstant.MC.TIME));
                                                }
                                                emitter.onNext(data[0]);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        MyLog.log("complete");
                        if (data[0] == null) {
                            callback.onSuccess(null);
                        }
                    }
                })
                .subscribe(new Consumer<McData>() {
                    @Override
                    public void accept(McData mcData) throws Exception {
                        callback.onSuccess(mcData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onFailed(-1, throwable.getMessage());
                    }
                });
    }

    /**
     * 获取某个月的mc数据列表
     *
     * @param year
     * @param month
     * @param callback
     */
    public void getMonthMcRecord(int year, int month, DataCallback<List<McDataFromApi>> callback) {
        User userDao = new UserDaoUtil().getUserDao();
        AVObject userObj = null;
        if (userDao.getGender() == TypeConstant.MEN) {
            //获取另一半
            String loverId = userDao.getLoverId();
            userObj = getUserObj(loverId);
        } else {
            //获取自己
            userObj = getUserObj(userDao.getObjectId());
        }
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);

        AVQuery<AVObject> mcQueryYear = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQueryYear.whereEqualTo(TableConstant.MC.YEAR, year);

        AVQuery<AVObject> mcQueryMonth = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQueryMonth.whereEqualTo(TableConstant.MC.MONTH, month);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(mcQuery, mcQueryYear, mcQueryMonth));

        query.addAscendingOrder(TableConstant.MC.TIME); //按照时间排序
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        List<McDataFromApi> dataList = new ArrayList<>();
                        for (AVObject object : list) {
                            int time = object.getInt(TableConstant.MC.TIME);
                            int year = object.getInt(TableConstant.MC.YEAR);
                            int month = object.getInt(TableConstant.MC.MONTH);
                            int day = object.getInt(TableConstant.MC.DAY);
                            int action = object.getInt(TableConstant.MC.ACTION);
                            McDataFromApi mcDataFromApi = new McDataFromApi(time, year, month, day, action);
                            dataList.add(mcDataFromApi);
                        }
                        callback.onSuccess(dataList);
                    }
                });
            }
        });
    }

    /**
     * 获取mc数据列表
     *
     * @param callback
     */
    public void getAllMcRecord(DataCallback<List<McDataFromApi>> callback) {
        User userDao = new UserDaoUtil().getUserDao();
        AVObject userObj = null;
        if (userDao.getGender() == TypeConstant.MEN) {
            //获取另一半
            String loverId = userDao.getLoverId();
            userObj = getUserObj(loverId);
        } else {
            //获取自己
            userObj = getUserObj(userDao.getObjectId());
        }
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);
        mcQuery.addAscendingOrder(TableConstant.MC.TIME); //按照时间排序
        mcQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        List<McDataFromApi> dataList = new ArrayList<>();
                        for (AVObject object : list) {
                            long time = object.getLong(TableConstant.MC.TIME);
                            int year = object.getInt(TableConstant.MC.YEAR);
                            int month = object.getInt(TableConstant.MC.MONTH);
                            int day = object.getInt(TableConstant.MC.DAY);
                            int action = object.getInt(TableConstant.MC.ACTION);
                            McDataFromApi mcDataFromApi = new McDataFromApi(time, year, month, day, action);
                            dataList.add(mcDataFromApi);
                        }
                        callback.onSuccess(dataList);
                    }
                });
            }
        });
    }


    /**
     * 获取mc数据列表
     *
     * @param lastTime 第一次传0
     * @param size
     * @param callback
     */
    public void getMcRecord(int lastTime, int size, DataCallback<List<McDataFromApi>> callback) {
        User userDao = new UserDaoUtil().getUserDao();
        AVObject userObj = null;
        if (userDao.getGender() == TypeConstant.MEN) {
            //获取另一半
            String loverId = userDao.getLoverId();
            userObj = getUserObj(loverId);
        } else {
            //获取自己
            userObj = getUserObj(userDao.getObjectId());
        }
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);
        mcQuery.addDescendingOrder(TableConstant.MC.TIME); //按照时间排序
        if (lastTime > 0) {
            mcQuery.whereLessThan(TableConstant.MC.TIME, lastTime);
        }
        mcQuery.limit(size);
        mcQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        List<McDataFromApi> dataList = new ArrayList<>();
                        for (AVObject object : list) {
                            long time = object.getLong(TableConstant.MC.TIME);
                            int year = object.getInt(TableConstant.MC.YEAR);
                            int month = object.getInt(TableConstant.MC.MONTH);
                            int day = object.getInt(TableConstant.MC.DAY);
                            int action = object.getInt(TableConstant.MC.ACTION);
                            McDataFromApi mcDataFromApi = new McDataFromApi(time, year, month, day, action);
                            dataList.add(mcDataFromApi);
                        }
                        callback.onSuccess(dataList);
                    }
                });
            }
        });
    }

    // TODO: 2018/4/5 修改逻辑

    /**
     * 获取情侣中女性最近一条mc记录（用于心情预警，如果没来25天或刚来3天，则预警）
     *
     * @param callback
     */
    public void getLastMcRecord(DataCallback<Boolean> callback) {
        User userDao = new UserDaoUtil().getUserDao();
        AVObject userObj = null;
        if (userDao.getGender() == TypeConstant.MEN) {
            //获取另一半
            String loverId = userDao.getLoverId();
            userObj = getUserObj(loverId);
        } else {
            //获取自己
            userObj = getUserObj(userDao.getObjectId());
        }
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);
        mcQuery.addDescendingOrder(TableConstant.MC.TIME); //先按照更新状态的时间排序
        mcQuery.addDescendingOrder(TableConstant.Common.CREATE_TIME);
        mcQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                //获取最新一条
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        if (avObject != null) {
                            int status = avObject.getInt(TableConstant.MC.STATUS);
                            int gapBetweenTwoDay = Math.abs(LibTimeUtils.getGapBetweenTwoDay(new Date(), new Date(avObject.getLong(TableConstant.MC.TIME))));

                            if (status == 0
                                    && LibSPUtils.getInt(SPKeyConstant.MC_GO_MIN_DAY, 25) < gapBetweenTwoDay
                                    && gapBetweenTwoDay < LibSPUtils.getInt(SPKeyConstant.MC_GO_MAX_DAY, 37)) {
                                //走了 25 < day < 37 要预警
                                callback.onSuccess(true);
                            } else if (status == 1
                                    && gapBetweenTwoDay < LibSPUtils.getInt(SPKeyConstant.MC_COME_MAX_DAY, 3)) {
                                //来 day < 3 要预警
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } else {
                            callback.onSuccess(false);
                        }
                    }
                });
            }
        });
    }

    /**
     * 添加mc状态
     */
    public void addMcAction(@TypeConstant.McStatus int action, int year, int month, int day, long time, DataCallback<McDataFromApi> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到mc表 + 首页历史列表
        AVObject mcObj = new AVObject(TableConstant.MC.CLAZZ_NAME);
        mcObj.put(TableConstant.MC.ACTION, action);
        mcObj.put(TableConstant.MC.USER, getUserObj(currentUser.getObjectId()));
        mcObj.put(TableConstant.MC.TIME, time);
        mcObj.put(TableConstant.MC.YEAR, year);
        mcObj.put(TableConstant.MC.MONTH, month);
        mcObj.put(TableConstant.MC.DAY, day);

        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.MC, mcObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_MC);
        loveHistoryObj.put(TableConstant.LoveHistory.USER, getUserObj(currentUser.getObjectId()));

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> {
                    McDataFromApi mcDataFromApi = new McDataFromApi(time, year, month, day, action);
                    dataCallback.onSuccess(mcDataFromApi);
                });
            }
        });
    }

    //移除mc数据
    public void deleteMcAction(int year, int month, int day, DataCallback<Boolean> callback) {
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, getUserObj(AVUser.getCurrentUser().getObjectId()));

        AVQuery<AVObject> mcQueryYear = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQueryYear.whereEqualTo(TableConstant.MC.YEAR, year);

        AVQuery<AVObject> mcQueryMonth = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQueryMonth.whereEqualTo(TableConstant.MC.MONTH, month);

        AVQuery<AVObject> mcQueryDay = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQueryDay.whereEqualTo(TableConstant.MC.DAY, day);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(mcQuery, mcQueryYear, mcQueryMonth, mcQueryDay));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        avObject.deleteInBackground();
                        //先查在history表中的对应数据
                        AVQuery<AVObject> loveHistoryQuery = new AVQuery<>(TableConstant.LoveHistory.CLAZZ_NAME);
                        loveHistoryQuery.whereEqualTo(TableConstant.LoveHistory.MC, avObject);
                        loveHistoryQuery.deleteAllInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                callback.onSuccess(true);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 获取情侣中女性最近一条mc记录（用于设置中间状态，显示来或去按钮）
     *
     * @param callback
     */
    public void getLatestMcRecord(DataCallback<McDataFromApi> callback) {
        User userDao = new UserDaoUtil().getUserDao();
        AVObject userObj = null;
        if (userDao.getGender() == TypeConstant.MEN) {
            //获取另一半
            String loverId = userDao.getLoverId();
            userObj = getUserObj(loverId);
        } else {
            //获取自己
            userObj = getUserObj(userDao.getObjectId());
        }
        AVQuery<AVObject> mcQuery = new AVQuery<>(TableConstant.MC.CLAZZ_NAME);
        mcQuery.whereEqualTo(TableConstant.MC.USER, userObj);
        mcQuery.addDescendingOrder(TableConstant.MC.TIME); //先按照更新状态的时间排序
        mcQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                //获取最新一条
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        if (avObject != null) {
                            int time = avObject.getInt(TableConstant.MC.TIME);
                            int year = avObject.getInt(TableConstant.MC.YEAR);
                            int month = avObject.getInt(TableConstant.MC.MONTH);
                            int day = avObject.getInt(TableConstant.MC.DAY);
                            int action = avObject.getInt(TableConstant.MC.ACTION);
                            McDataFromApi mcDataFromApi = new McDataFromApi(time, year, month, day, action);
                            callback.onSuccess(mcDataFromApi);
                        } else {
                            callback.onSuccess(null);
                        }
                    }
                });
            }
        });
    }

    /**
     * 更新mc状态
     */
    public void updateMcStatus(@TypeConstant.McAction int status, long time, DataCallback<Boolean> dataCallback) {
        AVUser currentUser = AVUser.getCurrentUser();

        //存到mc表 + 首页历史列表
        AVObject mcObj = new AVObject(TableConstant.MC.CLAZZ_NAME);
        mcObj.put(TableConstant.MC.STATUS, status);
        mcObj.put(TableConstant.MC.USER, getUserObj(currentUser.getObjectId()));
        mcObj.put(TableConstant.MC.TIME, time);

        AVObject loveHistoryObj = new AVObject(TableConstant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(TableConstant.LoveHistory.MC, mcObj);
        loveHistoryObj.put(TableConstant.LoveHistory.TYPE, TypeConstant.TYPE_MC);
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
     * 修改昵称
     *
     * @param userName
     * @param callback
     */
    public void setUserName(String userName, DataCallback<String> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        currentUser.put(TableConstant.AVUserClass.NICK_NAME, userName);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        callback.onSuccess(userName);
                    }
                });
            }
        });
    }

    /**
     * 获取另一半的最新名字和头像
     *
     * @param callback
     */
    public void getLoverInfo(DataCallback<LoverInfo> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
        query.whereEqualTo(TableConstant.BindLover.USER, getUserObj(currentUser.getObjectId()));
        query.include(TableConstant.BindLover.USER);
        query.include(TableConstant.BindLover.LOVER);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException exc) {
                handleResult(exc, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        if (list != null && list.size() > 0) {
                            AVObject bindLoverItemData = list.get(0);
                            AVObject loverObject = bindLoverItemData.getAVObject(TableConstant.BindLover.LOVER);
                            if (loverObject != null) {
                                callback.onSuccess(new LoverInfo(loverObject.getString(TableConstant.AVUserClass.NICK_NAME)
                                        , loverObject.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE).getUrl()));
                            } else {
                                callback.onSuccess(null);
                            }
                        } else {
                            callback.onSuccess(null);
                        }
                    }
                });
            }
        });
    }

    /**
     * 更新头像
     *
     * @param filePath
     * @param dataCallback
     */
    public void updateHeadImg(String filePath, final DataCallback<String> dataCallback) {
        String name = "head_img.jpg";
        if (filePath.contains("/")) {
            name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        }
        try {
            AVFile file = AVFile.withAbsoluteLocalPath(name, filePath);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        AVUser currentUser = AVUser.getCurrentUser();
                        currentUser.fetchInBackground(new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                AVFile avFile = avObject.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE);
                                if (avFile == null) {
                                    currentUser.put(TableConstant.AVUserClass.HEAD_IMG_FILE, file);
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            handleResult(e, dataCallback, () -> dataCallback.onSuccess(file.getUrl()));

                                        }
                                    });
                                } else {
                                    //如果原来有图片文件，则先删除原来的头像图片，节约空间
                                    avFile.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            //再存储到user表
                                            currentUser.put(TableConstant.AVUserClass.HEAD_IMG_FILE, file);
                                            currentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    handleResult(e, dataCallback, () -> dataCallback.onSuccess(file.getUrl()));

                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });


                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            dataCallback.onFailed(-1, e.toString());
        }
    }

    /**
     * 获取配置
     *
     * @param callback
     */
    public void getConfig(DataCallback<ConfigBean> callback) {
        AVQuery<AVObject> query = new AVQuery<>(TableConstant.CONFIGURATION.CLAZZ_NAME);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        ConfigBean configBean = new ConfigBean();
                        configBean.setCanPushImg(avObject.getBoolean(TableConstant.CONFIGURATION.ADD_IMG_ENABLE));
                        configBean.setMcComeMaxDay(avObject.getInt(TableConstant.CONFIGURATION.MC_COME_MAX_DAY));
                        configBean.setMcGoMaxDay(avObject.getInt(TableConstant.CONFIGURATION.MC_GO_MAX_DAY));
                        configBean.setMcGoMinDay(avObject.getInt(TableConstant.CONFIGURATION.MC_GO_MIN_DAY));
                        callback.onSuccess(configBean);
                    }
                });
            }
        });
    }

    /**
     * 注册
     *
     * @param email
     * @param pwd
     * @param callback
     */
    public void register(String email, String pwd, String nickname, DataCallback<Boolean> callback) {
        AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(email);// 设置用户名
        user.setEmail(email);
        user.setPassword(pwd);// 设置密码
        user.put(TableConstant.AVUserClass.NICK_NAME, nickname);// 设置昵称
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        //自动登录
                        login(email, pwd, callback);
                    }
                });
            }
        });
    }

    /**
     * 登录
     *
     * @param email
     * @param pwd
     * @param callback
     */
    public void login(String email, String pwd, DataCallback<Boolean> callback) {
        AVUser.logInInBackground(email, pwd, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        callback.onSuccess(true);
                    }
                });
            }
        });
    }

    /**
     * 发送验证邮件
     *
     * @param email
     * @param callback
     */
    public void checkEmail(String email, DataCallback<Boolean> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        if (LibUtils.isNullOrEmpty(currentUser.getEmail())) {
            currentUser.setEmail(email);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    sendCheckEmail(email, callback);
                }
            });
        } else {
            sendCheckEmail(email, callback);
        }
    }

    /**
     * 检验邮箱是否已验证
     *
     * @param callback
     */
    public void checkEmailStatus(DataCallback<Boolean> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        currentUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        callback.onSuccess(avObject.getBoolean(TableConstant.AVUserClass.EMAIL_VERIFIED));
                    }
                });
            }
        });
    }

    private void sendCheckEmail(String email, DataCallback<Boolean> callback) {
        AVUser.requestEmailVerifyInBackground(email, new RequestEmailVerifyCallback() {
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

    /**
     * 发送重置密码邮件
     *
     * @param email
     * @param callback
     */
    public void findPwd(String email, DataCallback<Boolean> callback) {
        AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
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

    public void registerPushIdToUser(String pushId, DataCallback<Boolean> callback) {
        String loverId = new UserDaoUtil().getUserDao().getLoverId();
        AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
        query.whereEqualTo(TableConstant.BindLover.USER, getUserObj(loverId));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        avObject.put(TableConstant.BindLover.LOVER_PUSH_ID, pushId);
                        avObject.saveInBackground(new SaveCallback() {
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

    public void getPeerPushId(DataCallback<String> callback) {
        AVQuery<AVObject> query = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
        query.whereEqualTo(TableConstant.BindLover.USER, AVUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                handleResult(e, callback, new onResultSuc() {
                    @Override
                    public void onSuc() {
                        String peerPushId = avObject.getString(TableConstant.BindLover.LOVER_PUSH_ID);
                        callback.onSuccess(peerPushId);
                    }
                });
            }
        });
    }

    public void addFeedBack(String content, String userNumber, String filePath, final DataCallback<Boolean> dataCallback) {
        Observable observable = null;
        if (LibUtils.isNullOrEmpty(filePath)) {
            observable = addFeedBack(content, userNumber, null);
        } else {
            observable = uploadPic(filePath).concatMap(new Function<AVFile, Observable<Boolean>>() {
                @Override
                public Observable<Boolean> apply(AVFile avFile) throws Exception {
                    return addFeedBack(content, userNumber, avFile);
                }
            });
        }
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        dataCallback.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dataCallback.onFailed(-1, throwable.getMessage());
                    }
                });
    }

    private Observable uploadPic(String filePath) {
        return Observable.create(new ObservableOnSubscribe<AVFile>() {
            @Override
            public void subscribe(ObservableEmitter<AVFile> emitter) throws Exception {
                String name = "img.jpg";
                if (filePath.contains("/")) {
                    name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                }
                try {
                    AVFile file = AVFile.withAbsoluteLocalPath(name, filePath);
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                emitter.onNext(file);
                            } else {
                                emitter.onError(new Throwable(e.getMessage() + e.getCode()));
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    emitter.onError(new Throwable("FileNotFoundException"));
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    private Observable addFeedBack(String content, String userNumber, AVFile imgFile) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            AVObject feedbackObj = new AVObject(TableConstant.FeedBack.CLAZZ_NAME);
            feedbackObj.put(TableConstant.FeedBack.CONTANT, content);
            if (imgFile != null) {
                feedbackObj.put(TableConstant.FeedBack.IMG, imgFile);
            }
            feedbackObj.put(TableConstant.FeedBack.BAND, LibDeviceUtils.getBand() + " " + LibDeviceUtils.getModel());
            feedbackObj.put(TableConstant.FeedBack.UPLOADER, AVObject.createWithoutData(TableConstant.AVUserClass.CLAZZ_NAME, AVUser.getCurrentUser().getObjectId()));

            feedbackObj.put(TableConstant.FeedBack.SYSTEM_VERSION, LibDeviceUtils.getAndroidVersion());
            feedbackObj.put(TableConstant.FeedBack.APP_VERSION, getAppVersion());
            feedbackObj.put(TableConstant.FeedBack.USER_NUMBBER, userNumber);
            feedbackObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    emitter.onNext(true);
                }
            });
        }).subscribeOn(Schedulers.newThread());
    }

    private String getAppVersion() {
        Context applicationContext = BaseApplication.getInstance().getApplicationContext();
        String versionName = "";
        try {
            versionName = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
