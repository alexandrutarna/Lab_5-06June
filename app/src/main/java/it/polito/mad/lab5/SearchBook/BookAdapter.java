package it.polito.mad.lab5.SearchBook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.polito.mad.lab5.BookDetailActivity;
import it.polito.mad.lab5.Chat.Chat;
//import it.polito.mad.lab3.ChatList.ChatList.Chat;
import it.polito.mad.lab5.R;


/**
 * Created by gianlucaleone on 06/05/18.
 */

public class BookAdapter extends ArrayAdapter<Book>  {

    private Context context;
    private int flag;

    public BookAdapter(Context context, ArrayList<Book> books, int flag) {
        super(context, 0, books);
        this.context = context;
        this.flag = flag; //flag searchBook = 0
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Book book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        Button button = (Button) convertView.findViewById(R.id.button);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.bookTitle);
        ImageView tvImg = (ImageView)  convertView.findViewById(R.id.bookImage);

        // Populate the data into the template view using the data object
        tvTitle.setText(book.title);
        tvTitle.setTextColor(Color.DKGRAY);
        if (book.img != null) tvImg.setImageBitmap(book.img);

        button.setBackgroundColor(Color.TRANSPARENT);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (flag) {

                    case 0:

                        Book bookToPass = getItem(position);

                        // add the details of the book for the intent to BookDetailsActivity
                        Intent intent = new Intent();
                        intent.setClass(context, BookDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("copyID", bookToPass.copyID);
                        intent.putExtra("ownerID",bookToPass.ownerID);

                        context.startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}