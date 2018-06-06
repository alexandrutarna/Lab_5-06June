package it.polito.mad.lab5.NavigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Collections;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import it.polito.mad.lab5.MyBooks;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.SearchBook.SearchBook;
import it.polito.mad.lab5.ShowProfile;
import it.polito.mad.lab5.SignInActivity;

/**
 * Created by gianlucaleone on 11/05/18.
 */

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

    public List<NavigationDrawerItem> dataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter  (Context context, List<NavigationDrawerItem> data){
        super(context, 0, data);

        this.dataList = data;
        this.context=context;

        this.inflater = LayoutInflater.from(context);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NavigationDrawerItem item = dataList.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nav_draw_item, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.bookTitle);
        ImageView tvImg = (ImageView)  convertView.findViewById(R.id.bookImage);
        Button tvButton = (Button) convertView.findViewById(R.id.aaaaaaaa);

        // Populate the data into the template view using the data object
        tvTitle.setText(item.title);
        tvImg.setImageResource(item.imgID);
        tvButton.setBackgroundColor(Color.TRANSPARENT);
        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.aaaaaaaa:
                        switch (position) {
                            case 0:
                                Intent searchBookIntent = new Intent(context, SearchBook.class);
                                context.startActivity(searchBookIntent);
                                break;
                            case 1:
                                Intent myBooksIntent = new Intent(context, MyBooks.class);
                                context.startActivity(myBooksIntent);
                                break;
                            case 2:
                                Intent showProfileIntent = new Intent(context, ShowProfile.class);
                                context.startActivity(showProfileIntent);
                                break;
                            case 3:
                                break;
                            case 4:
                                SharedPreferences sharedPref = context.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
                                SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences
                                editor.putString("uID",null);
                                editor.putString("geo",null);
                                editor.apply();
                                System.out.println("Logged out-> uID "+ sharedPref.getString("uID",null));
                                Intent signInIntent = new Intent(context, SignInActivity.class);
                                signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(signInIntent);
                                break;
                            default:
                                break;
                        }


                        break;
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


}
