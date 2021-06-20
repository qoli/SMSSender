package com.qoli.smssender.activity.main


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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

    private lateinit var smsTools: SmsTools
    private lateinit var binding: ActivityMainBinding
    private var intentSimStateChanged: IntentFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XXPermissions.setScopedStorage(true)

        // init
        binding = ActivityMainBinding.inflate(layoutInflater)
        smsTools = SmsTools(this)

        // Receiver
        intentSimStateChanged = IntentFilter("android.intent.action.SIM_STATE_CHANGED")
        registerReceiver(simStateReceiver, intentSimStateChanged)

        // view
        setContentView(binding.root)
        setupView()
        setupAllJobsView()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
    }

    // BroadcastReceiver
    private val simStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                reloadUI()
            }
        }
    }

    // Views
    private fun setupAllJobsView() {
        JobsHelper(this.applicationContext).listAll { data ->
            binding.AllJobsView.adapter = MainJobsRecyclerAdapter(data)
            binding.AllJobsView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun fetchData() {
        JobsHelper(this.applicationContext).listAll { data ->
            try {
                runOnUiThread {
                    (binding.AllJobsView.adapter as? MainJobsRecyclerAdapter)?.updateData(data)
                    binding.AllJobsView.adapter?.notifyDataSetChanged()
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
            customView(R.layout.new_jobs_bottom_sheet, scrollable = true, dialogWrapContent = true)
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
            .permission(Permission.WRITE_SETTINGS)
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
        binding.DefaultApp.text = smsTools.getDefaultApp()
        binding.SIMCardText.text = smsTools.getSIMCardState()
        binding.PhoneNumber.text = smsTools.getPhoneNumber()
    }


}


