package wortman.com.inventorymanagement;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Field;

import java.lang.reflect.Field;

import wortman.com.openshiftapplication.R;


public class MainActivity extends ActionBarActivity implements LocationListener, SearchView.OnQueryTextListener {

    //This is added so something changed so I could make a push to the GIT
    private Activity submitActivity = this;
    private double latitude;
    private double longitude;

    private MenuItem searchItem;
    private SearchView searchView;

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

        RelativeLayoutButton button1 = new RelativeLayoutButton(this,R.id.inventory_button);
        RelativeLayoutButton button2 = new RelativeLayoutButton(this,R.id.add_button);
        RelativeLayoutButton button3 = new RelativeLayoutButton(this,R.id.barcode_button);
        RelativeLayoutButton button4 = new RelativeLayoutButton(this,R.id.report_button);
        RelativeLayoutButton button5 = new RelativeLayoutButton(this,R.id.nearby_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(submitActivity, IMInventoryActivity.class);
                startActivity(intent);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(submitActivity, IMAddActivity.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(submitActivity, IMBarcodeActivity.class);
                startActivity(intent);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(submitActivity, IMReportActivity.class);
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(submitActivity, IMLocationActivity.class);
                startActivity(intent);

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
        String query = String.valueOf(extras.get(SearchManager.QUERY));

        Log.d("query:", query);
        Log.d("userQuery:", userQuery);

        Toast.makeText(this, "query: " + query + " user_query: " + userQuery,
                Toast.LENGTH_SHORT).show();

        //add the ListViewAdapter
        //setup the method like getByLabel
        //pass the variable in as a 'label'
        //use the generalsearch.php instead of getByLabel.php
        //value is added after '?label=label
        //base generalSearch off of getByLabel.php in conjunction
        // with my current generalsearch.php

    }

    protected void showSearch(boolean visible) {
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }

    public void search(String searchQuery) {
        Toast.makeText(this, "The searched for query is:" + searchQuery, Toast.LENGTH_LONG).show();

        Log.d("inside search method:", searchQuery);
    }

    /**
     * Called when the hardware search button is pressed
     */
    @Override
    public boolean onSearchRequested() {
        Log.d("onSearchRequested();", "searchySearchSearchSearch");
        showSearch(true);

        // dont show the built-in search dialog
        return false;
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

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() != 0.0){
            latitude = location.getLatitude();
        }
        if(location.getLongitude() != 0.0){
            longitude = location.getLongitude();
        }

    }

    public void setLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = l.getLatitude();
            longitude = l.getLongitude();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setLocation();
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

    // junk needed for location manager below

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
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
}
