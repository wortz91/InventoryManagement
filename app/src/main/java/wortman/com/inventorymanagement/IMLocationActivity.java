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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import wortman.com.openshiftapplication.R;

/**
 * Gets the location of the device
 *
 * @author Nicholas Wortman
 */
public class IMLocationActivity extends ActionBarActivity implements LocationListener, SearchView.OnQueryTextListener {

    private Activity submitActivity = this;
    //private TextView responseTextView;
    private ListView getLocationView;
    private JSONArray jsonArray;
    JSONArray nearbyArray;

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

    /**
     * the main view
     * @param savedInstanceState    the bundle of passed variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        this.getLocationView = (ListView) this.findViewById(R.id.getInventoryView);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        //creates search bar object
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //creates search view bar
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

        //the cursor for the search bar
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

        //ListView
        new GetInventoryTask().execute(new ApiConnector());

        //finds the latitude
        curLat = findLatitude();

        //finds the longitude
        curLon = findLongitude();

        //the variables from the database
        dbLat = 0;
        dbLon = 0;

        //creates the view for the activity
        this.getLocationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get inventory item clicked
                    JSONObject clicked = nearbyArray.getJSONObject(position);

                    // Do something with the click, here we would go to edit screen
                    Intent showDetails = new Intent(getApplicationContext(), IMViewEditActivity.class);
                    showDetails.putExtra("ItemID", clicked.getInt("ItemID"));

                    //starts the given activity
                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * what happens on new intent
     * @param intent    the new activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent:", intent.toString());
        showSearch(false);

        //the bundle of intents
        Bundle extras = intent.getExtras();

        //the strings from the intents
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

    /**
     * shows the search
     *
     * @param visible   returns true if the value is searchable
     */
    protected void showSearch(boolean visible) {
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }

    /**
     * what happens when the searched text is submitted
     * @param myQuery   the searched for text
     * @return  false if text is submitted
     */
    @Override
    public boolean onQueryTextSubmit(String myQuery) {

        return (false);
    } /* on query text submit */


    /**
     * what happens when the searched for text is changes
     * @param change    the string representation of the text
     * @return  false if text is changing
     */
    @Override
    public boolean onQueryTextChange(String change)
    {
        // "change" represents current text string as being typed
        return(false);
    } /* on query text change */

    /**
     * the nearby terms
     *
     * @param jsonArray the entire passed in array
     */
    public void setListAdapter (JSONArray jsonArray) {
        //the entire database as a JSONArray
        this.jsonArray = jsonArray;

        //empty new array
        nearbyArray = new JSONArray();

        try {
            Log.d("JSONArray", jsonArray.getJSONObject(0).toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }

        try {

            Log.d("Remaining objects:", jsonArray.length() + "");

            //goes through all values in the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dbVal = jsonArray.getJSONObject(i);
                Log.d("JSONObject:", dbVal.toString());

                Log.d("JSONObject:", dbVal.toString());

                //adds the value from the parsed double in the database to the variable
                dbLat = Double.parseDouble((String) dbVal.get("Latitude"));
                Log.d("dbLat:", dbLat + "");

                Log.d("curLat:", curLat + "");

                //adds the value from the parsed double in the database to the variable
                dbLon = Double.parseDouble((String) dbVal.get("Longitude"));
                Log.d("dbLon:", dbLon + "");

                Log.d("curLon:", curLon + "");

                Log.d("nearBy", isNearby(curLat, curLon, dbLat, dbLon, distance) + "");


                //place the nearby code here
                if (isNearby(curLat, curLon, dbLat, dbLon, distance)) {

                    //add item to list -- this setAdapter is adding the entire jsonArray if one is true
                    nearbyArray.put(dbVal);
                    this.getLocationView.setAdapter(new GetLocationListViewAdapter(nearbyArray, this));
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * this parses the array
     */
    private class GetInventoryTask extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        /**
         * the array modification on the background thread
         * @param params    the passed in ApiConnector objects
         * @return  the json Array
         */
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread
            return params[0].GetInventory();
        }

        /**
         * what to do with the array after it is created
         * @param jsonArray the json array created on the background thread
         */
        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            setListAdapter(jsonArray);
        }
    }

    /**
     * what to do once it comes back from the background thread
     */
    @Override
    protected void onResume() {
        super.onResume();

        new GetInventoryTask().execute(new ApiConnector());
    }

    /**
     * infaltes the search menu
     * @param menu  the menu to be inflated
     * @return  returns true if the menu is inflated
     */
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

    /**
     * creates the overflow menu
     * @param item  the individual clickable objects
     * @return  return true if an item is selected
     */
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

    /**
     * this is the method to calculate if the object is nearby
     *
     * @param curLat    the phones latitude
     * @param curLon    the phones longitude
     * @param dbLat     the latitude of the object in the database
     * @param dbLon     the longitude of the object in the database
     * @param distance  what the distance that is deemed "nearby"
     * @return          true if nearby
     */
    public boolean isNearby(double curLat, double curLon, double dbLat, double dbLon, double distance) {
        /*if (Math.sqrt(Math.pow(curLat - dbLat, 2) + Math.pow(curLon - dbLon, 2)) < distance) {
            return true;
        }*/
        return ((curLat < (dbLat + distance)) && (curLat > (dbLat - distance)) &&
                (curLon < (dbLon + distance)) && (curLon > (dbLon - distance)));
    }

    /**
     * gets the latitude from gps
     * @return  the latitude
     */
    public double findLatitude() {
        //sets up the gps object
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            //gets the gps location
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                curLat = location.getLatitude();
            } else {
                curLat = 0.0;
            }
        }

        return curLat;
    }

    /**
     * gets the longitude of the device
     * @return  the longitude
     */
    public double findLongitude() {
        //sets up the gps object
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            //gets the gps location
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

    /**
     * gets the edit date
     * @return  the date that the database was last edited in the given format dd:MMMM:yyyy HH:mm:ss
     */
    public String getLastEditDate() {
        //gets the calendar object
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");


        return sdf.format(cal.getTime());
    }
}



