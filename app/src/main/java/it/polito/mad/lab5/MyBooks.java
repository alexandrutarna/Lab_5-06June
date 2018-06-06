package it.polito.mad.lab5;

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
import android.view.ViewGroup;
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

import it.polito.mad.lab5.ChatList.ChatList;
import it.polito.mad.lab5.NavigationDrawer.NavigationDrawerFragment;
import it.polito.mad.lab5.NewBook.NewBook;
import it.polito.mad.lab5.Request.ReceivedRequest.ReceivedRequest;
import it.polito.mad.lab5.SearchBook.Book;
import it.polito.mad.lab5.SearchBook.BookAdapter;
import it.polito.mad.lab5.SearchBook.SearchBook;
import it.polito.mad.lab5.Request.SentRequest.SentRequest;

public class MyBooks extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    private boolean menu_is_present = false;
    NavigationDrawerFragment fragment = new NavigationDrawerFragment();

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
        setContentView(R.layout.activity_my_books);

        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        String textTopString = res.getString(R.string.myBooks);
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
                                break;
                            case R.id.nav_profile:
                                Intent showProfileIntent = new Intent(getApplicationContext(), ShowProfile.class);
                                startActivity(showProfileIntent);
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
                                new AlertDialog.Builder(MyBooks.this)
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

        button();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        menu_is_present = false;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        final SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        final String uID = sharedPref.getString("uID", null);

        String textTopString = res.getString(R.string.connecting);
        topText.setText(textTopString);

        final ArrayList<Book> list = new ArrayList<Book>();

        for (DataSnapshot issue : dataSnapshot.child("users").child(uID).child("copies").getChildren()) {
            String key = issue.getValue(String.class);
            String isbn = dataSnapshot.child("copies").child(key).child("isbn").getValue(String.class);
            String title = dataSnapshot.child("books").child(isbn).child("title").getValue(String.class);
            String copyID = issue.getKey();
            System.out.println("titolo "+title+"\n");

            String bookImgUrl = dataSnapshot.child("copies").child(key).child("imageUrl").getValue(String.class);
            if (bookImgUrl != null) {
                try {
                    System.out.println("SONO NEL TRY");
                    Bitmap bookImage = decodeFromFirebaseBase64(bookImgUrl);
                    System.out.println("HO DECODIFICATO L'IMMAGINE");
                    Book book = new Book(title,bookImage, copyID, null);
                    list.add(book);
                    System.out.println("HO AGGIUNTO L'ITEM ALLA LISTA");
                } catch (IOException e) {
                    e.printStackTrace();  System.out.println("SONO NEL CATCH");
                }
            } else list.add(new Book (title,null, copyID, null));

        }
        BookAdapter adapter = new BookAdapter(getApplicationContext(), list,1);
        ListView listView = findViewById(R.id.listView);
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

        textTopString = res.getString(R.string.myBooks);
        topText.setText(textTopString);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    public void newBook(){
        Intent intent = new Intent(getApplicationContext(), NewBook.class);
        startActivity(intent);
    }


    private void button () {
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setOnClickListener(this);

        ImageButton addNewBookButton = findViewById(R.id.newBookButton);
        addNewBookButton.setBackgroundColor(Color.TRANSPARENT);
        addNewBookButton.setOnClickListener(this);
    }

    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // open menu fragment
            case R.id.menuButton:

                if (menu_is_present) {
                    menu_is_present = false;
/*
                    // create a FragmentManager
                    FragmentManager fm = getFragmentManager();
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit(); // save the changes
*/
                     mDrawerLayout.closeDrawers();
                } else {
                    menu_is_present = true;
/*
                    // create a FragmentManager
                    FragmentManager fm = getFragmentManager();
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    fragmentTransaction.replace(R.id.fragmentLayout, fragment);
                    fragmentTransaction.commit(); // save the changes
*/
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.newBookButton:
                newBook();
                break;
        }
    }

    public void onBackPressed() {
        if (menu_is_present) {
            menu_is_present = false;
            mDrawerLayout.closeDrawers();
            } else finish();
    }

}
