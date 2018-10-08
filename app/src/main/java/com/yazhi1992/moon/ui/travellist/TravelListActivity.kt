package com.yazhi1992.moon.ui.travellist

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.adapter.TravelListViewBinder
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter
import com.yazhi1992.moon.api.Api
import com.yazhi1992.moon.api.DataCallback
import com.yazhi1992.moon.databinding.ActivityTravelListBinding
import com.yazhi1992.moon.dialog.AddTravelListDialog
import com.yazhi1992.moon.dialog.ItemsDialog
import com.yazhi1992.moon.dialog.LoadingHelper
import com.yazhi1992.moon.ui.BaseActivity
import com.yazhi1992.moon.util.TipDialogHelper
import com.yazhi1992.moon.viewmodel.TravelListItemDataBean
import com.yazhi1992.yazhilib.utils.LibUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.drakeet.multitype.Items
import java.util.concurrent.TimeUnit

@Route(path = ActivityRouter.TRAVEL_LIST)
class TravelListActivity : BaseActivity() {

    lateinit var mBinding: ActivityTravelListBinding
    private var mAdapter = CustomMultitypeAdapter()
    private var mItems = Items()
    private var mOriginalItems = Items()
    private val mAddTravelListDialog by lazy {
        AddTravelListDialog().apply {
            setOnFinishListener { content, id ->
                mBinding.multiView.visibility = View.GONE
                val itemDataBean = TravelListItemDataBean(content, false, id)
                mItems.add(itemDataBean)
                mAdapter.notifyItemInserted(mItems.size - 1)
                LibUtils.showToast("添加成功！")
            }
        }
    }
    private val mMenuDialog by lazy {
        val items = ArrayList<String>()
        items.add(getString(R.string.travel_menu_text1))
        items.add(getString(R.string.travel_menu_text2))
        ItemsDialog.getInstance(items) { position ->
            if (position == 0) {
                //新增
                mAddTravelListDialog.showDialog(fragmentManager)
            } else if (position == 1) {
                //删除
                val showMsg = "是否清空所有事项的准备状态？"
                TipDialogHelper.getInstance().showDialog(this@TravelListActivity, showMsg) {
                    mItems.forEachIndexed { index, value->
                        var temp = value as TravelListItemDataBean
                        temp.mComplete.set(false)
                        mItems[index] = temp
                    }
                }
            }
        }
    }
    private val mEditDialog by lazy {
        val items = ArrayList<String>()
        items.add(getString(R.string.travel_list_edit))
        items.add(getString(R.string.travel_list_delete))
        ItemsDialog.getInstance(items) { position ->
            if (position == 0) {
                //编辑
                val chooseItem = mItems[mChoosedPosition] as TravelListItemDataBean
                AddTravelListDialog.getInstance(chooseItem.mDes.get(), chooseItem.mObjectId)
                        .apply {
                            setOnFinishListener { content, id ->
                                chooseItem.mDes.set(content)
                                mItems[mChoosedPosition] = chooseItem
                                mAdapter.notifyItemChanged(mChoosedPosition)
                                LibUtils.showToast("修改成功！")
                            }
                            showDialog(this@TravelListActivity.fragmentManager)
                        }
            } else if (position == 1) {
                //删除
                val showMsg = "是否删除该事项？"
                TipDialogHelper.getInstance().showDialog(this@TravelListActivity, showMsg) {
                    val chooseItem = mItems[mChoosedPosition] as TravelListItemDataBean
                    Api.getInstance().deleteTravelListItemData(chooseItem.mObjectId, object : DataCallback<Boolean> {
                        override fun onSuccess(data: Boolean?) {
                            mItems.removeAt(mChoosedPosition)
                            mAdapter.notifyItemRemoved(mChoosedPosition)
                        }

                        override fun onFailed(code: Int, msg: String) {
                            LibUtils.showToast(msg)
                        }
                    })
                }
            }
        }
    }
    private var mChoosedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_travel_list)
        initToolBar(mBinding.toolbar)
        val travelListViewBinder = TravelListViewBinder()
        travelListViewBinder.setOnItemLongClickListener { position ->
            mChoosedPosition = position
            //长按 编辑/删除
            mEditDialog.show(fragmentManager)
        }
        mAdapter.register(TravelListItemDataBean::class.java, travelListViewBinder)
        mAdapter.items = mItems
        //        mBinding.ryTravel.setPadding(0, (int) LibCalcUtil.dp2px(this, 20), 0, 0);
        mBinding.ryTravel.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mBinding.ryTravel.adapter = mAdapter
        mBinding.ryTravel.layoutManager = LinearLayoutManager(this)
        mBinding.smartRefresh.setOnRefreshListener { refreshlayout -> getDatas() }
        mBinding.smartRefresh.autoRefresh()
    }

    private fun getDatas() {
        Api.getInstance().getTravelList(object : DataCallback<List<TravelListItemDataBean>> {
            override fun onSuccess(data: List<TravelListItemDataBean>) {
                mBinding.smartRefresh.finishRefresh()
                if (data.isEmpty()) {
                    mBinding.multiView.showEmpty()
                } else {
                    mItems.clear()
                    mItems.addAll(data)
                    mOriginalItems = mItems;
                    mAdapter.notifyDataSetChanged()
                }
                Observable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { mBinding.smartRefresh.isEnableRefresh = false }
            }

            override fun onFailed(code: Int, msg: String) {
                mBinding.smartRefresh.finishRefresh()
                mBinding.multiView.showNetErr()
            }
        })
    }

    //添加右上角加号按钮
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.travel_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_travel -> {
                mMenuDialog.show(fragmentManager)
            }
            else -> {
            }
        }
        return true
    }

    override fun onBackPressed() {
        saveAllStatus { quit ->
            if (quit) {
                if (!isFinishing) {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun clickToolbarBack() {
        saveAllStatus { quit ->
            if (quit) {
                super.clickToolbarBack()
            }
        }
    }

    private fun saveAllStatus(callback: (Boolean) -> Unit) {
        if(mOriginalItems == mItems) {
            //没有修改
            callback(true)
            return
        }
        LoadingHelper.getInstance().showLoading(this)
        var datas = mItems as ArrayList<TravelListItemDataBean>
        Api.getInstance().uploadAllItemStatus(datas, object : DataCallback<Boolean> {
            override fun onFailed(code: Int, msg: String?) {
                Observable.just(0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            LoadingHelper.getInstance().closeLoading()
                            val showMsg = "保存失败，仍然退出？"
                            TipDialogHelper.getInstance().showDialog(this@TravelListActivity
                                    , showMsg
                                    , "重试"
                                    , { clickToolbarBack() }
                                    , { callback(true) })
                        }
            }

            override fun onSuccess(data: Boolean?) {
                Observable.just(0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            LoadingHelper.getInstance().closeLoading()
                            callback(true)
                        }
            }
        })

    }
}
