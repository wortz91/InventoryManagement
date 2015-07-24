package wortman.com.inventorymanagement;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import wortman.com.openshiftapplication.R;

/**
 * Created by Nicholas on 7/6/2015.
 */
public class IMSearchableActivity extends ActionBarActivity {

    private Activity submitActivity = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        //this sets up the search feature and passes in the searched text
        Intent searchIntent = getIntent();
        Log.d("searchIntent", getIntent().toString());
        Log.d("before search", searchIntent.toString());
        Log.d("actionSearch", Intent.ACTION_SEARCH);
//        Log.d("actionIntent", searchIntent.getAction());

        //checks if the action is equal to the search action for the intents
        if(Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
            String searchQuery = searchIntent.getStringExtra(SearchManager.QUERY);
            Log.d("searchQuery", searchQuery);

            search(searchQuery);
        }

        Log.d("After search intent:", searchIntent.toString());
    }

    /**
     * the search string is passed
     * @param searchQuery   the search term
     */
    public void search(String searchQuery) {
        //Toast.makeText(this, "The searched for query is:" + searchQuery, Toast.LENGTH_LONG).show();

        Log.d("inside search method:", searchQuery);
    }
}
