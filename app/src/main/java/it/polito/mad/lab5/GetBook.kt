package it.polito.mad.lab5

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_get_book.*
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polito.mad.lab5.Chat.ChatMessage
import it.polito.mad.lab5.SearchBook.SearchBook
import kotlinx.android.synthetic.main.top_back.*
import java.util.*

class GetBook : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private var topText: TextView? = null
    private var res: Resources? = null

    var copyID  : String = ""
    var ownerID : String = ""
    var otherID : String = ""
    var bookTitle : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_book)

        topText = findViewById(R.id.topText)

        val intent = intent
        copyID = intent.getStringExtra("copyID") as String
        ownerID = intent.getStringExtra("ownerID") as String
        bookTitle = intent.getStringExtra("title") as String

        topText!!.text = bookTitle

        val sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE)
        otherID = sharedPref.getString("uID", null)

        backButton!!.setOnClickListener(this)
        backButton!!.setBackgroundColor(Color.TRANSPARENT)

        getBookButton!!.setOnClickListener(this)

        spinner()

    }


    fun spinner () {

            /********** LOAN DAY **********/
        loanD!!.onItemSelectedListener = this
        //val loanD = findViewById<Spinner>(R.id.loanD)

        val res1 : Resources = resources
        val day : String = res1.getString(R.string.day)
        val day_array = arrayOf(day,"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31");

        val dayAdapter = ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, day_array)


        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loanD.adapter = dayAdapter

        /********** LOAN MONTH **********/
        loanM!!.onItemSelectedListener = this

        val res2 : Resources = resources
        val month : String = res2.getString(R.string.month)
        val month_array = arrayOf(month,"01","02","03","04","05","06","07","08","09","10","11","12")

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, month_array)

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loanM!!.adapter = monthAdapter

        /********** LOAN YEAR **********/
        loanY!!.onItemSelectedListener = this

        val res3 : Resources = resources
        val year : String = res3.getString(R.string.year)

        val year_array = arrayOf(year,"2018","2019","2020","2021","2022","2023","2024","2025")
        val yearAdapter = ArrayAdapter (this, android.R.layout.simple_spinner_item, year_array)

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loanY!!.adapter = yearAdapter

        /********** RETURN DAY **********/
        returnD!!.onItemSelectedListener = this
        returnD!!.adapter = dayAdapter

        /********** RETURN MONTH **********/
        returnM!!.onItemSelectedListener = this
        returnM!!.adapter = monthAdapter

        /********** RETURN YEAR **********/
        returnY!!.onItemSelectedListener = this
        returnY!!.adapter = yearAdapter

    }

    override fun onClick(v: View?) {
        pushRequest()
        val intent = Intent (this,SearchBook::class.java);
        startActivity(intent);
    }

    fun pushRequest() {

        loanD!!.selectedItem

        val REQref : DatabaseReference = FirebaseDatabase.getInstance()
                .reference
                .child("request")

        val key = REQref.push().key.toString()

        REQref.child(key).child("dateLoan").setValue(loanD!!.selectedItem.toString() + "/" + loanM!!.selectedItem.toString() + "/" + loanY!!.selectedItem.toString())
        REQref.child(key).child("dateReturn").setValue(returnD!!.selectedItem.toString() + "/" + returnM!!.selectedItem.toString() + "/" + returnY!!.selectedItem.toString())
        REQref.child(key).child("ownerID").setValue(ownerID)
        REQref.child(key).child("loanerID").setValue(otherID)
        REQref.child(key).child("copyID").setValue(copyID)
        REQref.child(key).child("status").setValue("pending")


        val CHATref : DatabaseReference = FirebaseDatabase.getInstance()
                .reference
                .child("chats")

        val chatID = CHATref.push().key.toString()
        CHATref.child(chatID).child("copyID").setValue(copyID)
        CHATref.child(chatID).child("ownerID").setValue(ownerID)
        CHATref.child(chatID).child("otherID").setValue(otherID)
        try {
            if (msg!!.text != null) {
                FirebaseDatabase.getInstance()
                        .reference
                        .child("chats")
                        .child(chatID)
                        .child("messages")
                        .push()
                        .setValue( ChatMessage(msg.getText().toString(), otherID) )

                FirebaseDatabase.getInstance()
                        .reference
                        .child("request")
                        .child(key)
                        .child("msg")
                        .setValue(msg.getText().toString())

            }

        } catch (e : Exception) {}
        REQref.child(key).child("chatID").setValue(chatID)

        val Uref : DatabaseReference = FirebaseDatabase.getInstance()
                .reference
                .child("users")
        Uref.child(ownerID).child("chats").push().setValue(chatID)
        Uref.child(ownerID).child("receivedRequest").child(key).setValue(key)

        Uref.child(otherID).child("chats").push().setValue(chatID)
        Uref.child(otherID).child("sentRequest").child(key).setValue(key)
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                // use position to know the selected item
            }

    override fun onNothingSelected(arg0: AdapterView<*>) {

            }

}
