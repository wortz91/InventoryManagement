package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wortman.com.openshiftapplication.R;

/**
 * Created by Nicholas on 7/19/2015.
 */
public class IMSearchResultsActivity extends ActionBarActivity{

    private Activity submitActivity = this;
    public static final String SESSION_DATA = "sessionData";
    Context context;

    private ListView getSearchView;
    private JSONArray jsonArray;
    private JSONArray searchedArray;

    private String searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        Intent searchIntent = getIntent();

        searchResults= searchIntent.getExtras().getString("SearchResults");

        Toast.makeText(this, "searchResult is:" + searchResults, Toast.LENGTH_LONG).show();

        //this is where the actual activity begins!
        //this.responseTextView = (TextView) this.findViewById(R.id.responseTextView);
        this.getSearchView = (ListView) this.findViewById(R.id.getSearchResults);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        //here is a change to push to Unfuddle
        //ListView
        new GetSearchTask().execute(new ApiConnector());


        this.getSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get inventory item clicked
                    JSONObject clicked = searchedArray.getJSONObject(position);

                    // Do something with the click, here we would go to edit screen
                    Intent showDetails = new Intent(getApplicationContext(), IMViewEditActivity.class);
                    showDetails.putExtra("ItemID", clicked.getInt("ItemID"));

                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void setListAdapter (JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        searchedArray = new JSONArray();

        try {
            Log.d("JSONArray", jsonArray.getJSONObject(0).toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }

        try {

            Log.d("Remaining objects:", jsonArray.length() + "");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dbVal = jsonArray.getJSONObject(i);
                Log.d("JSONObject:", dbVal.toString());

                Log.d("JSONObject:", dbVal.toString());

                searchedArray.put(dbVal);
                this.getSearchView.setAdapter(new GetSearchListViewAdapter(searchedArray, this));

            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        //this.getLocationView.setAdapter(new GetLocationListViewAdapter(jsonArray, this));
    }


    private class GetSearchTask extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread
            return params[0].GetSearchInventory(searchResults);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            //setTextToTextView(jsonArray);
            setListAdapter(jsonArray);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        new GetSearchTask().execute(new ApiConnector());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(submitActivity, IMSettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(submitActivity, IMHelpActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_main) {
            Intent intent = new Intent(submitActivity, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}