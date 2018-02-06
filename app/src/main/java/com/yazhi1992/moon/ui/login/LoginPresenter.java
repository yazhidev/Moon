package com.yazhi1992.moon.ui.login;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSBase;
import com.avos.sns.SNSCallback;
import com.avos.sns.SNSException;
import com.avos.sns.SNSType;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zengyazhi on 2018/1/26.
 */

// TODO: 2018/1/28 未安装QQ状态
public class LoginPresenter {
    private SNSType ThirdPartyType = SNSType.AVOSCloudSNSQQ;
    private DataCallback<Boolean> mCallback;
    final SNSCallback myCallback = new SNSCallback() {
        @Override
        public void done(SNSBase object, SNSException e) {
            if (e == null) {
                JSONObject jsonObject = object.authorizedData();
                if (jsonObject != null) {
                    try {
                        String nickname = object.authorizedData().getString("nickname");
                        String headUrl = object.authorizedData().getString("figureurl_qq_2");
                        SNS.loginWithAuthData(object.userInfo(), new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                //关联成功，已在 _User 表新增一条用户数据
                                //修改用户名为QQ名，头像为QQ头像
                                avUser.setUsername(nickname);
                                avUser.put(TableConstant.AVUserClass.HEAD_URL, headUrl);
                                avUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            //插入数据库
                                            User user = new User();
                                            user.setName(nickname);
                                            user.setHeadUrl(headUrl);
                                            user.setInviteNumber(avUser.getString(TableConstant.AVUserClass.INVITE_NUMBER));
                                            user.setObjectId(avUser.getObjectId());
                                            if (LibUtils.notNullNorEmpty(avUser.getString(TableConstant.AVUserClass.LOVER_ID))) {
                                                user.setHaveLover(true);
                                                user.setLoverId(avUser.getString(TableConstant.AVUserClass.LOVER_ID));
                                                user.setLoverName(avUser.getString(TableConstant.AVUserClass.LOVER_NAME));
                                                user.setLoverHeadUrl(avUser.getString(TableConstant.AVUserClass.LOVER_HEAD_URL));
                                            }
                                            new UserDaoUtil().insert(user);

                                            if (mCallback != null) {
                                                mCallback.onSuccess(user.getHaveLover());
                                            }
                                        }
                                    }
                                });

                            }
                        });
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
            } else {
                LoadingHelper.getInstance().closeLoading();
            }
        }
    };

    void init(Activity activity) {
        SNS.logout(activity, ThirdPartyType);
        new UserDaoUtil().clear();
    }

    /**
     * qq授权登录
     *
     * @param activity
     * @param callback true 已绑定另一半，false 未绑定
     */
    void loginWithQQ(Activity activity, DataCallback<Boolean> callback) {
        SNS.logout(activity, ThirdPartyType);
        try {
            if (callback != null) {
                mCallback = callback;
            }
            LoadingHelper.getInstance().showLoading(activity);
            SNS.setupPlatform(activity, SNSType.AVOSCloudSNSQQ, BuildConfig.QQ_ID, BuildConfig.QQ_KEY, "");
            SNS.loginWithCallback(activity, SNSType.AVOSCloudSNSQQ, myCallback);
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        SNS.onActivityResult(requestCode, resultCode, data, ThirdPartyType);
    }
}
