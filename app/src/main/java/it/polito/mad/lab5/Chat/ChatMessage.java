package it.polito.mad.lab5.Chat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nicolapiano on 15/05/18.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageTime;
    private String uID;
    private Boolean seen;

    public ChatMessage (String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.messageTime = formatter.format(new Date().getTime());
    }

    public ChatMessage (String messageText, String messageUser, String uID, Boolean seen) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.uID = uID;
        this.seen = seen;

        // Initialize to current time
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.messageTime = formatter.format(new Date().getTime());
    }

    public ChatMessage (String messageText, String messageUser, String uID,String messageTime, Boolean seen) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.uID = uID;
        // Initialize to current time
        this.messageTime = messageTime;
        this.seen = seen;
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return this.messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getuID() {
        return uID;
    }

    public static String getCurrentTime(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date().getTime());
    }

    public static long getTime(String date){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = f.parse(date);
            long milliseconds = d.getTime();
            return milliseconds;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Boolean getSeen() {
        return seen;
    }
}
