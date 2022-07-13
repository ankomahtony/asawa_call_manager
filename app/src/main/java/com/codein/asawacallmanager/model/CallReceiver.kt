package com.codein.asawacallmanager.model

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.view.Gravity
import android.widget.Toast


private var calReceived = false
private var incomingCall = false
private var incomingNum = ""

class CallReceiver: BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.getStringExtra(TelephonyManager.EXTRA_STATE) ==
            TelephonyManager.EXTRA_STATE_OFFHOOK){
            showToastMsg(context!!,"Phone Call Started...")
            calReceived = true
        }
        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) ==
            TelephonyManager.EXTRA_STATE_RINGING){
            showToastMsg(context!!,"Incoming Call...")
            incomingCall = true

            val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephony.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    super.onCallStateChanged(state, incomingNumber)
                    incomingNum = incomingNumber
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) ==
            TelephonyManager.EXTRA_STATE_IDLE){
//            showToastMsg(context!!,"Phone Call Ended...")
            if (incomingCall && !calReceived)
            {
                val SHARED_PREFS = "sharedPrefs"
                val DEFAULT = "default"
                val sharedPreferences =
                    context!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                val message = sharedPreferences.getString(DEFAULT, "")
                showToastMsg(context,"Missed Call: $incomingNum")
                sendSMS(incomingNum,message)
            }
        }

    }
    private fun showToastMsg(c:Context, msg:String){
        val toast = Toast.makeText(c, msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0,0)
        toast.show()
    }

    private fun sendSMS(number: String?, message: String?) {
        val mySmsManager = SmsManager.getDefault()
        mySmsManager.sendTextMessage(number, null, message, null, null)
    }

}