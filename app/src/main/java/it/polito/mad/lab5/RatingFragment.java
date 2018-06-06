package it.polito.mad.lab5;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by gianlucaleone on 05/06/18.
 */

public class RatingFragment extends Fragment {

    String userID = null;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        view = inflater.inflate(R.layout.rating_fragment, container, false);

        //userID = getArguments().getString("userID");

        final ShowProfile showProfile = (ShowProfile) getActivity();
        userID = showProfile.getRatebleID();


        SharedPreferences shPref = showProfile.getApplicationContext().getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        final String myUserID = shPref.getString("uID", null);

        ImageButton sendButton = view.findViewById(R.id.sendButton);
        sendButton.setBackgroundColor(Color.TRANSPARENT);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .child("feedbacks")
                        .push();

                dbRef.child("id").setValue(myUserID);

                RatingBar rb = view.findViewById(R.id.ratingBar);
                float rate_f = rb.getRating();
                String rate = Float.toString(rate_f);
                dbRef.child("rate").setValue(rate);

                EditText et = view.findViewById(R.id.comment);
                String comment = et.getText().toString();
                dbRef.child("comment").setValue(comment);

                InputMethodManager im = (InputMethodManager) showProfile.getSystemService(ShowProfile.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);

                FirebaseDatabase.getInstance().getReference().child("users").child(myUserID).child("canRate").child(userID).removeValue();

                showProfile.closeFragment();

            }
        });

        return view;
    }
}