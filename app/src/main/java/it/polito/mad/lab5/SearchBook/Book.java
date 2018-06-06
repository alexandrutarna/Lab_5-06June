package it.polito.mad.lab5.SearchBook;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by gianlucaleone on 06/05/18.
 */

public class Book {
    public String title;
    public Bitmap img;
    public String copyID;
    public String ownerID;
    public String book_image_uri;

    public Book(String title, Bitmap img, String copyID, String ownerID) {
        this.title = title;
        if (img != null) this.img = img;
        this.copyID = copyID;
        this.ownerID = ownerID;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", copyID='" + copyID + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", book_image_uri='" + book_image_uri + '\'' +
                '}';
    }

}