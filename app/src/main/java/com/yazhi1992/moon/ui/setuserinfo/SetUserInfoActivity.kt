package com.yazhi1992.moon.ui.setuserinfo

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.yazhi1992.moon.R
import com.yazhi1992.moon.databinding.ActivitySetUserInfoBinding
import com.yazhi1992.moon.ui.BaseActivity

/**
 * 注册后填写个人信息页面
 */
class SetUserInfoActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySetUserInfoBinding
    private val mModel = SetUserInfoModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivitySetUserInfoBinding>(this@SetUserInfoActivity, R.layout.activity_set_user_info)
        mBinding.model = mModel
        initToolBar(mBinding.toolbar)
    }
}
