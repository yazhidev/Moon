package com.yazhi1992.moon.adapter

import android.support.v7.util.DiffUtil

/**
 * Created by zengyazhi on 2017/12/29.
 */

class MyDiffUtil(var oldDatas: MutableList<String>, var newDatas: MutableList<String>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDatas[oldItemPosition].equals(newDatas[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldDatas.size
    }

    override fun getNewListSize(): Int {
        return newDatas.size

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDatas[oldItemPosition].equals(newDatas[newItemPosition])
    }
}
