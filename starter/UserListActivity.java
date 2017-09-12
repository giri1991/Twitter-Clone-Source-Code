package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserListActivity extends AppCompatActivity  {

    AutoCompleteTextView autoCompleteTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.friends:

                Intent intent  = new Intent(UserListActivity.this, FriendsTweetsAlternate.class);
                startActivity(intent);

                return  true;
            case R.id.tweet:

                final EditText editText = new EditText(this);
                ViewGroup.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                editText.setHint("Send a tweet");
                editText.setLayoutParams(layoutParams);


                new AlertDialog.Builder(this)

                        .setTitle("Tweet")
                        .setView(editText)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (editText.getText().toString().length() <=150) {
                                    ParseUser.getCurrentUser().put("Tweet", editText.getText().toString());
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if (e == null) {
                                                Toast.makeText(UserListActivity.this, "Tweet Successful", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(UserListActivity.this, "Tweet unsuccessful " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("no",null)
                        .show();


                return  true;
            case R.id.logout:

                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e== null){

                            Toast.makeText(UserListActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent(UserListActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {

                            Toast.makeText(UserListActivity.this, "Logout unsuccessful " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                return  true;
             default:
                 return  false;



        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("Search for users");
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        final ArrayList<String> userList = new ArrayList<>();
        final ArrayList<String> selectedUsers = new ArrayList<>();
        final Set<String> userSet = new HashSet<>();

        userList.clear();
        userSet.clear();



        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {


                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains("username",s.toString());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public String done(List<ParseUser> objects, ParseException e) {

                        if (e== null && objects.size() > 0){


                            userSet.clear();
                            userSet.add(objects.get(0).getUsername());
                            userList.clear();
                            userList.addAll(userSet);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,
                                    userList);
                            autoCompleteTextView.setAdapter(arrayAdapter);

                            arrayAdapter.notifyDataSetChanged();
                            autoCompleteTextView.showDropDown();




                        }

                        return null;
                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView listView = (ListView)findViewById(R.id.listView);
                selectedUsers.add((userList.get(0)));
                ArrayAdapter<String> arrayAdapterSelected = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_multiple_choice, selectedUsers);
                listView.setAdapter(arrayAdapterSelected);


                Log.i("unique", selectedUsers.get(0));

                arrayAdapterSelected.notifyDataSetChanged();



               // LayoutInflater inflater = getLayoutInflater();
               //view = (CheckBox) inflater.inflate(R.layout.checkbox,listView);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                // ViewGroup.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                //  ViewGroup.LayoutParams.WRAP_CONTENT);





                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        CheckedTextView textview = (CheckedTextView)view;
                        textview.toggle();




                       ParseUser.getCurrentUser().put(selectedUsers.get(position), "true");
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e==null){

                                    Toast.makeText(UserListActivity.this, "Followed " + selectedUsers.get(position) + " successfully", Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(UserListActivity.this, "Followed " + selectedUsers.get(position) + " Unsuccessfully: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                });



            }
        });






    }


}
