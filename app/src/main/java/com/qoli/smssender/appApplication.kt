package com.qoli.smssender

import android.app.Application
import android.view.Gravity
import com.hjq.toast.ToastUtils

class appApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        ToastUtils.init(this);
        ToastUtils.setGravity(Gravity.BOTTOM,0,80)
    }
}
