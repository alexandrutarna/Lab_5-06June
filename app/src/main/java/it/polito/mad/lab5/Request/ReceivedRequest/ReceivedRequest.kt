package it.polito.mad.lab5.Request.ReceivedRequest

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.*
import it.polito.mad.lab5.*
import it.polito.mad.lab5.Chat.Chat
import it.polito.mad.lab5.ChatList.ChatList
import it.polito.mad.lab5.R
import it.polito.mad.lab5.Request.RequestItem
import it.polito.mad.lab5.Request.SentRequest.SentRequest
import it.polito.mad.lab5.SearchBook.SearchBook
import kotlinx.android.synthetic.main.show_top.*
import java.util.ArrayList



class ReceivedRequest : AppCompatActivity(), ValueEventListener {

    private var topText: TextView? = null
    private var res: Resources? = null

    private lateinit var uID : String
    private lateinit var listView: ListView

    private var menuIsPresent : Boolean = false

    private var mDrawerLayout: DrawerLayout? = null
    private var imageView: ImageView? = null
    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request)

        topText = findViewById(R.id.topText)

        res = applicationContext.resources
        val textTopString = res!!.getString(R.string.receivedRequest)
        topText!!.text = textTopString


        menuIsPresent = false

        menuButton!!.setBackgroundColor(Color.TRANSPARENT)
        menuButton!!.setOnClickListener(View.OnClickListener {
            if (menuIsPresent) {
                menuIsPresent = false
                mDrawerLayout!!.closeDrawers()
            } else {
                menuIsPresent = true
                mDrawerLayout!!.openDrawer(GravityCompat.START)
            } })

        val sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE)
        uID = sharedPref.getString("uID", null)

        val ref = FirebaseDatabase.getInstance().reference
        ref.addValueEventListener(this)


        mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)
        imageView = header.findViewById<ImageView>(R.id.nav_header_imageView)
        textView = header.findViewById(R.id.nav_header_textView)
        if (Notify.getImageBitmap() != null) {
            imageView!!.setImageBitmap(Notify.getImageBitmap())
        }
        if (Notify.getMail() != null) {
            textView!!.setText(Notify.getMail())
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout!!.closeDrawers()

            // Add code here to update the UI based on the item selected
            when (menuItem.itemId) {
                R.id.nav_search -> {
                    val searchBookIntent = Intent(applicationContext, SearchBook::class.java)
                    startActivity(searchBookIntent)
                }
                R.id.nav_book -> {
                    val myBooksIntent = Intent(applicationContext, MyBooks::class.java)
                    startActivity(myBooksIntent)
                }
                R.id.nav_profile -> {
                    val showProfileIntent = Intent(applicationContext, ShowProfile::class.java)
                    startActivity(showProfileIntent)
                }
                R.id.nav_chat -> {
                    val chatIntent = Intent(applicationContext, ChatList::class.java)
                    startActivity(chatIntent)
                }
                R.id.nav_received_request -> {}
                R.id.nav_sent_request -> {
                    val sentIntent = Intent(applicationContext, SentRequest::class.java)
                    startActivity(sentIntent)
                }
                R.id.nav_logout -> {
                    val sharedPref = getSharedPreferences("shared_id", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("uID", null)
                    editor.putString("geo", null)
                    editor.apply()
                    println("Logged out-> uID " + sharedPref.getString("uID", null)!!)
                    AlertDialog.Builder(this@ReceivedRequest)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Bye bye :)")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes") { dialog, which ->
                                val signInIntent = Intent(applicationContext, SignInActivity::class.java)
                                signInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(signInIntent)
                            }
                            .setNegativeButton("No", null)
                            .show()
                }
            }
            true
        }


    }

    override fun onResume() {
        super.onResume()
        menuIsPresent = false
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {

        val sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE)
        uID = sharedPref.getString("uID", null)

        var textTopString = res!!.getString(R.string.connecting)
        topText!!.text = textTopString

        if (dataSnapshot.exists()) {
            println("snapshot exists")

            val list = ArrayList<RequestItem>()

            // dataSnapshot is the "issue" node with all children with id 0
           // for (issue in dataSnapshot.child("users").child(uID).child("receivedRequest").children) {
            for (issue in dataSnapshot.child("users").child(uID).child("receivedRequest").children) {
                val requestID = issue.getValue(String::class.java)
                println("item found: " + requestID!!)

                val chatID = dataSnapshot.child("request").child(requestID).child("chatID").value.toString()
                val copyID = dataSnapshot.child("request").child(requestID).child("copyID").value.toString()
                val loanerID = dataSnapshot.child("request").child(requestID).child("loanerID").value.toString()
                val dateLoan = dataSnapshot.child("request").child(requestID).child("dateLoan").value.toString()
                val dateReturn = dataSnapshot.child("request").child(requestID).child("dateReturn").value.toString()
                val msg = dataSnapshot.child("request").child(requestID).child("msg").value.toString()
                val status= dataSnapshot.child("request").child(requestID).child("status").value.toString()

                val loanerUserame = dataSnapshot.child("users").child(loanerID).child("name").value.toString()

                val imageProfileUrl : String? = dataSnapshot.child("users").child(loanerID).child("imageUrl").value as String?
                val imageBookUrl : String? = dataSnapshot.child("copies").child(copyID).child("imageUrl").value as String?

                val title = dataSnapshot.child("copies").child(copyID).child("title").value.toString()


                var userImage: Bitmap? = null
                var bookImage: Bitmap? = null


                if (imageProfileUrl != null) userImage = decodeFromFirebaseBase64(imageProfileUrl)
                if (imageBookUrl != null) bookImage = decodeFromFirebaseBase64(imageBookUrl)

                list.add(RequestItem(loanerUserame, dateLoan, dateReturn, msg, title, requestID, chatID, userImage, bookImage, status, loanerID, uID))

            }
            intent = Intent(this.applicationContext, Chat::class.java);
            val adapter = ReceivedRequestAdapter(this.applicationContext, list, intent)
            listView = findViewById(R.id.list_view)
            listView.adapter = adapter

            var totalHeight = 0
            val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
            for (i in 0 until adapter.count) {
                val listItem = adapter.getView(i, null, listView)
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }

            val params = listView.layoutParams
            params.height = totalHeight + listView.dividerHeight * (adapter.count - 1)
            listView.layoutParams = params
            listView.requestLayout()


        }

        textTopString = res!!.getString(R.string.receivedRequest)
        topText!!.text = textTopString

    }

    override fun onCancelled(databaseError: DatabaseError) {

    }

    @Throws(KotlinNullPointerException::class)
    fun decodeFromFirebaseBase64(imageUrl: String): Bitmap {
        val decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }


    override fun onBackPressed() {
        if (menuIsPresent) {
            menuIsPresent = false
            mDrawerLayout!!.closeDrawers()
        } else
            finish()
    }

}

