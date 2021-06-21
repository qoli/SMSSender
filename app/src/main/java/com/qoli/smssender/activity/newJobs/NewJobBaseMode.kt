package com.qoli.smssender.activity.newJobs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.app.AppUnits
import com.qoli.smssender.databinding.NewJobBaseModeBinding
import com.qoli.smssender.entity.JobEntity
import com.qoli.smssender.entity.JobsHelper


class NewJobBaseMode : AppCompatActivity() {

    private lateinit var binding: NewJobBaseModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewJobBaseModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.title = getString(R.string.send_mode_base)
        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            this.finish()
        }

        binding.SaveButton.saveButton.setOnClickListener {

            if (binding.ShareForm.JobTitle.text.toString().isNullOrEmpty()) {
                ToastUtils.show("必須存在任務標題")
                return@setOnClickListener
            }


            val newJob =
                JobEntity(
                    0,
                    binding.ShareForm.JobTitle.text.toString(),
                    System.currentTimeMillis().toInt()
                )

            newJob.jobInterval = binding.ShareForm.interval.text.toString().toIntOrNull() ?: 5
            newJob.jobBackNumber = binding.ShareForm.backNumber.text.toString()
            newJob.basePhoneNumbers = binding.PhoneNumbers.text.toString()
            newJob.baseMessage = binding.message.text.toString()

            JobsHelper(this.applicationContext).newJob(newJob) { data ->

                if (data != null) {
                    AppUnits.toJobViewByID(this, data.id.toInt())
                } else {
                    ToastUtils.show("添加任務失敗")
                }
                this.finish()
            }

        }

    }
}

