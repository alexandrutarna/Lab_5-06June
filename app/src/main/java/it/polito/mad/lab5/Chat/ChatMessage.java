package it.polito.mad.lab5.Chat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.lab5.beans.Message;

/**
 * Created by nicolapiano on 15/05/18.
 */

public class ChatMessage {
    Message message;
    private String uID;
    private Boolean seen;

    public ChatMessage (Message msg) {
        this.message = msg;
    }

    public ChatMessage (Message msg, String uID, Boolean seen) {
        this.message = msg;
        this.uID = uID;
        this.seen = seen;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
