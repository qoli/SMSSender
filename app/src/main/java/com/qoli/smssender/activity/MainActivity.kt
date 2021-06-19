package com.qoli.smssender.activity


import android.content.*
import android.os.Bundle
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.klinker.android.send_message.*
import com.pixplicity.easyprefs.library.Prefs
import com.qoli.smssender.app.AppConstant
import com.qoli.smssender.app.AppUnits
import com.qoli.smssender.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var intentSimStateChanged: IntentFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XXPermissions.setScopedStorage(true)

        binding = ActivityMainBinding.inflate(layoutInflater)


        // Receiver
        intentSimStateChanged = IntentFilter("android.intent.action.SIM_STATE_CHANGED")
        registerReceiver(simStateReceiver, intentSimStateChanged)


        // view
        setContentView(binding.root)

        // init app
//        checkPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
    }

    private val simStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                reloadUI()
            }
        }
    }



//    private fun initApns() {
//        ApnUtils.initDefaultApns(
//            this
//        ) {
//            settings = com.qoli.smssender.Settings.get(this, true)
//        }
//    }
//
//
//    private fun setupUI() {
//        if (Utils.isDefaultSmsApp(this)) {
//            binding.setAsDefault.visibility = View.GONE
//        }
//
//        binding.setAsDefault.setOnClickListener { setDefaultSmsApp() }
//        binding.apns.setOnClickListener { initApns() }
//
//        binding.messageEdit.setText("Message: ${Date()}")
//        binding.from.setText(Utils.getMyPhoneNumber(this))
//        binding.to.setText(Utils.getMyPhoneNumber(this))
//        binding.send.setOnClickListener { sendLoops() }
//        binding.loopTimes.setText(Prefs.getString(AppConstant.loopTimes, "5"))
//        binding.interval.setText(Prefs.getString(AppConstant.interval, "5000"))
//
//    }
//
//    private fun setDefaultSmsApp() {
//        binding.setAsDefault.visibility = View.GONE
//        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
//        intent.putExtra(
//            Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
//            packageName
//        )
//        startActivity(intent)
//    }


//    private fun checkPermissions() {
//        XXPermissions.with(this)
//            .permission(Permission.SEND_SMS)
//            .permission(Permission.READ_SMS)
//            .permission(Permission.RECEIVE_SMS)
//            .permission(Permission.READ_PHONE_STATE)
//            .permission(Permission.WRITE_EXTERNAL_STORAGE)
//            .permission(Permission.WRITE_SETTINGS)
//            .request { permissions, all ->
//                Log.d("Logs", permissions.toString())
//
//                if (all) {
//                    reloadUI()
//                } else {
//                    binding.helperText.text = "Permissions not enough"
//                }
//            }
//    }
//
    private fun reloadUI() {
        getSIMState()
    }

    private fun getSIMState() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val state = tm.simState
        binding.helperText.text = AppUnits.simStatetoText(state)
    }


}

