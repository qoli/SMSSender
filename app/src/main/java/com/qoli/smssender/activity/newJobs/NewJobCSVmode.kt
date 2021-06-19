package com.qoli.smssender.activity.newJobs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.databinding.NewJobCsvmodeBinding


class NewJobCSVmode : AppCompatActivity() {
    private lateinit var binding: NewJobCsvmodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewJobCsvmodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.title = getString(R.string.send_mode_csv)
        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.SaveButton.saveButton.setOnClickListener {
            ToastUtils.show("save")
        }
    }
}
