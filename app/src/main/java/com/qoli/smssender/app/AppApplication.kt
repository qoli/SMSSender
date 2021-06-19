package com.qoli.smssender.app

import android.app.Application
import android.content.ContextWrapper
import android.view.Gravity
import com.hjq.toast.ToastUtils
import com.pixplicity.easyprefs.library.Prefs


class AppApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        // ToastUtils.init
        ToastUtils.init(this);
        ToastUtils.setGravity(Gravity.BOTTOM,0,80)

        // Initialize the Prefs class
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

    }
}
