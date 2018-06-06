package it.polito.mad.lab5.NavigationDrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import it.polito.mad.lab5.R;

import android.widget.ListView;

/*
 * Created by gianlucaleone on 11/05/18.
 */

public class NavigationDrawerFragment extends Fragment {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public ListView mDrawerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        //setUpRecyclerView(view);

        NavigationDrawerAdapter mAdapter = new NavigationDrawerAdapter(getActivity(), NavigationDrawerItem.getData());
        mDrawerList =  view.findViewById(R.id.navDrawerList);
        mDrawerList.setAdapter(mAdapter);



        //_initMenu();
        return view;
    }




/*
    private void setUpRecyclerView(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.drawerList);

        it.polito.mad.lab3.NavigationDrawer.NavigationDrawerAdapter adapter = it.polito.mad.lab3.NavigationDrawer.NavigationDrawerAdapter(getActivity(), NavigationDrawerItem.getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }*/
/*
    private void _initMenu() {
        NavigationDrawerAdapter mAdapter = new NavigationDrawerAdapter(this, NavigationDrawerItem.getData());
        ListView mDrawerList = (ListView) findViewById(this,R.id.navDrawerList);
        mDrawerList.setAdapter(mAdapter);
    }*/
}