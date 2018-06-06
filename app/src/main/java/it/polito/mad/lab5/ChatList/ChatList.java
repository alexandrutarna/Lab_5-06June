package it.polito.mad.lab5.ChatList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import it.polito.mad.lab5.MyBooks;
import it.polito.mad.lab5.Notify;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.Request.ReceivedRequest.ReceivedRequest;
import it.polito.mad.lab5.SearchBook.SearchBook;
import it.polito.mad.lab5.Request.SentRequest.SentRequest;
import it.polito.mad.lab5.ShowProfile;
import it.polito.mad.lab5.SignInActivity;

public class ChatList extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private boolean menu_is_present = false;
    private String uID;

    //BAMBA
    private DrawerLayout mDrawerLayout;
    private ImageView imageView;
    private TextView textView;
    //BAMBA


    private TextView topText;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);


        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        String textTopString = res.getString(R.string.chatList);
        topText.setText(textTopString);

        //BAMBA
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        imageView = header.findViewById(R.id.nav_header_imageView);
        textView = header.findViewById(R.id.nav_header_textView);
        if(Notify.getImageBitmap() != null){
            imageView.setImageBitmap(Notify.getImageBitmap());
        }
        if(Notify.getMail() != null){
            textView.setText(Notify.getMail());
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        switch (menuItem.getItemId()) {
                            case R.id.nav_search:
                                Intent searchIntent = new Intent(getApplicationContext(), SearchBook.class);
                                startActivity(searchIntent);
                                break;
                            case R.id.nav_book:
                                Intent myBooksIntent = new Intent(getApplicationContext(), MyBooks.class);
                                startActivity(myBooksIntent);
                                break;
                            case R.id.nav_profile:
                                Intent showProfileIntent = new Intent(getApplicationContext(), ShowProfile.class);
                                startActivity(showProfileIntent);
                                break;
                            case R.id.nav_chat:
                                break;
                            case R.id.nav_received_request:
                                Intent receivedIntent = new Intent(getApplicationContext(), ReceivedRequest.class);
                                startActivity(receivedIntent);
                                break;
                            case R.id.nav_sent_request:
                                Intent sentIntent = new Intent(getApplicationContext(), SentRequest.class);
                                startActivity(sentIntent);
                                break;
                            case R.id.nav_logout:
                                SharedPreferences sharedPref = getSharedPreferences("shared_id", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("uID",null);
                                editor.putString("geo",null);
                                editor.apply();
                                System.out.println("Logged out-> uID "+ sharedPref.getString("uID",null));
                                new AlertDialog.Builder(ChatList.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Bye bye :)")
                                        .setMessage("Are you sure you want to logout?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                                                signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(signInIntent);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                //Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                                //signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //startActivity(signInIntent);
                                break;
                        }

                        return true;
                    }
                });
        //BAMBA

        SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        uID = sharedPref.getString("uID", null);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(this);

        final ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        menu_is_present = false;
    }

    public void onBackPressed() {
        if (menu_is_present) {
            menu_is_present = false;
            mDrawerLayout.closeDrawers();
        } else finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuButton:
                if (menu_is_present) {
                    menu_is_present = false;
                    mDrawerLayout.closeDrawers();

                } else {
                    menu_is_present = true;
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        String textTopString = res.getString(R.string.connecting);
        topText.setText(textTopString);

        if (dataSnapshot.exists()) {
            System.out.println("snapshot exists");

            ArrayList<ChatItem> list = new ArrayList<ChatItem>();

            // dataSnapshot is the "issue" node with all children with id 0
            for (DataSnapshot issue : dataSnapshot.child("users").child(uID).child("chats").getChildren()) {
                String chatID = issue.getValue(String.class);
                System.out.println("item found: " + chatID);

                String copyID  = dataSnapshot.child("chats").child(chatID).child("copyID").getValue(String.class);
                String copyTitle = dataSnapshot.child("copies").child(copyID).child("title").getValue(String.class);

                String ownerID = dataSnapshot.child("chats").child(chatID).child("ownerID").getValue(String.class);
                String otherID = dataSnapshot.child("chats").child(chatID).child("otherID").getValue(String.class);
                String imageUrl;// other;

                if (ownerID == uID) {
                    imageUrl = dataSnapshot.child("users").child(otherID).child("imageUrl").getValue(String.class);
                    //other =  dataSnapshot.child("users").child(usr2).child("name").getValue(String.class);
                }
                    else {
                    imageUrl = dataSnapshot.child("users").child(ownerID).child("imageUrl").getValue(String.class);
                    //other =  dataSnapshot.child("users").child(usr1).child("name").getValue(String.class);
                }
                Bitmap image = null;
                if (imageUrl != null) try {
                        image = decodeFromFirebaseBase64(imageUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                //String ownerID = dataSnapshot.child("copies").child(copyID).child("userID").getValue(String.class);

                list.add(new ChatItem(copyTitle,chatID,image));
                }
            ChatListAdapter adapter = new ChatListAdapter(getApplicationContext(), list);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(adapter);
            }

        textTopString = res.getString(R.string.chatList);
        topText.setText(textTopString);


        }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
