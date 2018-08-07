package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class Messages extends AppCompatActivity {

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    private EditText messagesText;
    // These are the Contacts rows that we will retrieve
//    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
//            ContactsContract.Data.DISPLAY_NAME};

    // This is the select criteria
//    static final String SELECTION = "((" +
//            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
//            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        // Create a progress bar to display while the list loads
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        progressBar.setIndeterminate(true);
//        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        root.addView(progressBar);

        messagesText = (EditText) findViewById(R.id.messagesText);

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        Set<String> receivedMessages = settings.getStringSet("messages", null);
        if(receivedMessages == null) receivedMessages = new HashSet<>();
        // For the cursor adapter, specify which columns go into which views
//        String[] fromColumns = receivedMessages.toArray(new String[receivedMessages.size()]);
        for(String message: receivedMessages) {
            messagesText.append(message + "\n\n\n\n");
        }


//        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
//
//        // Create an empty adapter we will use to display the loaded data.
//        // We pass null for the cursor, then update it in onLoadFinished()
//        mAdapter = new SimpleCursorAdapter(this,
//                android.R.layout.simple_list_item_1, null,
//                fromColumns, toViews, 0);
//        setListAdapter(mAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
//        getLoaderManager().initLoader(0, null, this);
    }

    // Called when a new Loader needs to be created
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
//                PROJECTION, SELECTION, null, null);
//    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Do something when a list item is clicked
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

//        if (i == R.id.chat_menu) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        } else

        if (i == R.id.about_menu) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        } else if (i == R.id.work_menu) {
            Intent intent = new Intent(this, WorkActivity.class);
            startActivity(intent);
        } else if(i == R.id.bill_menu) {
            Intent intent = new Intent(this, BillSummary.class);
            startActivity(intent);
        } else if (i == R.id.messages) {
            Intent intent = new Intent(this, Messages.class);
            startActivity(intent);
        } else if (i == R.id.sign_in_menu) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        return false;
    }

}