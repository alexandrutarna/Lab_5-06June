package it.polito.mad.lab5.Request.ReceivedRequest

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polito.mad.lab5.R
import it.polito.mad.lab5.Request.RequestItem


class ReceivedRequestAdapter (private val context: Context,
                              private val dataSource: ArrayList<RequestItem>,
                              private var intent : Intent) : BaseAdapter(){

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
        val rowView = inflater.inflate(R.layout.book_request_item, parent, false)

        val username = rowView.findViewById<TextView>(R.id.username)
        val date = rowView.findViewById<TextView>(R.id.request_date)
        val msg = rowView.findViewById<TextView>(R.id.user_request_msg)
        val title = rowView.findViewById<TextView>(R.id.bookTitle)
        val userImage = rowView.findViewById<ImageView>(R.id.userImage)
        val bookImage = rowView.findViewById<ImageView>(R.id.bookImage)

        val accept = rowView.findViewById<Button>(R.id.accept_button)
        val refuse = rowView.findViewById<Button>(R.id.refuse_button)
        val openchat = rowView.findViewById<Button>(R.id.open_chat)
        val delete = rowView.findViewById<Button>(R.id.delete_button)


        val ri = getItem(position) as RequestItem

        val res : Resources = context.resources
        val from = res.getString(R.string.from)
        val to = res.getString(R.string.to)
        val datelocal : String? = from.plus(" ").plus(ri.dateLoan).plus(" " ).plus(to).plus(" ").plus(ri.dateReturn) as String?

        username.text = ri.username
        date.text = datelocal
        msg.text = ri.msg
        title.text = ri.title

        if (ri.userImage != null) userImage.setImageBitmap(ri.userImage)
        if (ri.bookImage != null) bookImage.setImageBitmap(ri.bookImage)

        if ( !ri.status.equals("pending") ) {
            accept.visibility = INVISIBLE
            refuse.visibility = INVISIBLE

            openchat.visibility = VISIBLE
            delete.visibility = VISIBLE
        }

        accept.setOnClickListener(OnClickListener {
            val REQref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("request").child(ri.requestID).child("status")
            REQref.setValue("accepted")

            val USERref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
            USERref.child(ri.loanerID).child("canRate").child(ri.ownerID).setValue(ri.ownerID)
            USERref.child(ri.ownerID).child("canRate").child(ri.loanerID).setValue(ri.loanerID)
        })

        refuse.setOnClickListener(OnClickListener {
            val REQref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("request").child(ri.requestID).child("status")
            REQref.setValue("refused")
        })

        openchat.setOnClickListener(OnClickListener {
            intent.putExtra("chatID", ri.chatID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        })

        delete.setOnClickListener ( OnClickListener {
            val REQref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(ri.ownerID).child("receivedRequest").child(ri.requestID)
            REQref.setValue(null)
        } )

        msg.setOnClickListener(OnClickListener {
            intent.putExtra("chatID", ri.chatID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        })

        return rowView
    }


}