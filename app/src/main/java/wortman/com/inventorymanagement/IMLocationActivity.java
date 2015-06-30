package wortman.com.inventorymanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import wortman.com.openshiftapplication.R;


public class IMLocationActivity extends ActionBarActivity implements LocationListener{

    private Activity submitActivity = this;
    //need to initialize db
    Context context;
    private ArrayList<String> placeholder = new ArrayList<>(Arrays.asList("test1"+ "       test11" + "       test111","test2","test3","test4","test5","test6"));
    public static final String SESSION_DATA = "sessionData";

    //current latitude/longitude
    private double curLat;
    private double curLon;

    //database latitude/longitude
    private double dbLat;
    private double dbLon;

    //private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Session
        SharedPreferences prefs = getSharedPreferences(SESSION_DATA, 0);

        // Set current latitude/longitude
        curLat = findLatitude();
        curLon = findLongitude();

        //set dbLatitude/dbLongitude
        dbLat = 0;
        dbLon = 0;

        if(isNearby(curLat, curLon, dbLat, dbLon)) {
            populateListView();
        } else {
            Toast.makeText(this, "There are no nearby items", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void populateListView() {
        //open db
        // need db calls here
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.location_bg, placeholder);
        // Configure the list view.
        ListView list = (ListView) findViewById(R.id.location_listView);
        list.setAdapter(adapter);
        // close db
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

    public boolean isNearby(double curLat, double curLon, double dbLat, double dbLon) {
        if (Math.sqrt(Math.pow(curLat - dbLat, 2) + Math.pow(curLon - dbLon, 2) ) < .25) {
            return true;
        }
        return false;
    }
    //Assist methods (Latitude)
    public double findLatitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if(lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
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

        if(lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
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
}
