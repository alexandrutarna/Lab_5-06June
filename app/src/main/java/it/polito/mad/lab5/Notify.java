package it.polito.mad.lab5;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;

import it.polito.mad.lab5.Chat.Chat;
import it.polito.mad.lab5.Chat.ChatMessage;

import static it.polito.mad.lab5.MyBooks.decodeFromFirebaseBase64;

public class Notify extends Application {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String CHANNEL_ID="mychannelD";

    //BAMBA
    private static String mail;
    private static Bitmap imageBitmap;
    public static String getMail() {
        return mail;
    }
    public static Bitmap getImageBitmap() {
        return imageBitmap;
    }
    //BAMBA

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // TODO: Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        // TODO: Attach a new AuthListener to detect sign in and out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    SharedPreferences sharedPref = getSharedPreferences("shared_id", Context.MODE_PRIVATE);
                    final String uID = sharedPref.getString("uID", "");

                    // BAMBA
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(uID);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mail = dataSnapshot.child("mail").getValue(String.class);
                            String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                            if (imageUrl != null) {
                                try {
                                    imageBitmap = decodeFromFirebaseBase64(imageUrl);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // BAMBA

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot issue : dataSnapshot.child("users").child(uID).child("chats").getChildren()) {
                                    final String chatID = issue.getValue(String.class);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("chats/" + chatID + "/messages");
                                    myRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            if (dataSnapshot.exists()) {
                                                //String checkTime = ChatMessage.getCurrentTime();
                                                String messageTime = dataSnapshot.child("messageTime").getValue(String.class);
                                                final String messageUser = dataSnapshot.child("messageUser").getValue(String.class);
                                                final String messageText = dataSnapshot.child("messageText").getValue(String.class);
                                                final long msgTime = ChatMessage.getTime(messageTime);
                                                final long now = new Date().getTime();
                                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/" + messageUser + "/name");
                                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        //compare if msg arrive 1 second ago notfy , if more ignore or add a parameter.
                                                        final String name = dataSnapshot.getValue(String.class);
                                                        if (((now - msgTime) <= 5000) && (!messageUser.equals(uID)))
                                                        {
                                                            Notify.this.notify(name,messageText,chatID);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out

                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void notify(String messageUser, String messageText, String chatId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("chatID", chatId);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        // build notification
        // the addAction re-use the same intent to keep the example short
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"mychannelD");

            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.mipmap.icon_book)
                    .setContentTitle("Nuovo messaggio da " + messageUser)
                    .setContentText(messageText)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(messageText))
                    .setContentIntent(pIntent)
                    //.setAutoCancel(true)
                    .setAutoCancel(true);
                    //.build();

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(111, mBuilder.build());
        //}
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "chat";
            String description = "Book sharing";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
