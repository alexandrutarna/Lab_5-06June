package it.polito.mad.lab5;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import it.polito.mad.lab5.ChatList.ChatList;
import it.polito.mad.lab5.Feedback.Feedback;
import it.polito.mad.lab5.Feedback.FeedbackAdapter;
import it.polito.mad.lab5.Request.ReceivedRequest.ReceivedRequest;
import it.polito.mad.lab5.SearchBook.Book;
import it.polito.mad.lab5.SearchBook.SearchBook;
import it.polito.mad.lab5.Request.SentRequest.SentRequest;


public class ShowProfile extends AppCompatActivity  implements View.OnClickListener, ValueEventListener {

    //private DatabaseReference db;
    Bitmap imageBitmap;
    String imageUrl;
    String uID;
    RatingFragment fragment = new RatingFragment();

    public boolean menu_is_present = false;
    private boolean isYourProfile = true;
    private boolean canRate = false;
    private boolean is_rating_frag_open = false;

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
        setContentView(R.layout.activity_show_profile);

        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        //String textTopString = res.getString(R.string.showProfile);
        //topText.setText(textTopString);

        Intent inIntent = getIntent();
        try {
            String otherUserID = inIntent.getStringExtra("otherUserID");
            if (otherUserID != null) {
                isYourProfile = false;
                uID = otherUserID;
            }
        } catch (Exception e) {
            System.out.println("No other ID found");
        }

        button();

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
                                if (!isYourProfile) {
                                    Intent profileIntent = new Intent(getApplicationContext(), ShowProfile.class);
                                    startActivity(profileIntent);
                                }
                                break;
                            case R.id.nav_chat:
                                Intent chatIntent = new Intent(getApplicationContext(), ChatList.class);
                                startActivity(chatIntent);
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
                                new AlertDialog.Builder(ShowProfile.this)
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

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();//child("users").child(uID);
        dbRef.addListenerForSingleValueEvent(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (menu_is_present) {
            menu_is_present = false;
            mDrawerLayout.closeDrawers();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void button () {
        findViewById(R.id.rate_button).setBackgroundColor(Color.TRANSPARENT);
        final ImageButton editButton = findViewById(R.id.editButton);
        if (isYourProfile) {
            editButton.setBackgroundColor(Color.TRANSPARENT);
            editButton.setOnClickListener(this);
        } else {
            editButton.setVisibility(View.INVISIBLE);

            SharedPreferences shPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
            String myUserID = shPref.getString("uID", null);

            DatabaseReference canRateRef = FirebaseDatabase.getInstance().getReference().child("users").child(myUserID).child("canRate");
            canRateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String textTopString = res.getString(R.string.connecting);
                    topText.setText(textTopString);

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String ratableID = issue.getValue(String.class);
                        if ( ratableID.equals(uID) ) { canRate = true; break; }
                    }
                    if (canRate) {
                        final Button rateButton = (Button) findViewById(R.id.rate_button);
                        rateButton.setVisibility(View.VISIBLE);
                        rateButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                // create a FragmentManager
                                FragmentManager fm = getFragmentManager();
                                // create a FragmentTransaction to begin the transaction and replace the Fragment
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                // replace the FrameLayout with new Fragment
                                fragmentTransaction.replace(R.id.rating_fragment, fragment);
                                fragmentTransaction.commit(); // save the changes

                                is_rating_frag_open = true;

                            }

                        });
                    }

                    topText.setText(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

        final ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // open menu fragment
            case R.id.menuButton:

                if (menu_is_present) {
                    menu_is_present = false;
                    mDrawerLayout.closeDrawers();
                } else {
                    menu_is_present = true;
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.editButton: editProfile(); break;
        }
    }


    public void editProfile(){
        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
        startActivity(intent);
    }


    public void signIn(){
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }


    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


    public void onBackPressed() {
        if (menu_is_present) {
            menu_is_present = false;
            mDrawerLayout.closeDrawers();
        } else if (is_rating_frag_open) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
            is_rating_frag_open = false;
        } else finish();
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);

        String textTopString = res.getString(R.string.connecting);
        topText.setText(textTopString);

        if (isYourProfile) {
            uID = sharedPref.getString("uID", null);
        }

        String mail = null;
        String geo = null;

        final String name = dataSnapshot.child("users").child(uID).child("name").getValue(String.class);
        if (isYourProfile) mail = dataSnapshot.child("users").child(uID).child("mail").getValue(String.class);
        final String bio = dataSnapshot.child("users").child(uID).child("bio").getValue(String.class);
        if (isYourProfile) geo = dataSnapshot.child("users").child(uID).child("geo").getValue(String.class);
        imageUrl = dataSnapshot.child("users").child(uID).child("imageUrl").getValue(String.class);

        if (isYourProfile) {
            findViewById(R.id.geo_icon).setVisibility(View.VISIBLE);
            findViewById(R.id.mail_icon).setVisibility(View.VISIBLE);
        }

        TextView show_name = findViewById(R.id.name);
        TextView show_email = findViewById(R.id.mail);
        TextView show_bio = findViewById(R.id.bio);
        TextView show_geo = findViewById(R.id.geo);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        show_name.setText(name);

        if (isYourProfile) show_email.setText(mail);
        else {
            show_email.setVisibility(View.INVISIBLE);
            //ratingBar.setVisibility(View.VISIBLE);
        }

        show_bio.setText(bio);

        if (isYourProfile) {
            show_geo.setText(geo);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("geo", geo); editor.apply();
            System.out.println("geo " + geo);
        } else show_geo.setVisibility(View.INVISIBLE);


        if (imageUrl != null) {
            try {
                imageBitmap = decodeFromFirebaseBase64(imageUrl);

                ImageView imageViewRound = (ImageView) findViewById(R.id.img);


                imageViewRound.setImageBitmap(imageBitmap);
//                final ImageView img = findViewById(R.id.img);
//                img.setImageBitmap(imageBitmap);
                //System.out.println("la sto prendendo dal database");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final ArrayList<Feedback> list = new ArrayList<Feedback>();

        if (dataSnapshot.exists()) {
            float rate_sum = (float) 0.0;

            for (DataSnapshot issue : dataSnapshot.child("users").child(uID).child("feedbacks").getChildren()) {
                final String userID = issue.child("id").getValue(String.class);
                final String comment = issue.child("comment").getValue(String.class);
                final String rate_string = issue.child("rate").getValue(String.class);
                final Float rate = Float.parseFloat(rate_string);

                rate_sum+=rate;

                final String username = dataSnapshot.child("users").child(userID).child("name").getValue(String.class);
                list.add( new Feedback(userID, username, rate, comment) );
            }

            FeedbackAdapter adapter = new FeedbackAdapter(this, list);
            ListView listView = findViewById(R.id.feedback_list);
            listView.setAdapter(adapter);

            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();

            RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
            if (adapter.getCount() > 0) rb.setRating(rate_sum/adapter.getCount());
                else rb.setRating((float) 0.0);
        }

        //textTopString = res.getString(R.string.showProfile);
        topText.setText(null);
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public String getRatebleID () {
        return uID;
    }


    public void closeFragment () {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        is_rating_frag_open = false;

        findViewById(R.id.rate_button).setVisibility(View.INVISIBLE);
    }




}


