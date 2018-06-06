package it.polito.mad.lab5.SearchBook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import it.polito.mad.lab5.ChatList.ChatList;
import it.polito.mad.lab5.MyBooks;
import it.polito.mad.lab5.NavigationDrawer.NavigationDrawerFragment;
import it.polito.mad.lab5.NewBook.NewBook;
import it.polito.mad.lab5.Notify;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.Request.ReceivedRequest.ReceivedRequest;
import it.polito.mad.lab5.Request.SentRequest.SentRequest;
import it.polito.mad.lab5.ShowProfile;
import it.polito.mad.lab5.SignInActivity;
import android.content.res.Resources;

public class SearchBook extends AppCompatActivity implements View.OnClickListener, ValueEventListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    boolean menu_is_present = false;
    NavigationDrawerFragment fragment = new NavigationDrawerFragment();
    private RadioGroup radio;
    private Spinner genre;

    private TextView topText;
    private Resources res;

    String geo;

    //BAMBA
    private DrawerLayout mDrawerLayout;
    private ImageView imageView;
    private TextView textView;
    //BAMBA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);



        SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        String uID = sharedPref.getString("uID", null);
        try {
            if (uID.isEmpty() == true) goToSignIn();
        } catch (Exception e) {
            goToSignIn();
        }

        geo = sharedPref.getString("geo", null);
        System.out.println("geo: " + geo);

        setContentView(R.layout.activity_search_book);

        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        String textTopString = res.getString(R.string.bookSharing);
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
                                new AlertDialog.Builder(SearchBook.this)
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

        final ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setBackgroundColor(Color.TRANSPARENT);
        searchButton.setOnClickListener(this);

        final ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setOnClickListener(this);


        final ImageButton add_book_button = findViewById(R.id.add_book_button);
        add_book_button.setBackgroundColor(Color.TRANSPARENT);
        add_book_button.setOnClickListener(this);

        radio = (RadioGroup) findViewById(R.id.radio_button);
        radio.check(R.id.radio_title);
        radio.setOnCheckedChangeListener(this);

        genre = findViewById(R.id.genre_spinner);
        genre.setVisibility(View.INVISIBLE);


        Resources res = getResources();
        String [] gen = res.getStringArray(R.array.genre_array);

        List<String> genre_array = new ArrayList<String>();
        for (int i = 0; i < gen.length; i++) genre_array.add(gen[i]);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genre_array) {
            @Override
            public boolean isEnabled(int position){
                if(position == 0) return false;
                else return true;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0) tv.setTextColor(Color.GRAY);
                else tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(dataAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        menu_is_present = false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.searchButton:
                radio = (RadioGroup) findViewById(R.id.radio_button);
                int selected = radio.getCheckedRadioButtonId();

                final ImageButton add_book_button = findViewById(R.id.add_book_button);
                add_book_button.setVisibility(View.INVISIBLE);
                System.out.println("Search Button");
                EditText searchBar = findViewById(R.id.searchBar);
                String toFind = searchBar.getText().toString().toLowerCase();
                System.out.println(toFind);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query;

                switch (selected) {
                    case R.id.radio_title:
                        query = reference.child("copies").orderByChild("title_to_search").startAt(toFind).endAt(toFind+"\uf8ff");
                        query.addListenerForSingleValueEvent(this);
                        break;

                    case R.id.radio_author:
                        query = reference.child("copies").orderByChild("author_to_search").startAt(toFind).endAt(toFind+"\uf8ff");
                        query.addListenerForSingleValueEvent(this);
                        break;

                    case R.id.radio_genre:
                        if (genre.getSelectedItemPosition() != 0) {
                            toFind = genre.getSelectedItem().toString();
                            query = reference.child("copies").orderByChild("genre").equalTo(toFind);
                            query.addListenerForSingleValueEvent(this);
                        } else {
                            Toast.makeText(this, R.string.no_genre_selected, Toast.LENGTH_LONG).show();
                        }
                        break;

                    default:
                        break;
                }

                break;
            case R.id.add_book_button: addBook(); break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_genre) {
            final EditText searchBar = (EditText) findViewById(R.id.searchBar);
            searchBar.setVisibility(View.INVISIBLE);
            genre = findViewById(R.id.genre_spinner);
            genre.setVisibility(View.VISIBLE);
        } else {
            final EditText searchBar = (EditText) findViewById(R.id.searchBar);
            searchBar.setVisibility(View.VISIBLE);
            Spinner genre = findViewById(R.id.genre_spinner);
            genre.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void addBook() {
        Intent intent = new Intent(getApplicationContext(), NewBook.class);
        startActivity(intent);
    }

    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public void goToChatList(){
        Intent intent = new Intent(getApplicationContext(), ChatList.class);
        startActivity(intent);
    }

    public void goToSignIn(){
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Bye bye :)")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        String textTopString = res.getString(R.string.connecting);
        topText.setText(textTopString);

        if (dataSnapshot.exists()) {
            System.out.println("snapshot exists");
            final ArrayList<Book> list = new ArrayList<Book>();
            // dataSnapshot is the "issue" node with all children with id 0
            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                String geoBook = issue.child("geo").getValue(String.class);
                System.out.println("item found: " + geo + " " + geoBook);

                if (geoBook.equals(geo)) {

                    String title = issue.child("title").getValue(String.class);
                    String bookImgUrl = issue.child("imageUrl").getValue(String.class);
                    String copyID = issue.getKey();
                    String ownerID = issue.child("userID").getValue(String.class);

                    if (bookImgUrl != null) {
                        try {
                            Bitmap bookImage = decodeFromFirebaseBase64(bookImgUrl);
                            Book book = new Book(title, bookImage, copyID,ownerID);
                            list.add(book);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else list.add(new Book(title, null, copyID,ownerID));

                    System.out.println("item found: " + title + "\n");
                }
            }

            BookAdapter adapter = new BookAdapter(getApplicationContext(), list,0);
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

        }

        textTopString = res.getString(R.string.bookSharing);
        topText.setText(textTopString);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
