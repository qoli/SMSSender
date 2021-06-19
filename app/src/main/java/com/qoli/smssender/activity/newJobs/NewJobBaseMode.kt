package com.qoli.smssender.activity.newJobs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.databinding.NewJobBaseModeBinding
import com.qoli.smssender.entity.JobsHelper

class NewJobBaseMode : AppCompatActivity() {

    private lateinit var binding: NewJobBaseModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewJobBaseModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.title = getString(R.string.send_mode_base)
        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.SaveButton.saveButton.setOnClickListener {

            if (binding.ShareForm.JobTitle.text.toString().isNullOrEmpty()) {
                ToastUtils.show("必須存在任務標題")
                return@setOnClickListener
            }

            ToastUtils.show(binding.ShareForm.JobTitle.text.toString())

            JobsHelper(this.applicationContext).newBaseJob(binding.ShareForm.JobTitle.text.toString())

            super.onBackPressed()

        }

    }
}

