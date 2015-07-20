package wortman.com.inventorymanagement;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Field;

import wortman.com.openshiftapplication.R;


public class IMInventoryActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{

    private Activity submitActivity = this;
    //private TextView responseTextView;
    private ListView getInventoryView;
    private JSONArray jsonArray;

    //Search variables
    private MenuItem searchItem;
    private SearchView searchView;
    public static String query;
    private JSONObject jObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //this.responseTextView = (TextView) this.findViewById(R.id.responseTextView);
        this.getInventoryView = (ListView) this.findViewById(R.id.getInventoryView);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(1000);

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById((android.support.v7.appcompat.R.id.search_src_text));

        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    showSearch(false);
            }
        });

        try {
            // This sets the cursor
            // resource ID to 0 or @null
            // which will make it visible
            // on white background
            Field mCursorDrawableRes = TextView.class
                    .getDeclaredField("mCursorDrawableRes");

            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        new GetInventoryTask().execute(new ApiConnector());

        this.getInventoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get inventory item clicked
                    JSONObject clicked = jsonArray.getJSONObject(position);

                    // Do something with the click, here we would go to edit screen
                    Intent showDetails = new Intent(getApplicationContext(),IMViewEditActivity.class);
                    showDetails.putExtra("ItemID", clicked.getInt("ItemID"));
                    showDetails.putExtra("ConditionID", clicked.getString("ConditionID"));
                    Log.d("JSONObject passed in:", clicked.getInt("ItemID") + "");
                    Log.d("JSONObject passed in:", clicked.getString("ConditionID") + "");

                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent:", intent.toString());
        showSearch(false);
        Bundle extras = intent.getExtras();
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        query = String.valueOf(extras.get(SearchManager.QUERY));

        Log.d("query:", query);
        Log.d("userQuery:", userQuery);

        Toast.makeText(this, "query: " + query + " user_query: " + userQuery,
                Toast.LENGTH_SHORT).show();

        if(userQuery != null) {
            Intent searchResults = new Intent(this, IMSearchResultsActivity.class);
            searchResults.putExtra("SearchResults", userQuery);
            startActivity(searchResults);
        }

        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
    }

    protected void showSearch(boolean visible) {
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }



    @Override
    public boolean onQueryTextSubmit(String myQuery) {

        return (false);
    } /* on query text submit */


    @Override
    public boolean onQueryTextChange(String change)
    {
        // "change" represents current text string as being typed
        return(false);
    } /* on query text change */

    public void setListAdapter (JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.getInventoryView.setAdapter(new GetInventoryListViewAdapter(jsonArray,this));
    }

    private class GetInventoryTask extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread
            return params[0].GetInventory();
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
        new GetInventoryTask().execute(new ApiConnector());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
        searchItem = menu.add(android.R.string.search_go);

        searchItem.setIcon(R.drawable.ic_action_search);

        MenuItemCompat.setActionView(searchItem, searchView);

        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

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
