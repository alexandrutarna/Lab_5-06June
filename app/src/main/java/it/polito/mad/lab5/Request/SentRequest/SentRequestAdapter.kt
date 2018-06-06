package it.polito.mad.lab5.Request.SentRequest

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polito.mad.lab5.R
import it.polito.mad.lab5.Request.RequestItem


class SentRequestAdapter (private val context: Context,
                          private val dataSource: ArrayList<RequestItem>,
                          private var intent : Intent) : BaseAdapter() {

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return dataSource.size
    }


    override fun getItem(position: Int): Any {
        return dataSource[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.sent_request_item, parent, false)

        val username = rowView.findViewById<TextView>(R.id.username)
        val date = rowView.findViewById<TextView>(R.id.request_date)
        val msg = rowView.findViewById<TextView>(R.id.user_request_msg)
        val title = rowView.findViewById<TextView>(R.id.bookTitle)
        val userImage = rowView.findViewById<ImageView>(R.id.userImage)
        val bookImage = rowView.findViewById<ImageView>(R.id.bookImage)

        val openchat_left = rowView.findViewById<Button>(R.id.open_chat_left)
        val openchat_right = rowView.findViewById<Button>(R.id.open_chat_right)
        val delete_cr = rowView.findViewById<Button>(R.id.delete_request_cr)
        val delete_right = rowView.findViewById<Button>(R.id.delete_request_right)

        val status_text = rowView.findViewById<TextView>(R.id.status)

        val ri = getItem(position) as RequestItem

        val res : Resources = context.resources
        val from = res.getString(R.string.from)
        val to = res.getString(R.string.to)
        val datelocal : String? = from.plus(" ").plus(ri.dateLoan).plus(" " ).plus(to).plus(" ").plus(ri.dateReturn) as String?

        username.text = ri.username
        date.text = datelocal
        if (ri.msg != null) msg.text = ri.msg
        title.text = ri.title

        if (ri.userImage != null) userImage.setImageBitmap(ri.userImage)
        if (ri.bookImage != null) bookImage.setImageBitmap(ri.bookImage)

        if ( ri.status.equals("accepted") ) {
            openchat_left.visibility = INVISIBLE
            openchat_right.visibility = VISIBLE
            delete_cr.visibility = INVISIBLE
            delete_right.visibility = VISIBLE

            status_text.visibility = VISIBLE
            status_text.text = res.getString(R.string.accepted)
            status_text.setTextColor(Color.parseColor("#008000"))

        } else if (ri.status.equals("refused")) {

            openchat_left.visibility = INVISIBLE
            openchat_right.visibility = VISIBLE
            delete_cr.visibility = INVISIBLE
            delete_right.visibility = VISIBLE

            status_text.visibility = VISIBLE
            status_text.text = res.getString(R.string.refused)
            status_text.setTextColor(Color.parseColor("#FF0000"))

        }


        openchat_left.setOnClickListener(OnClickListener {
            intent.putExtra("chatID", ri.chatID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        })


        openchat_right.setOnClickListener(OnClickListener {
            intent.putExtra("chatID", ri.chatID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        })


        delete_cr.setOnClickListener(OnClickListener {
            val REQref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("request").child(ri.requestID)
            REQref.removeValue()

            val USERref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
            USERref.child(ri.loanerID).child("sentRequest").child(ri.requestID).removeValue()
            USERref.child(ri.ownerID).child("receivedRequest").child(ri.requestID).removeValue()

        })

        delete_right.setOnClickListener(OnClickListener {
            val REQref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("request").child(ri.requestID)
            REQref.removeValue()

            val USERref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
            USERref.child(ri.loanerID).child("sentRequest").child(ri.requestID).removeValue()
            USERref.child(ri.ownerID).child("receivedRequest").child(ri.requestID).removeValue()

        })

        return rowView
    }


}