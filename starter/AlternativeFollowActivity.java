package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class AlternativeFollowActivity extends AppCompatActivity {

    List<String> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_follow);

        users.add("rob");
        users.add("kristen");

        final ListView listView = (ListView)findViewById(R.id.listView);

        //Add an arraylist if there are no items within the parselist - i.e. parselist is null, needs to be referenced in app
        if (ParseUser.getCurrentUser().getList("isfollowing") == null){
            List<String> emptyList = new ArrayList<>();
            ParseUser.getCurrentUser().put("isfollowing", emptyList);
            ParseUser.getCurrentUser().saveInBackground();
        }

        // SET CHOICE MODE LISTVIEW
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        final ArrayAdapter<String> arrayAdapterSelected = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_checked, users);
        listView.setAdapter(arrayAdapterSelected);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView textview = (CheckedTextView)view;

                //CHECK IF TEXTVIEW IS CHECKED

                if (textview.isChecked()){
                    //YOU CAN USE ARRAYS FOR COLUMNS, HERE; ADDING USER TO AN ARRAY ON PARSE:
                    ParseUser.getCurrentUser().getList("isfollowing").add(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                    Log.i("list", ParseUser.getCurrentUser().getList("isfollowing").toString());
                }else{
                    ParseUser.getCurrentUser().getList("isfollowing").remove(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                }


            }
        });

        users.clear();
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public String done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseUser object : objects) {
                            users.add(object.getUsername());
                        }
                        arrayAdapterSelected.notifyDataSetChanged();

                        for(String username : users){
                            if (ParseUser.getCurrentUser().getList("isfollowing").contains(username)){
                                listView.setItemChecked(users.indexOf(username),true);
                            }
                        }
                    }
                }
                return null;
            }
        });
    }}