package com.qoli.smssender.activity.main


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.qoli.smssender.R
import com.qoli.smssender.activity.SettingsActivity
import com.qoli.smssender.activity.newJobs.NewJobBaseMode
import com.qoli.smssender.activity.newJobs.NewJobCSVmode
import com.qoli.smssender.activity.newJobs.NewJobNotionMode
import com.qoli.smssender.databinding.ActivityMainBinding
import com.qoli.smssender.entity.JobsHelper
import com.qoli.smssender.module.SmsTools


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XXPermissions.setScopedStorage(true)

        // init
        binding = ActivityMainBinding.inflate(layoutInflater)

        // view
        setContentView(binding.root)
        setupView()
        setupAllJobsView()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }


    // Views
    private fun setupAllJobsView() {
        binding.AllJobsView.adapter = MainJobsRecyclerAdapter(emptyList(), this)
        binding.AllJobsView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        JobsHelper(this.applicationContext).listAll { data ->
            try {
                runOnUiThread {
                    (binding.AllJobsView.adapter as? MainJobsRecyclerAdapter)?.updateData(data)
                    binding.AllJobsView.adapter?.notifyDataSetChanged()

                    if (data.isNotEmpty()) {
                        binding.AllJobsViewText.visibility = View.GONE
                    } else {
                        binding.AllJobsViewText.visibility = View.VISIBLE
                    }

                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }


    private fun setupView() {

        checkPermissions()

        binding.AppBarLayout.topAppBar.title = getString(R.string.app_name)
        binding.AppBarLayout.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setting -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.NewJob.setOnClickListener {
            newJobDialog()
        }
    }

    private fun newJobDialog() {

        val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            cornerRadius(16f)
            customView(R.layout.sheet_new_jobs, scrollable = true, dialogWrapContent = true)
            lifecycleOwner(this@MainActivity)
        }

        val customView = dialog.getCustomView()

        customView.findViewById<Button>(R.id.buttonBaseMode).setOnClickListener {
            startActivity(Intent(this, NewJobBaseMode::class.java))
        }

        customView.findViewById<Button>(R.id.buttonCSVMode).setOnClickListener {
            startActivity(Intent(this, NewJobCSVmode::class.java))
        }

        customView.findViewById<Button>(R.id.buttonNotionMode).setOnClickListener {
            startActivity(Intent(this, NewJobNotionMode::class.java))
        }
    }


    private fun checkPermissions() {
        XXPermissions.with(this)
            .permission(Permission.SEND_SMS)
            .permission(Permission.READ_SMS)
            .permission(Permission.RECEIVE_SMS)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
//            .permission(Permission.WRITE_SETTINGS)
            .request { permissions, all ->
                Log.d("Logs", permissions.toString())

                if (all) {
                    reloadUI()
                    binding.PermissionsText.text = "All is OK"
                } else {
                    binding.PermissionsText.text = "Permissions not enough"
                }
            }
    }

    //
    private fun reloadUI() {
        binding.DefaultApp.text = SmsTools(this).getDefaultApp()
    }


}


