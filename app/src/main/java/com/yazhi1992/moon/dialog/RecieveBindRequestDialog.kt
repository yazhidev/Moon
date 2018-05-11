package com.yazhi1992.moon.dialog

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import com.yazhi1992.moon.R
import com.yazhi1992.moon.databinding.DialogRecieveBindRequestBinding

/**
 * Created by zengyazhi on 2018/5/11.
 */
class RecieveBindRequestDialog: BaseDialog() {

    lateinit var binding: DialogRecieveBindRequestBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate<DialogRecieveBindRequestBinding>(inflater, R.layout.dialog_recieve_bind_request, null, false)
        val window = dialog.window
        val lp = window!!.attributes
        lp.gravity = Gravity.CENTER
        window.attributes = lp
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindLoverDialogCancel.setOnClickListener { dismiss() }
    }
}