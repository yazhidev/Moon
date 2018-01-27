package com.yazhi1992.moon.ui.startup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.GetCallback
import com.yazhi1992.moon.R
import com.yazhi1992.moon.constant.NameContant
import com.yazhi1992.moon.sql.UserDaoUtil
import com.yazhi1992.moon.widget.PageRouter
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe { start() }
    }

    private fun start() {
        if (UserDaoUtil().userDao == null) {
            //未登录
            PageRouter.gotoLogin()
            finish()
        } else {
            val daoUtil = UserDaoUtil()
            if(daoUtil.userDao.haveLover) {
                //已绑定
                PageRouter.gotoHomePage()
                finish()
            } else {
                AVUser.getCurrentUser().fetchInBackground(object : GetCallback<AVObject>() {
                    override fun done(p0: AVObject?, p1: AVException?) {
                        if(p1 == null) {
                            if(p0!!.getBoolean(NameContant.AVUserClass.HAVE_LOVER)) {
                                daoUtil.updateLoveInfo(p0!!.getString(NameContant.AVUserClass.LOVER_ID)
                                        , p0!!.getString(NameContant.AVUserClass.LOVER_NAME)
                                        , p0!!.getString(NameContant.AVUserClass.LOVER_HEAD_URL))
                                //已绑定
                                PageRouter.gotoHomePage()
                            } else {
                                //未绑定
                                PageRouter.gotoBindLover()
                            }
                        } else {
                            //异常
                            PageRouter.gotoLogin()
                        }
                        finish()
                    }
                })
            }
        }
    }

}
