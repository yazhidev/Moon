package com.yazhi1992.moon.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yazhi1992.moon.R
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.layout_item.view.*

class Main2Activity : AppCompatActivity() {

    public var data = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        floatingBtn.setOnClickListener { }

        for (i in 0..20) {
            data.add("第" + i)
        }

        ry.layoutManager = LinearLayoutManager(this)
        val myAdapter = MyAdapter()
        myAdapter.datalist = data
        ry.adapter = myAdapter
        ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scroll Down
                    if (floatingBtn.isShown()) {
                        floatingBtn.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!floatingBtn.isShown()) {
                        floatingBtn.show();
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

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
            holder!!.text.setText(datalist.get(position))
        }

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text = view.text
    }
}
