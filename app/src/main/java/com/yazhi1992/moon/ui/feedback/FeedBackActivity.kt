package com.yazhi1992.moon.ui.feedback

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.BuildConfig
import com.yazhi1992.moon.R
import com.yazhi1992.moon.api.DataCallback
import com.yazhi1992.moon.constant.ActionConstant
import com.yazhi1992.moon.constant.SPKeyConstant
import com.yazhi1992.moon.databinding.ActivityFeedBackBinding
import com.yazhi1992.moon.event.AddDataEvent
import com.yazhi1992.moon.ui.BaseActivity
import com.yazhi1992.moon.ui.ViewBindingUtils
import com.yazhi1992.moon.util.UploadPhotoHelper
import com.yazhi1992.yazhilib.utils.LibSPUtils
import com.yazhi1992.yazhilib.utils.LibUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.greenrobot.eventbus.EventBus

@Route(path = ActivityRouter.FEED_BACK)
class FeedBackActivity : BaseActivity() {

    lateinit var mBinding : ActivityFeedBackBinding
    var mImgPath : String ?= null
    val mPresenter = FeedBackPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityFeedBackBinding>(this, R.layout.activity_feed_back)

        initToolBar(mBinding.toolbar)

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.feedbackEtConnectNumber)

        mBinding.feedbackIgAdd.visibility = if (LibSPUtils.getBoolean(SPKeyConstant.PUSH_IMG_ENABLE, true)) View.VISIBLE else View.GONE

        mBinding.feedbackIgAdd.setOnClickListener { //选择图片
            Matisse.from(this)
                    .choose(MimeType.ofImage())
                    .showSingleMediaType(true)
                    .capture(true)
                    .captureStrategy(CaptureStrategy(true, if (BuildConfig.DEBUG) "com.yazhi1992.moon.debug.provider" else "com.yazhi1992.moon.provider"))
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(GlideEngine())
                    .forResult(10) }

        feedback_btn_add.setOnClickListener {
            val content = feedbackEtContent.text.toString()
            if (content.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.add_text_empty))
                return@setOnClickListener
            }
            val number = feedback_et_connect_number.text.toString()
            feedback_btn_add.isLoading = true
            //todo 封装上传
            if (LibUtils.notNullNorEmpty(mImgPath)) {
                UploadPhotoHelper.uploadPhoto(this, mImgPath, { filePath, callback ->
                    mPresenter.addFeedback(content, number, mImgPath, object : DataCallback<Boolean> {
                        override fun onSuccess(isSuc: Boolean) {
                            callback.onSuccess("")
                        }

                        override fun onFailed(code: Int, msg: String) {
                            callback.onFailed(code, msg)
                        }
                    })
                }, object : UploadPhotoHelper.onUploadPhotoListener {
                    override fun onStart() {

                    }

                    override fun onSuc(remoteImgUrl: String) {
                        mBinding.feedbackBtnAdd.isLoading = false
                        EventBus.getDefault().post(AddDataEvent(ActionConstant.ADD_TEXT))
                        LibUtils.hideKeyboard(feedbackEtContent)
                        LibUtils.showToast(this@FeedBackActivity, getString(R.string.feed_back_suc))
                        finish()
                    }

                    override fun onError(msg: String) {
                        LibUtils.showToast(this@FeedBackActivity, msg)
                        mBinding.feedbackBtnAdd.isLoading = false
                    }
                })
            } else {
                mPresenter.addFeedback(content, number, mImgPath, object : DataCallback<Boolean> {
                    override fun onSuccess(isSuc: Boolean) {
                        mBinding.feedbackBtnAdd.isLoading = false
                        EventBus.getDefault().post(AddDataEvent(ActionConstant.ADD_TEXT))
                        LibUtils.hideKeyboard(feedbackEtContent)
                        LibUtils.showToast(this@FeedBackActivity, getString(R.string.feed_back_suc))
                        finish()
                    }

                    override fun onFailed(code: Int, msg: String) {
                        LibUtils.showToast(this@FeedBackActivity, msg)
                        mBinding.feedbackBtnAdd.isLoading = false
                    }
                })
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            val uris = Matisse.obtainPathResult(data)
            mImgPath = uris[0]
            ViewBindingUtils.imgUrl(mBinding.feedbackIgAdd, mImgPath)
        }
    }
}
