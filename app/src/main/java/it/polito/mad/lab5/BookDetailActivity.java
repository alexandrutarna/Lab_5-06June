package it.polito.mad.lab5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;

import it.polito.mad.lab5.Chat.Chat;
import it.polito.mad.lab5.SearchBook.Book;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener, Serializable, ValueEventListener {

    // for logging ---------------------------------------
    String className = this.getClass().getSimpleName();
    String TAG = "--- " + className + " --- ";
    // ---------------------------------------------------

    private String copyID;
    private String ownerID;
    private String title;



//    private TextView tvPageCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        Log.i(TAG, "Intent received");
        //int position = intent.getIntExtra("position", 0);

        copyID = (String) intent.getStringExtra("copyID");
        ownerID = (String) intent.getStringExtra("ownerID");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(this);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setBackgroundColor(Color.TRANSPARENT);
        backButton.setOnClickListener(this);


        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(this);
        Button askBookButton = findViewById(R.id.askBookButton);
        askBookButton.setOnClickListener(this);

        findViewById(R.id.userImage).setOnClickListener(this);
        findViewById(R.id.username).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backButton: finish(); break;

            case R.id.chatButton: goToChat(); break;

            case R.id.askBookButton: goToGetBook(); break;

            case R.id.userImage:
                Intent showProfileIntent_0 = new Intent(this, ShowProfile.class);
                showProfileIntent_0.putExtra("otherUserID", ownerID);
                startActivity(showProfileIntent_0);
                break;
            case R.id.username:
                Intent showProfileIntent_1 = new Intent(this, ShowProfile.class);
                showProfileIntent_1.putExtra("otherUserID", ownerID);
                startActivity(showProfileIntent_1);
                break;


        }
    }


    private void goToGetBook() {
        Intent intent = new Intent(this,GetBook.class);
        //intent.setClass(this, it.polito.mad.lab5.GetBook.class);
        intent.putExtra("copyID", copyID);
        intent.putExtra("ownerID", ownerID);
        intent.putExtra("title", title);

        startActivity(intent);
    }


    private void goToChat() {

        final SharedPreferences sharedPref = getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        String uID = sharedPref.getString("uID",null);


        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final String chatID = dbRef.child("chats").push().getKey();        // create chat

        dbRef.child("chats").child(chatID).child("copyID").setValue(copyID);        // put copy on database
        dbRef.child("chats").child(chatID).child("ownerID").setValue(ownerID);
        dbRef.child("chats").child(chatID).child("otherID").setValue(uID);

        dbRef.child("users").child(uID).child("chats").push().setValue(chatID);      // save chatID in your profile

        dbRef.child("users").child(ownerID).child("chats").push().setValue(chatID);  // save chatID in book owner's profile


        Intent intent = new Intent();
        intent.setClass(this, Chat.class);
        intent.putExtra("chatID", chatID);

        startActivity(intent);
    }


    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        TextView tvTitle = findViewById(R.id.title);
        TextView tvAuthor = findViewById(R.id.author);
        TextView tvEditionYear = findViewById(R.id.editionYear);
        TextView tvPublisher = findViewById(R.id.publisher);
        TextView tvIsbn = findViewById(R.id.isbn);
        TextView tvGenre = findViewById(R.id.genre);
        TextView tvBookCondition = findViewById(R.id.bookCondition);
        TextView tvUsername = findViewById(R.id.username);



        final String isbn = dataSnapshot.child("copies").child(copyID).child("isbn").getValue(String.class);

        title = dataSnapshot.child("copies").child(copyID).child("title").getValue(String.class);
        tvTitle.setText(title);
        tvAuthor.setText(dataSnapshot.child("copies").child(copyID).child("author").getValue(String.class));

        try {
            if (isbn != null) {
                tvEditionYear.setText(dataSnapshot.child("books").child(isbn).child("editionYear").getValue(String.class));
                tvPublisher.setText(dataSnapshot.child("books").child(isbn).child("publisher").getValue(String.class));
            }
        } catch (Exception e) {}

        tvIsbn.setText(isbn);
        tvGenre.setText(dataSnapshot.child("copies").child(copyID).child("genre").getValue(String.class));
        tvBookCondition.setText(dataSnapshot.child("copies").child(copyID).child("bookCondition").getValue(String.class));


        String bookImgUrl = dataSnapshot.child("copies").child(copyID).child("imageUrl").getValue(String.class);
        if (bookImgUrl != null) {
            try {
                Bitmap bookImage = decodeFromFirebaseBase64(bookImgUrl);
                ImageView img = findViewById(R.id.bookImage);
                img.setImageBitmap(bookImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tvUsername.setText(dataSnapshot.child("users").child(ownerID).child("name").getValue(String.class));

        String userImgUrl = dataSnapshot.child("users").child(ownerID).child("imageUrl").getValue(String.class);
        if (userImgUrl != null) {
            try {
                Bitmap userImageBitmap = decodeFromFirebaseBase64(userImgUrl);
                ImageView userImage = findViewById(R.id.userImage);
                userImage.setImageBitmap(userImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
