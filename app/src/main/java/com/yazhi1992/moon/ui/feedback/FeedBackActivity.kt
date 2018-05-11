package com.yazhi1992.moon.ui.feedback

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.databinding.ActivityFeedBackBinding
import com.yazhi1992.moon.ui.BaseActivity

@Route(path = ActivityRouter.FEED_BACK)
class FeedBackActivity : BaseActivity() {

    lateinit var mBinding : ActivityFeedBackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityFeedBackBinding>(this, R.layout.activity_feed_back)

        initToolBar(mBinding.toolbar)


    }
}
