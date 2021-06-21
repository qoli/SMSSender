package com.qoli.smssender.activity.newJobs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.databinding.NewJobNotionModeBinding

class NewJobNotionMode : AppCompatActivity() {
    private lateinit var binding: NewJobNotionModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewJobNotionModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.title = getString(R.string.send_mode_notion)
        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            this.finish()
        }

        binding.SaveButton.saveButton.setOnClickListener {
            ToastUtils.show("save")
        }
    }
}
