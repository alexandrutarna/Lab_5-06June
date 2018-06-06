package it.polito.mad.lab5.ChatList;

import android.graphics.Bitmap;
/**
 * Created by gianlucaleone on 20/05/18.
 */

class ChatItem {
    String title;
    String chatID;
    Bitmap image;

    public String getTitle() {
        return title;
    }

    public String getChatID() {
        return chatID;
    }

    public Bitmap getImage() {
        return image;
    }

    public ChatItem(String title, String chatID, Bitmap image) {
        this.title = title;
        this.chatID = chatID;
        this.image = image;
    }
}