package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendTweets extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_tweets);

        ListView listView = (ListView)findViewById(R.id.listView);

        final ArrayList<HashMap<String,String>> hashMaps = new ArrayList<>();
        //hashMap.clear();
        hashMaps.clear();
        String[] from = {"friend","tweet"};
        int[] to = {R.id.textView, R.id.textView1};
        final SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), hashMaps,R.layout.
                list_view_items ,from,to);
        listView.setAdapter(simpleAdapter);
        setTitle("Friend Activity");
        final ArrayList<String> friendNames = new ArrayList<>();
        friendNames.clear();
        Intent intent = getIntent();

        final ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo(ParseUser.getCurrentUser().getUsername(), "true");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public String done(List<ParseUser> objects, ParseException e) {

                if (e== null){

                    if (objects.size() > 0) {

                        for (final ParseUser object : objects ) {
                            friendNames.clear();


                          ParseQuery<ParseUser> queryInit = ParseUser.getQuery();
                            queryInit.whereEqualTo(object.getUsername(), "true");
                            queryInit.findInBackground(new FindCallback<ParseUser>() {
                              @Override
                              public String done(List<ParseUser> objectsInit, ParseException e) {
                                  if (e==null){
                                      if (objectsInit.size()> 0){
                                          for (int i=0; i<objectsInit.size();i++) {
                                              Log.i("names",objectsInit.get(i).getUsername());
                                              if (objectsInit.get(i).getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                                                  HashMap<String, String> hashMap = new HashMap<>();
                                                  hashMap.put("friend", (object.getUsername()));
                                                  friendNames.add(objectsInit.get(i).getUsername());
                                                  if (object.getString("Tweet") != null) {

                                                      if (!object.getString("Tweet").equals("")) {
                                                          hashMap.put("tweet", object.getString("Tweet"));

                                                      }
                                                  } else {
                                                      hashMap.put("tweet", "");
                                                  }
                                                  hashMaps.add(hashMap);
                                                  //Log.i("unique",hashMaps.get(0).get("tweet"));
                                                  simpleAdapter.notifyDataSetChanged();

                                              }
                                                  if (i == objectsInit.size() - 1 && friendNames.size() == 0) {

                                                      new AlertDialog.Builder(FriendTweets.this)
                                                              .setTitle("Friend Request")
                                                              .setMessage(object.getUsername() + " has sent you a friend request")
                                                              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      ParseUser.getCurrentUser().put(object.getUsername(),
                                                                              "true");
                                                                      ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                                          @Override
                                                                          public void done(ParseException e) {
                                                                              if (e==null){
                                                                                  Toast.makeText(FriendTweets.this, object.getUsername() + " " +
                                                                                          "has been added to your friends list", Toast.LENGTH_SHORT).show();
                                                                              }
                                                                          }
                                                                      });
                                                                  }
                                                              })
                                                              .setNegativeButton("no", null)
                                                              .show();
                                                  }
                                              }
                                          } else {
                                          new AlertDialog.Builder(FriendTweets.this)
                                                  .setTitle("Friend Request")
                                                  .setMessage(object.getUsername() + " has sent you a friend request")
                                                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          ParseUser.getCurrentUser().put(object.getUsername(),
                                                                  "true");
                                                          ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                              @Override
                                                              public void done(ParseException e) {
                                                                  if (e==null){
                                                                      Toast.makeText(FriendTweets.this, object.getUsername() + " " +
                                                                              "has been added to your friends list", Toast.LENGTH_SHORT).show();
                                                                  }
                                                              }
                                                          });
                                                      }
                                                  })
                                                  .setNegativeButton("no", null)
                                                  .show();}
                                  }

                                  return null;
                              }
                          });

                           // Log.i("friend",hashMap.get("friend"));

                        }

                }} else {

                    Toast.makeText(FriendTweets.this, "Friends could not be displayed " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
                return null;
            }
        });




    }
}
