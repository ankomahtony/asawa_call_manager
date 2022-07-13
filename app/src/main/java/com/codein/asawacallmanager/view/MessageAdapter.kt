package com.codein. asawacallmanager.view

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.codein.asawacallmanager.PrefConfig
import com.codein.asawacallmanager.R
import com.codein.asawacallmanager.model.MessageData


class MessageAdapter(val c: Context, val messageList:ArrayList<MessageData>, val listener:OnItemClickListener):RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    inner class MessageViewHolder(v: View, vm: View): RecyclerView.ViewHolder(v), View.OnClickListener{

        var message:TextView = v.findViewById(R.id.mTitle)
        var mMenus: ImageView = v.findViewById(R.id.mMenus)
        private var edtMessage: EditText? = vm.findViewById(R.id.enter_message_edit)
//        var your_message: TextView

        init {
            //            your_message = vm.findViewById(R.id.default_message)
            mMenus.setOnClickListener{ popupMenus(it)}
            v.setOnClickListener(this)
        }
        private val SHARED_PREFS = "sharedPrefs"
        private val DEFAULT = "default"


        private fun popupMenus(v:View) {
            messageList[adapterPosition]
            val popupMenus = PopupMenu(c, v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.selectText -> {
                        /**set delete*/
//                        val myNewMessage = message.text.toString()
//                        val sharedPreferences = c.getSharedPreferences(this.SHARED_PREFS,
//                            AppCompatActivity.MODE_PRIVATE
//                        )
//                        val editor = sharedPreferences.edit()
//                        editor.putString(this.DEFAULT, myNewMessage)
//                        editor.apply()
//                        notifyDataSetChanged()

                        listener.onItemClick(adapterPosition)

                        true
                    }
                    R.id.delete -> {
                        /**set delete*/
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this Information")
                            .setPositiveButton("Yes") { dialog, _ ->
                                messageList.removeAt(adapterPosition)
                                PrefConfig.writeListInPref(c, messageList)
                                notifyDataSetChanged()
                                Toast.makeText(c, "Deleted this Information", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else -> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.item_list,parent,false)
        val vm = inflater.inflate(R.layout.activity_main,parent,false)
        return MessageViewHolder(v, vm)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val newList = messageList[position]
        holder.message.text = newList.message

        val SHARED_PREFS = "sharedPrefs"
        val DEFAULT = "default"
        val sharedPreferences =
            c.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val default_message = sharedPreferences.getString(DEFAULT, "")

        if (newList.message == default_message){
            holder.mMenus.setImageResource(R.drawable.ic_check)
        }else{
            holder.mMenus.setImageResource(R.drawable.ic_more)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}