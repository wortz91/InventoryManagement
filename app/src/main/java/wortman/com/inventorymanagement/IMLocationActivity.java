package wortman.com.inventorymanagement;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wortman.com.openshiftapplication.R;


public class IMLocationActivity extends ActionBarActivity implements LocationListener, SearchView.OnQueryTextListener {

    private Activity submitActivity = this;
    //private TextView responseTextView;
    private ListView getLocationView;
    private JSONArray jsonArray;
    JSONArray nearbyArray;

    //private Toolbar toolbar;
    //location variables
    double curLat = 0;
    double curLon = 0;
    double dbLat = 0;
    double dbLon = 0;
    double distance = 0.25;


    //Search variables
    private MenuItem searchItem;
    private SearchView searchView;
    public static String query;
    private JSONObject jObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //this.responseTextView = (TextView) this.findViewById(R.id.responseTextView);
        this.getLocationView = (ListView) this.findViewById(R.id.getInventoryView);
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


        //here is a change to push to Unfuddle
        //ListView
        new GetInventoryTask().execute(new ApiConnector());

        curLat = findLatitude();
        curLon = findLongitude();

        dbLat = 0;
        dbLon = 0;

        this.getLocationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get inventory item clicked
                    JSONObject clicked = nearbyArray.getJSONObject(position);

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
        nearbyArray = new JSONArray();

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

                dbLat = Double.parseDouble((String) dbVal.get("Latitude"));
                Log.d("dbLat:", dbLat + "");

                Log.d("curLat:", curLat + "");

                dbLon = Double.parseDouble((String) dbVal.get("Longitude"));
                Log.d("dbLon:", dbLon + "");

                Log.d("curLon:", curLon + "");

                Log.d("nearBy", isNearby(curLat, curLon, dbLat, dbLon, distance) + "");


                //place the nearby code here
                if (isNearby(curLat, curLon, dbLat, dbLon, distance)) {

                    //add item to list -- this setAdapter is adding the entire jsonArray if one is true
                    //I think I need to change the AdapterListView
                    nearbyArray.put(dbVal);
                    this.getLocationView.setAdapter(new GetLocationListViewAdapter(nearbyArray, this));
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        //this.getLocationView.setAdapter(new GetLocationListViewAdapter(jsonArray, this));
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

    public boolean isNearby(double curLat, double curLon, double dbLat, double dbLon, double distance) {
        /*if (Math.sqrt(Math.pow(curLat - dbLat, 2) + Math.pow(curLon - dbLon, 2)) < distance) {
            return true;
        }*/
        return ((curLat < (dbLat + distance)) && (curLat > (dbLat - distance)) &&
                (curLon < (dbLon + distance)) && (curLon > (dbLon - distance)));
    }

    //Assist methods (Latitude)
    public double findLatitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                curLat = location.getLatitude();
            } else {
                curLat = 0.0;
            }
        }

        return curLat;
    }

    //Assist methods (Latitude)
    public double findLongitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                curLon = location.getLongitude();
            } else {
                curLon = 0.0;
            }
        }

        return curLon;
    }

    // junk needed for location manager below

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location loc) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public String getLastEditDate() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");


        return sdf.format(cal.getTime());
    }
}



