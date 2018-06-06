package it.polito.mad.lab5.ChatList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.polito.mad.lab5.Chat.Chat;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.SearchBook.Book;
import android.widget.ArrayAdapter;


/**
 * Created by gianlucaleone on 20/05/18.
 */

public class ChatListAdapter extends ArrayAdapter<ChatItem> {

    private Context context;

    public ChatListAdapter(Context context, ArrayList<ChatItem> chatItem) {
        super(context, 0, chatItem);
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final ChatItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        Button button = (Button) convertView.findViewById(R.id.button);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.bookTitle);
        ImageView tvImg = (ImageView)  convertView.findViewById(R.id.bookImage);

        // Populate the data into the template view using the data object
        tvTitle.setText(item.title);
        tvTitle.setTextColor(Color.DKGRAY);
        tvImg.setImageBitmap(item.getImage());
        //if (item.img != null) tvImg.setImageBitmap(book.img);

        button.setBackgroundColor(Color.TRANSPARENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(context, Chat.class);
                chatIntent.putExtra("chatID",item.getChatID());
                chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chatIntent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
