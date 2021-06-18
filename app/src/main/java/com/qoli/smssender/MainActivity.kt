package com.qoli.smssender


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.klinker.android.send_message.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var settings: Settings? = null
    private var setDefaultAppButton: Button? = null
    private var selectApns: Button? = null
    private var fromField: EditText? = null
    private var toField: EditText? = null
    private var messageField: EditText? = null
    private var sendButton: Button? = null
    private var helperText: TextView? = null



    private var intentSimStateChanged: IntentFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentSimStateChanged = IntentFilter("android.intent.action.SIM_STATE_CHANGED")
        registerReceiver(simStateReceiver, intentSimStateChanged)

        XXPermissions.setScopedStorage(true)

        setContentView(R.layout.activity_main)

        initViews()
        setPermissions()

    }

    private val simStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                reloadUI()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
    }

    private fun initSettings() {
        settings = Settings.get(this)
    }

    private fun initApns() {
        ApnUtils.initDefaultApns(
            this
        ) {
            settings = Settings.get(this, true)
        }
    }

    private fun initViews() {
        setDefaultAppButton = findViewById<View>(R.id.set_as_default) as Button
        selectApns = findViewById<View>(R.id.apns) as Button
        fromField = findViewById<View>(R.id.from) as EditText
        toField = findViewById<View>(R.id.to) as EditText
        messageField = findViewById<View>(R.id.message) as EditText
        sendButton = findViewById<View>(R.id.send) as Button
        helperText = findViewById(R.id.helperText)
    }


    private fun setupUI() {
        if (Utils.isDefaultSmsApp(this)) {
            setDefaultAppButton?.visibility = View.GONE
        } else {
            setDefaultAppButton?.setOnClickListener { setDefaultSmsApp() }
        }
        selectApns?.setOnClickListener { initApns() }

        messageField?.setText("Message: ${Date()}")
        fromField?.setText(Utils.getMyPhoneNumber(this))
        toField?.setText(Utils.getMyPhoneNumber(this))
        sendButton?.setOnClickListener { sendMessage() }


    }

    private fun setDefaultSmsApp() {
        setDefaultAppButton?.visibility = View.GONE
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(
            Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
            packageName
        )
        startActivity(intent)
    }

    private fun sendMessage() {
        Thread {
            try {
                val sendSettings = Settings()
                sendSettings.mmsc = settings?.mmsc
                sendSettings.proxy = settings?.mmsProxy
                sendSettings.port = settings?.mmsPort
                sendSettings.useSystemSending = true
                val transaction = Transaction(this, sendSettings)
                val message = Message(messageField?.text.toString(), toField?.text.toString())

                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID)

                helperText?.text = "Send to ${toField?.text.toString()}"
            } catch (e: Exception) {
                helperText?.text = e.toString()
                Log.w("sendMessage", e.toString())
            }
        }.start()
    }

    private fun setPermissions() {
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
                    initSettings()
                    reloadUI()
                } else {
                    helperText?.text = "Permissions not enough"
                }
            }
    }

    private fun reloadUI() {
        setupUI()
        getSIMState()
    }

    private fun getSIMState() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val state = tm.simState
        helperText?.text = appUnits.simStatetoText(state)
    }


}

