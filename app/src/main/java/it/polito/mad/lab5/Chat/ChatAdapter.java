package it.polito.mad.lab5.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.lab5.R;

/**
 * Created by nicolapiano on 19/05/18.
 */

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public ChatAdapter(Context context, ArrayList<ChatMessage> msg) {
        super(context, 0, msg);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatMessage msg = getItem(position);
        String str = msg.getMessageText() + "\n" + msg.getMessageTime().substring(11,16);
        TextView text = null;

        // Check if an existing view is being reused, otherwise inflate the view

            if (msg.getuID().equals(msg.getMessageUser())) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_right, parent, false);
                text = (TextView) convertView.findViewById(R.id.msg_text_r);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_left, parent, false);
                text = (TextView) convertView.findViewById(R.id.msg_text_l);
                if (msg.getSeen()) {
                    ImageView seen = (ImageView) convertView.findViewById(R.id.seen);
                    seen.setVisibility(View.VISIBLE);
                    seen.setColorFilter(Color.WHITE);
                }
            }

        text.setText(str);


        /*
        if (msg.getuID().equals(msg.getMessageUser())) {
             //System.out.println(msg.getMessageText() + "RIGHT");
             text = (TextView) convertView.findViewById(R.id.message_text_r);
             //time = (TextView) convertView.findViewById(R.id.message_time_r);
             text.setText(msg.getMessageText());
             //time.setText(msg.getMessageTime());
             if (msg.getSeen()) time.setText(msg.getMessageTime() + " seen");
             else time.setText(msg.getMessageTime());
             //text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
             //time.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            System.out.println(msg.getMessageText() + "LEFT");
            text = (TextView) convertView.findViewById(R.id.message_text_l);
            time = (TextView) convertView.findViewById(R.id.message_time_l);
            text.setText(msg.getMessageText());
            time.setText(msg.getMessageTime());
            //text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            //time.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }*/

        return convertView;
    }
}