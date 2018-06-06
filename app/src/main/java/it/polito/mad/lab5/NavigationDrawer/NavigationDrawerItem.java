package it.polito.mad.lab5.NavigationDrawer;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.lab5.R;

/**
 * Created by gianlucaleone on 11/05/18.
 */

public class NavigationDrawerItem {
    public String title;
    public int imgID;

    public NavigationDrawerItem (String title,int imgID) {
        this.title = title;
        this.imgID = imgID;
    }
    //public List<NavigationDrawerItem> getData(){
    public static List<NavigationDrawerItem> getData(){
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int [] imageIDs = {R.drawable.ic_search_black_24dp, R.drawable.ic_library_books_black_24dp, R.drawable.ic_perm_identity_black_24dp, R.drawable.ic_settings_black_24dp, R.drawable.ic_exit_to_app_black_24dp};
        //String [] titles = {"Find a Book","Your Profile","Settings","Log Out"};
        String [] titles = {"Trova un libro","I miei libri","Profilo","Impostazioni","Esci"};
        //Resources res = getResources();
        //String[] titles = res.getStringArray(R.array.menu);
        for (int i=0;i<titles.length;i++)  dataList.add(new NavigationDrawerItem(titles[i],imageIDs[i]));

        return dataList;
    }

}
