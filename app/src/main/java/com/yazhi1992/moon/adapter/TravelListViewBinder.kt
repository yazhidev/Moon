package com.yazhi1992.moon.adapter

import com.yazhi1992.moon.BaseApplication
import com.yazhi1992.moon.R
import com.yazhi1992.moon.adapter.base.DataBindingViewBinder
import com.yazhi1992.moon.databinding.ItemTravelListBinding
import com.yazhi1992.moon.dialog.CompleteTravelListDialog
import com.yazhi1992.moon.viewmodel.TravelListItemDataBean

/**
 * Created by zengyazhi on 2018/1/30.
 */
class TravelListViewBinder : DataBindingViewBinder<TravelListItemDataBean>(R.layout.item_travel_list) {

    override fun BindViewHolder(holder: DataBindingViewBinder.DatabindingViewHolder<*>, bean: TravelListItemDataBean) {
        super.BindViewHolder(holder, bean)
        val binding = holder.binding as ItemTravelListBinding
        binding.root.setOnClickListener {
            binding.checkBox.isChecked = !binding.checkBox.isChecked
            bean.mComplete.set(binding.checkBox.isChecked)
            checkAllComplete(bean)
        }

        binding.checkBox.setOnClickListener {
            bean.mComplete.set(binding.checkBox.isChecked)
            checkAllComplete(bean)
        }
    }

    private fun checkAllComplete(bean: TravelListItemDataBean) {
        if (bean.mComplete.get()) {
            //检测是否全部完成
            val items = adapter.items as ArrayList<TravelListItemDataBean>
            var allComplete = true
            items.forEach {
                if (!it.mComplete.get()) {
                    allComplete = false
                    return@forEach
                }
            }
            if (allComplete) {
                val completeTravelListDialog = CompleteTravelListDialog()
                completeTravelListDialog.showDialog(BaseApplication.getInstance().topActivity.fragmentManager)
            }
        }
    }

}
