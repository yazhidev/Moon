package com.yazhi1992.moon.ui.travellist

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.adapter.PopBinder
import com.yazhi1992.moon.adapter.TravelListViewBinder
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter
import com.yazhi1992.moon.api.Api
import com.yazhi1992.moon.api.DataCallback
import com.yazhi1992.moon.databinding.ActivityTravelListBinding
import com.yazhi1992.moon.dialog.AddTravelListDialog
import com.yazhi1992.moon.dialog.ItemsDialog
import com.yazhi1992.moon.dialog.ItemsDialog.getInstance
import com.yazhi1992.moon.dialog.LoadingHelper
import com.yazhi1992.moon.ui.BaseActivity
import com.yazhi1992.moon.util.TipDialogHelper
import com.yazhi1992.moon.viewmodel.TravelListItemDataBean
import com.yazhi1992.moon.viewmodel.TravelListTableDataBean
import com.yazhi1992.moon.widget.RecyclerPopupWindow
import com.yazhi1992.yazhilib.utils.LibUtils
import com.yazhi1992.yazhilib.widget.RelativePopupWindow
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

    private fun setShowTable(tableBean: TravelListTableDataBean) {
        mShowTableData = tableBean
        mBinding.toolbar.title = mShowTableData.mName.get()
        mTables.remove(tableBean)
        mTables.add(0, tableBean)
    }

    private val mEditDialog by lazy {
        val items = ArrayList<String>()
        items.add(getString(R.string.travel_list_edit))
        items.add(getString(R.string.travel_list_delete))
        getInstance(items) { position ->
            if (position == 0) {
                //编辑
                val chooseItem = mItems[mChoosedPosition] as TravelListItemDataBean
                AddTravelListDialog.editName(chooseItem.mDes.get(), chooseItem.mObjectId, false)
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
    private var mRecyclerViewState: Int = 0
    private var mTables = mutableListOf<TravelListTableDataBean>()
    private lateinit var mShowTableData: TravelListTableDataBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_travel_list)
        mBinding.toolbar.title = "加载中..."
        initToolBar(mBinding.toolbar)
        val travelListViewBinder = TravelListViewBinder()
        travelListViewBinder.setOnItemLongClickListener { position ->
            mChoosedPosition = position
            //长按 编辑/删除
            mEditDialog.show(fragmentManager)
        }
        mAdapter.register(TravelListItemDataBean::class.java, travelListViewBinder)
        mAdapter.items = mItems
        mBinding.ryTravel.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mBinding.ryTravel.adapter = mAdapter
        mBinding.ryTravel.layoutManager = LinearLayoutManager(this)
        mBinding.smartRefresh.setOnRefreshListener { refreshlayout -> getDatas() }
        refresh()

        mBinding.ryTravel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mRecyclerViewState == 1) {
                    //滑动中，smartRefreshLayout autoRefresh 时有时会触发滑动，但是state=2
                    if (dy > 0) {
                        // Scroll Down
                        if (mBinding.fab.isShown) {
                            mBinding.fab.hide()
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!mBinding.fab.isShown) {
                            mBinding.fab.show()
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mRecyclerViewState = newState
            }
        })

        mBinding.fab.setOnClickListener {
            if (mTables.isEmpty()) {
                mNoTableMenuDialog.show(fragmentManager)
            } else {
                if (mItems.isEmpty()) {
                    mEmptyTableMenuDialog.show(fragmentManager)
                } else {
                    mMenuDialog.show(fragmentManager)
                }
            }
        }
    }

    private fun getTables() {
        mTables.clear()
        Api.getInstance().getTravelListTable(object : DataCallback<List<TravelListTableDataBean>> {
            override fun onFailed(code: Int, msg: String?) {
                mBinding.smartRefresh.finishRefresh()
                mBinding.multiView.showNetErr()
                mBinding.toolbar.title = "暂无清单"
            }

            override fun onSuccess(data: List<TravelListTableDataBean>) {
                mBinding.smartRefresh.finishRefresh()
                if (data.isEmpty()) {
                    mBinding.toolbar.title = "暂无清单"
                    mBinding.multiView.showEmpty()
                } else {
                    mTables.addAll(data)
                    setShowTable(data[0])
                    checkMenuVisible()
                    //获取清单事项
                    getTableItems()
                }
            }
        })
    }

    private fun refresh() {
        //清空已有事项
        mBinding.multiView.showNormal()
        mItems.clear()
        mOriginalItems.clear()
        mAdapter.notifyDataSetChanged()
        //获取数据
        mBinding.smartRefresh.setEnableRefresh(true)
        mBinding.smartRefresh.autoRefresh()
    }

    private fun getDatas() {
        if (mTables.isEmpty()) {
            getTables()
        } else {
            getTableItems()
        }
    }

    private fun updateTable() {
        Api.getInstance().updateTable(mShowTableData.mObjectId)
    }

    private fun getTableItems() {
        Api.getInstance().getTravelList(mShowTableData.mObjectId, object : DataCallback<List<TravelListItemDataBean>> {
            override fun onSuccess(data: List<TravelListItemDataBean>) {
                mBinding.smartRefresh.finishRefresh()
                if (data.isEmpty()) {
                    mBinding.multiView.showEmpty()
                } else {
                    mBinding.multiView.showNormal()
                    mItems.clear()
                    mItems.addAll(data)
                    mItems.forEach {
                        val temp = it as TravelListItemDataBean
                        mOriginalItems.add(temp.deepClone())
                    }
                    mAdapter.notifyDataSetChanged()
                }
                Observable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { mBinding.smartRefresh.setEnableRefresh(false) }
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

    private var tablePopShowing = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_travel -> {
                if (mTables.isEmpty()) {
                    LibUtils.showToast("暂无清单")
                    return true
                }
                if (!tablePopShowing) {
                    Observable.timer(RecyclerPopupWindow.ANIM_TIME, TimeUnit.MILLISECONDS)
                            .subscribe { tablePopShowing = false }
                    showTablesPop()
                }
            }
            else -> {
            }
        }
        return true
    }

    private lateinit var popup: RecyclerPopupWindow

    private fun showTablesPop() {
        val adapter = CustomMultitypeAdapter()
        val binder = PopBinder()
        val items = Items()
        binder.setOnItemClickListener { position ->
            saveAllStatus("保存失败，仍然切换清单？") { result ->
                if (result) {
                    val temp = items[position] as TravelListTableDataBean
                    setShowTable(temp)
                    updateTable()
                    //再获取新的清单的列表
                    refresh()
                }
            }
            popup.dismiss()
        }
        adapter.register(TravelListTableDataBean::class.java, binder)
        items.addAll(mTables)
        //不显示当前清单
        items.removeAt(0)
        adapter.items = items

        popup = RecyclerPopupWindow(this, adapter)
        popup.showOnAnchor(mBinding.toolbar, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true)
    }

    override fun onBackPressed() {
        clickBack { quit ->
            if (quit) {
                if (!isFinishing) {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun clickToolbarBack() {
        clickBack { quit ->
            if (quit) {
                super.clickToolbarBack()
            }
        }
    }

    private fun clickBack(callback: (Boolean) -> Unit) {
        saveAllStatus("保存失败，仍然退出？", callback)
    }

    private fun saveAllStatus(errHint: String, callback: (Boolean) -> Unit) {
        val tempList = mItems as ArrayList<TravelListItemDataBean>
        if (!mOriginalItems.isEmpty() && !tempList.isEmpty()) {
            //比较是否修改了
            mOriginalItems.forEachIndexed { originindex, originvalue ->
                val original = originvalue as TravelListItemDataBean
                var equalIndex = -1
                run temp@{
                    tempList.forEachIndexed { index, value ->
                        //剔除状态没有改变的item
                        if ((value.mObjectId == original.mObjectId)
                                && value.mComplete.get() == original.mComplete.get()) {
                            equalIndex = index
                            return@temp
                        }
                    }
                }
                if (equalIndex >= 0) {
                    tempList.removeAt(equalIndex)
                }
            }
        }
        if (tempList.isEmpty()) {
            callback(true)
            return
        }
        LoadingHelper.getInstance().showLoading(this)
        Api.getInstance().uploadAllItemStatus(tempList, object : DataCallback<Boolean> {
            override fun onFailed(code: Int, msg: String?) {
                Observable.just(0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            LoadingHelper.getInstance().closeLoading()
                            TipDialogHelper.getInstance().showDialog(this@TravelListActivity
                                    , errHint
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

    private val mNoTableMenuDialog by lazy {
        val items = ArrayList<String>()
        //只有新增清单
        items.add(getString(R.string.travel_menu_text3))
        ItemsDialog.getInstance(items) {
            when (it) {
                0 -> newTable()
            }
        }
    }
    private val mEmptyTableMenuDialog by lazy {
        val items = ArrayList<String>()
        items.add(getString(R.string.travel_menu_text1))
        items.add(getString(R.string.travel_menu_text3))
        items.add(getString(R.string.travel_menu_text4))
        items.add(getString(R.string.travel_menu_text5))
        ItemsDialog.getInstance(items) {
            when (it) {
                0 -> newItem()
                1 -> newTable()
                2 -> editTableName()
                3 -> deleteTable()
            }
        }
    }
    private val mMenuDialog by lazy {
        val items = ArrayList<String>()
        items.add(getString(R.string.travel_menu_text1))
        items.add(getString(R.string.travel_menu_text2))
        items.add(getString(R.string.travel_menu_text3))
        items.add(getString(R.string.travel_menu_text4))
        items.add(getString(R.string.travel_menu_text5))
        getInstance(items) {
            when (it) {
                0 -> newItem()
                1 -> resetAllItem()
                2 -> newTable()
                3 -> editTableName()
                4 -> deleteTable()
            }
        }
    }

    private fun resetAllItem() {
        val showMsg = "是否清空所有事项的准备状态？"
        TipDialogHelper.getInstance().showDialog(this@TravelListActivity, showMsg) {
            mItems.forEachIndexed { index, value ->
                var temp = value as TravelListItemDataBean
                temp.mComplete.set(false)
                mItems[index] = temp
            }
        }
    }

    private fun deleteTable() {
        val showMsg = "是否删除该清单及其所有事项？"
        TipDialogHelper.getInstance().showDialog(this@TravelListActivity, showMsg) {
            Api.getInstance().deleteAllItemOfTable(mShowTableData.mObjectId, object : DataCallback<Boolean> {
                override fun onSuccess(data: Boolean?) {
                    mTables.remove(mShowTableData)
                    checkMenuVisible()
                    if(mTables.isEmpty()) {
                        mBinding.multiView.showEmpty()
                        mBinding.toolbar.title = "暂无清单"
                    } else {
                        setShowTable(mTables[0])
                        //再获取新的清单的列表
                        refresh()
                    }
                }

                override fun onFailed(code: Int, msg: String?) {
                }
            })
        }
    }

    private fun editTableName() {
        AddTravelListDialog.editName(mShowTableData.mName.get(), mShowTableData.mObjectId, true)
                .apply {
                    setOnFinishListener { content, id ->
                        mShowTableData.mName.set(content)
                        mBinding.toolbar.title = content
                        LibUtils.showToast("修改成功！")
                    }
                    showDialog(this@TravelListActivity.fragmentManager)
                }
    }

    private fun newTable() {
        saveAllStatus("保存失败，仍然新增清单？") { result ->
            if (result)
                AddTravelListDialog.newTable()
                        .apply {
                            setOnFinishListener { content, id ->
                                LibUtils.showToast("新增清单成功！")
                                setShowTable(TravelListTableDataBean(content, id))
                                checkMenuVisible()
                                //再获取新的清单的列表
                                refresh()
                            }
                            showDialog(this@TravelListActivity.fragmentManager)
                        }
        }
    }

    private fun newItem() {
        AddTravelListDialog.newItem(mShowTableData.mObjectId)
                .apply {
                    setOnFinishListener { content, id ->
                        mBinding.multiView.showNormal()
                        val itemDataBean = TravelListItemDataBean(content, false, id)
                        mItems.add(itemDataBean)
                        mAdapter.notifyItemInserted(mItems.size - 1)
                        LibUtils.showToast("添加成功！")
                    }
                    showDialog(this@TravelListActivity.fragmentManager)
                }
    }

    private lateinit var mMenu: Menu

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        checkMenuVisible()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun checkMenuVisible() {
        mMenu.getItem(0).isVisible = mTables.size > 1
    }
}
