package it.polito.mad.lab5.Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;


//import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.polito.mad.lab5.ChatList.ChatList;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.SearchBook.Book;
import it.polito.mad.lab5.SearchBook.BookAdapter;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    // for logging ---------------------------------------
    String className = this.getClass().getSimpleName();
    String TAG = "--- " + className + " --- ";
    // ---------------------------------------------------

    private String chatID;

    private DatabaseReference mDatabase;
    private String senderUid;
    private String receiverUid;
    private String senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ===== received from Notification ==================
        Bundle myBundle = getIntent().getExtras();
        String someData = myBundle.getString("someData");
        Log.i(TAG, "data from FCM: "+ someData);
        // ==================================================

        Intent intent = getIntent();
        receiverUid = intent.getStringExtra("otherUid");
        Log.i(TAG, "receiverUid: " + receiverUid);

        final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        senderUid = sharedPref.getString("uID", null);
        Log.i(TAG, "Sender of the messages is: " + senderUid);
        Log.i(TAG, "senderName: " + senderName);
        Log.i(TAG, "receiverUid of the messages is: " + receiverUid);


        FloatingActionButton b = (FloatingActionButton) findViewById(R.id.fab);
        b.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        Intent intent = getIntent();
//        chatID = intent.getStringExtra("chatID");

        /*ScrollView sc = (ScrollView) findViewById(R.id.chat_sc);
        sc.setVerticalScrollbarPosition(sc.getBottom());*/

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*ScrollView sc = (ScrollView) findViewById(R.id.chat_sc);
                sc.setVerticalScrollbarPosition(sc.getBottom());*/

                // initialize the name of the sender
                senderName = dataSnapshot.child("users").child(senderUid).child("name").getValue(String.class);

                String uID = sharedPref.getString("uID", null);
                //String chatID = sharedPref.getString("chatID", null);

//                String ownerID = dataSnapshot.child("chats").child(chatID).child("ownerID").getValue(String.class);
//                String otherID = dataSnapshot.child("chats").child(chatID).child("otherID").getValue(String.class);
//                String checkTime;
//
//                System.out.println("owner "+ownerID);
//                System.out.println("other "+otherID);
//
//                if (ownerID.equals(uID)) {
//                    dbRef.child("chats").child(chatID).child("ownerTime").setValue(ChatMessage.getCurrentTime());
//                    checkTime = dataSnapshot.child("chats").child(chatID).child("otherTime").getValue(String.class);
//                }
//                else {
//                    dbRef.child("chats").child(chatID).child("otherTime").setValue(ChatMessage.getCurrentTime());
//                    checkTime = dataSnapshot.child("chats").child(chatID).child("ownerTime").getValue(String.class);
//                }
//                System.out.println("checktime: "+checkTime);


                ArrayList<ChatMessage> chatMessagesList = new ArrayList<ChatMessage>();

                try {
                for (DataSnapshot issue : dataSnapshot.child("messages").child(senderUid).child(receiverUid).getChildren()) {

//                    String msgtxt = issue.child("messageText").getValue(String.class);
//                    String msgt = issue.child("messageTime").getValue(String.class);
//                    String msgusr = issue.child("messageUser").getValue(String.class);
//
//                    boolean seen = false;
//                    if (checkTime != null) if (checkTime.compareTo(msgt) > 0) seen = true;
//                                                else seen = false;
//
//                    System.out.println("seen: "+ seen);
//                    chatMessagesList.add(new ChatMessage(msgtxt,msgusr,uID,msgt,seen));


                    // retrieve data in order to create a message to add it to messages list
                    String body = issue.child("body").getValue(String.class);
                    Long dayTimestamp = issue.child("dayTimestamp").getValue(Long.class);
                    String from = issue.child("from").getValue(String.class);
                    Long negatedTimestamp = issue.child("negatedTimestamp").getValue(Long.class);
                    Long timestamp = issue.child("timestamp").getValue(Long.class);
                    String to = issue.child("to").getValue(String.class);

                    boolean seen = false;
//                    if (checkTime != null) if (checkTime.compareTo(msgt) > 0) seen = true;
//                    else seen = false;

                    seen = true;
                    Log.i(TAG, "seen: "+ seen);

                    it.polito.mad.lab5.beans.Message msg = new it.polito.mad.lab5.beans.Message(
                            timestamp,
                            negatedTimestamp,
                            dayTimestamp,
                            body,
                            from,
                            to
                    );

                    chatMessagesList.add(new ChatMessage(msg,senderUid,seen));
                }
                } catch(NullPointerException e){
                    Log.i(TAG, "There are no messages in the DB yet or a refference problem");
                    e.printStackTrace();
                }

                ChatAdapter adapter = new ChatAdapter(getApplicationContext(), chatMessagesList);
                ListView listView = findViewById(R.id.list_of_messages);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                EditText input = (EditText)findViewById(R.id.input);

                Log.i(TAG, "send btn: msg " + input.getText().toString());

                Log.i(TAG, "send btn: Sender of the messages is: " + senderUid);
                Log.i(TAG, "send btn: receiverUid of the messages is: " + receiverUid);

                // create a message and push it to the DB
                createMessage(input.getText().toString(), senderUid, receiverUid);


//                final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
//
//                final String uID = sharedPref.getString("uID", null);
//
//                System.out.println("msg "+input.getText().toString());
//
//                // Read the input field and push a new instance
//                // of ChatMessage to the Firebase database
//                FirebaseDatabase.getInstance()
//                        .getReference()
//                        .child("chats")
//                        .child(chatID)
//                        .child("messages")
//                        .push()
//                        .setValue(new ChatMessage(input.getText().toString(),uID)
//                        );

                // Clear the input
                input.setText("");
                break;

            default:
                break;
        }
    }

    public void createMessage(String msg, String _ownerUid, String _userUid) {
        long timestamp = new Date().getTime();
        long dayTimestamp = getDayTimestamp(timestamp);

        it.polito.mad.lab5.beans.Message message =
                new it.polito.mad.lab5.beans.Message(timestamp,
                        -timestamp,
                        dayTimestamp,
                        msg,
                        _ownerUid,
                        _userUid,
                        senderName);
        mDatabase
                .child("notifications")
                .child("messages")
                .push()
                .setValue(message);
        Log.i(TAG, "pushed in notifications.messages: " + message);

        mDatabase
                .child("messages")
                .child(_userUid)
                .child(_ownerUid)
                .push()
                .setValue(message);
        Log.i(TAG, "pushed messages in userUid: " + message);

        if (!_userUid.equals(_ownerUid)) {
            mDatabase
                    .child("messages")
                    .child(_ownerUid)
                    .child(_userUid)
                    .push()
                    .setValue(message);
            Log.i(TAG, "pushed messages in ownerUid: " + message);
        }
    }


    private long getDayTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    public void onBackPressed() {
        goToChatList();
    }

    public void goToChatList() {
        Intent intent = new Intent(getApplicationContext(), ChatList.class);
        startActivity(intent);
    }


}
