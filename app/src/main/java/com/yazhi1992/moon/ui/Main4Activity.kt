package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yazhi1992.moon.R
import com.yazhi1992.moon.adapter.MyDiffUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.layout_item.view.*

class Main4Activity : AppCompatActivity() {

    public var data = ArrayList<String>()

    public var oldData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)


        ry.layoutManager = LinearLayoutManager(this)
        val myAdapter = MyAdapter()
        myAdapter.datalist = data
        ry.adapter = myAdapter

        button.setOnClickListener {
            Observable.just(1)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        if (data.size == 0) {
                            for (i in 0..20) {
                                data.add("第" + i)
                            }
                        } else {
                            data.add(0, "test")
                        }


                        calculateDiff = DiffUtil.calculateDiff(MyDiffUtil(oldData, data))

                        oldData.clear()
                        oldData.addAll(data)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        calculateDiff.dispatchUpdatesTo(myAdapter)
                    }
        }
    }

    lateinit var calculateDiff: DiffUtil.DiffResult

    class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        var datalist = ArrayList<String>()

        override fun getItemCount(): Int {
            return datalist.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_item, null, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
            holder!!.text.text = datalist.get(position)
        }

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text = view.text
    }
}
