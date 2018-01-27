package com.yazhi1992.moon.ui.home.set;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.yazhi1992.moon.AppApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.FragmentSetBinding;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.widget.PageRouter;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.Arrays;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class SetFragment extends Fragment {

    private FragmentSetBinding mBinding;
    private UserDaoUtil mUserDaoUtil;
    private SetViewModel mViewModel = new SetViewModel();
    private SetPresenter mPresenter = new SetPresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false);
        mBinding.setItem(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserDaoUtil = new UserDaoUtil();
        User user = mUserDaoUtil.getUserDao();
        if(user != null) {
            mViewModel.myName.set(user.getName());
            mViewModel.myHeadUrl.set(user.getHeadUrl());
            mViewModel.loverHeadUrl.set(user.getLoverHeadUrl());
            mViewModel.loverName.set(user.getLoverName());

            jerryReceiveMsgFromTom();
        }

        mBinding.igLover.setOnClickListener(v -> {
        });

        mBinding.tvName.setOnClickListener(v -> {

        });

        mBinding.btnSendMsg.setOnClickListener(v -> {
            sendMessageToJerryFromTom();
        });

        mBinding.btnLogout.setOnClickListener(v -> {
            mUserDaoUtil.clear();
            PageRouter.gotoLogin();
            getActivity().finish();
        });

        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
    }

    public void sendMessageToJerryFromTom() {
        // Tom 用自己的名字作为clientId，获取AVIMClient对象实例
        AVIMClient tom = AVIMClient.getInstance(mViewModel.myName.get());
        // 与服务器连接
        tom.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    // 创建与Jerry之间的对话
                    client.createConversation(Arrays.asList("兰德里"), "Tom & Jerry", null,
                            new AVIMConversationCreatedCallback() {

                                @Override
                                public void done(AVIMConversation conversation, AVIMException e) {
                                    if (e == null) {
                                        AVIMTextMessage msg = new AVIMTextMessage();
                                        msg.setText("耗子，起床！");
                                        // 发送消息
                                        conversation.sendMessage(msg, new AVIMConversationCallback() {

                                            @Override
                                            public void done(AVIMException e) {
                                                if (e == null) {
                                                    LibUtils.showToast(getActivity(), "发送成功");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }

    public void jerryReceiveMsgFromTom(){
        //Jerry登录
        AVIMClient jerry = AVIMClient.getInstance(mViewModel.myName.get());
        jerry.open(new AVIMClientCallback(){

            @Override
            public void done(AVIMClient client,AVIMException e){
                if(e==null){
                }
            }
        });
    }

    public static class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){
            if(message instanceof AVIMTextMessage){
                LibUtils.showToast(AppApplication.getInstance(), ((AVIMTextMessage)message).getText());
            }
        }

        public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){

        }
    }
}
