package com.codein.asawacallmanager

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codein.asawacallmanager.model.MessageData
import com.codein.asawacallmanager.view.MessageAdapter

class MainActivity : AppCompatActivity(), MessageAdapter.OnItemClickListener{

    private lateinit var recv:RecyclerView
    private lateinit var messageList: ArrayList<MessageData>
    private lateinit var messageAdapter: MessageAdapter
    private var editText: EditText? = null
//    private var editText2: EditText? = null
//    private var your_message: TextView? = null
//    private var your_message2: TextView? = null

    private val SHARED_PREFS = "sharedPrefs"
    private val DEFAULT = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById<EditText>(R.id.enter_message_edit)


//        your_message = findViewById<TextView>(R.id.default_message)

        val saveButton: Button = findViewById(R.id.save_button)
        recv = findViewById(R.id.mMessages)

        if (PrefConfig.readListFromPref(this) != null) {
            messageList = PrefConfig.readListFromPref(this)
        }else{
            messageList = ArrayList()
        }

        messageAdapter = MessageAdapter(this,messageList, this)

        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = messageAdapter
        saveButton.setOnClickListener({ v: View? -> saveData() })

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_NUMBERS,android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.SEND_SMS,android.Manifest.permission.READ_CALL_LOG),1)
        }
        load_data()
        setupHyperlink()

    }

    fun setupHyperlink() {
        val linkTextView = findViewById<TextView>(R.id.privacy_policy_link)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(Color.BLUE)
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Selected", Toast.LENGTH_LONG).show()
        val clickItem = messageList[position]
        val my_new_message = clickItem.message
        val sharedPreferences = getSharedPreferences(this.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(this.DEFAULT, my_new_message)
        editor.apply()
        messageAdapter.notifyItemChanged(position)
        messageAdapter.notifyDataSetChanged()
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
        editText?.text = my_new_message.toEditable()
    }

    private fun saveData() {

        if (messageList.contains(MessageData(editText!!.text.toString()))) {
            System.out.println("message found")
        } else {
            messageList.add(MessageData(editText!!.text.toString()))
        }
        messageAdapter.notifyDataSetChanged()

        PrefConfig.writeListInPref(applicationContext, messageList)

        val sharedPreferences = getSharedPreferences(this.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(this.DEFAULT, editText!!.text.toString())
        editor.apply()
        Toast.makeText(this, "Default saved", Toast.LENGTH_SHORT).show()
        load_data()
    }


    private fun load_data() {
        val sharedPreferences = getSharedPreferences(this.SHARED_PREFS, MODE_PRIVATE)
        val message = sharedPreferences.getString(this.DEFAULT, "")
//        your_message?.text  = message
        editText?.setText(message)
    }

}