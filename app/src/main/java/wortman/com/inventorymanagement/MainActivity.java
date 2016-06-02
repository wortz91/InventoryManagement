package wortman.com.inventorymanagement;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import wortman.com.openshiftapplication.R;

/**
 * Main Activity class for main menu.
 *
 * Author Nicholas Wortman, Jason Edwards, Bob Boatwright
 */
public class MainActivity extends ActionBarActivity implements LocationListener, SearchView.OnQueryTextListener {
    private Activity submitActivity = this;
    private double latitude;
    private double longitude;
    //Search variables
    private MenuItem searchItem;
    private SearchView searchView;
    public static String query;
    // Error message
    private final String errmsg = "You must be connected to " +
            "a WIFI or cellular connection to use this feature " +
            "Please connect to a network and try again.";

    /**
     * the initial view
     * @param savedInstanceState    the initial variables from the IMLoginActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Search
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
            // This sets the cursor resource ID to 0 or @null
            // which will make it visible on white background
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Mapping buttons
        RelativeLayoutButton button1 = new RelativeLayoutButton(this, R.id.inventory_button);
        RelativeLayoutButton button2 = new RelativeLayoutButton(this, R.id.add_button);
        RelativeLayoutButton button3 = new RelativeLayoutButton(this, R.id.barcode_button);
        RelativeLayoutButton button4 = new RelativeLayoutButton(this, R.id.report_button);
        RelativeLayoutButton button5 = new RelativeLayoutButton(this, R.id.nearby_button);
        //Listener for View Inventory button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(submitActivity, IMInventoryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), errmsg,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        //Listener for Add Inventory Item button
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(submitActivity, IMAddActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), errmsg,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        // Listener for Barcode Scanner button
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(submitActivity, IMBarcodeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), errmsg,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        // Listener for Generate Report button
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(submitActivity, IMReportActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), errmsg,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        // Listener for Nearby Inventory button
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Intent intent = new Intent(submitActivity, IMLocationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), errmsg,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Make sure there is a network connection.
     *
     * @return true if there is
     */
    public boolean isConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to take intent to searched item.
     *
     * @param intent
     */
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
        if (userQuery != null) {
            Intent searchResults = new Intent(this, IMSearchResultsActivity.class);
            searchResults.putExtra("SearchResults", userQuery);
            startActivity(searchResults);
        }
    }

    /**
     * Method to show search field.
     *
     * @param visible
     */
    protected void showSearch(boolean visible) {
        if (visible) {
            MenuItemCompat.expandActionView(searchItem);
        } else {
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    /**
     * Method onQueryTextSubmit needed for search functionality.
     *
     * @param myQuery
     * @return false
     */
    @Override
    public boolean onQueryTextSubmit(String myQuery) {
        return (false);
    }

    /**
     * Method onQueryTextChange needed for search functionality.
     *
     * @param change
     * @return false
     */
    @Override
    public boolean onQueryTextChange(String change) {
        // "change" represents current text string as being typed
        return (false);
    }

    /**
     * Method to set latitude and logitude.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() != 0.0) {
            latitude = location.getLatitude();
        }
        if (location.getLongitude() != 0.0) {
            longitude = location.getLongitude();
        }
    }

    /**
     * Method to set location.
     */
    public void setLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = l.getLatitude();
            longitude = l.getLongitude();
        }
    }

    /**
     * Method to handle on resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        setLocation();
    }

    /**
     * Method to inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu
     * @return true
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
     * Method to set what shows in the overflow menu.
     *
     * @param item
     * @return option selected
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
            //Do nothing.
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method needed for location manager.
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // not used but needed
    }

    /**
     * Method needed for location manager.
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
        // not used but needed
    }

    /**
     * Method needed for location manager.
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        // not used but needed
    }
}
