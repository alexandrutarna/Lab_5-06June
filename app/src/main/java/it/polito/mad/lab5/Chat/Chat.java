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

import it.polito.mad.lab5.R;
import it.polito.mad.lab5.SearchBook.Book;
import it.polito.mad.lab5.SearchBook.BookAdapter;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton b = (FloatingActionButton) findViewById(R.id.fab);
        b.setOnClickListener(this);

        Intent intent = getIntent();
        chatID = intent.getStringExtra("chatID");

        /*ScrollView sc = (ScrollView) findViewById(R.id.chat_sc);
        sc.setVerticalScrollbarPosition(sc.getBottom());*/

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*ScrollView sc = (ScrollView) findViewById(R.id.chat_sc);
                sc.setVerticalScrollbarPosition(sc.getBottom());*/

                String uID = sharedPref.getString("uID", null);
                //String chatID = sharedPref.getString("chatID", null);

                String ownerID = dataSnapshot.child("chats").child(chatID).child("ownerID").getValue(String.class);
                String otherID = dataSnapshot.child("chats").child(chatID).child("otherID").getValue(String.class);
                String checkTime;

                System.out.println("owner "+ownerID);
                System.out.println("other "+otherID);

                if (ownerID.equals(uID)) {
                    dbRef.child("chats").child(chatID).child("ownerTime").setValue(ChatMessage.getCurrentTime());
                    checkTime = dataSnapshot.child("chats").child(chatID).child("otherTime").getValue(String.class);
                }
                else {
                    dbRef.child("chats").child(chatID).child("otherTime").setValue(ChatMessage.getCurrentTime());
                    checkTime = dataSnapshot.child("chats").child(chatID).child("ownerTime").getValue(String.class);
                }
                System.out.println("checktime: "+checkTime);


                ArrayList<ChatMessage> chatMessagesList = new ArrayList<ChatMessage>();

                for (DataSnapshot issue : dataSnapshot.child("chats").child(chatID).child("messages").getChildren()) {

                    String msgtxt = issue.child("messageText").getValue(String.class);
                    String msgt = issue.child("messageTime").getValue(String.class);
                    String msgusr = issue.child("messageUser").getValue(String.class);

                    boolean seen = false;
                    if (checkTime != null) if (checkTime.compareTo(msgt) > 0) seen = true;
                                                else seen = false;

                    System.out.println("seen: "+ seen);
                    chatMessagesList.add(new ChatMessage(msgtxt,msgusr,uID,msgt,seen));

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
                final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);

                final String uID = sharedPref.getString("uID", null);

                System.out.println("msg "+input.getText().toString());

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chats")
                        .child(chatID)
                        .child("messages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),uID)
                        );

                // Clear the input
                input.setText("");
                break;

            default:
                break;
        }
    }


}
