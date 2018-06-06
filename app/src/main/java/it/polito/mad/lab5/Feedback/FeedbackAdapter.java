package it.polito.mad.lab5.Feedback;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.lab5.R;
import it.polito.mad.lab5.ShowProfile;

/**
 * Created by gianlucaleone on 05/06/18.
 */

public class FeedbackAdapter extends ArrayAdapter<Feedback> {

    private Context context;
    private int flag;

    public FeedbackAdapter(Context context, ArrayList<Feedback> feedbacks) {
        super(context, 0, feedbacks);
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Feedback feedback = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feedback_item, parent, false);
        }
        // Lookup view for data population
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView feedback_msg = (TextView)  convertView.findViewById(R.id.feedback_msg);

        ratingBar.setRating(feedback.getRate());
        username.setText(feedback.getUsername());
        feedback_msg.setText(feedback.getComment());


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        // add the details of the book for the intent to BookDetailsActivity
                        Intent intent = new Intent();
                        intent.setClass(context, ShowProfile.class);
                        intent.putExtra("otherUserID", feedback.getUserID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
