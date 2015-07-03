package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wortman.com.openshiftapplication.R;


public class IMLocationActivity extends ActionBarActivity implements LocationListener {

    private Activity submitActivity = this;
    //private TextView responseTextView;
    private ListView getLocationView;
    private JSONArray jsonArray;

    //private Toolbar toolbar;
    //location variables
    double curLat = 0;
    double curLon = 0;
    double dbLat = 0;
    double dbLon = 0;
    double distance = 70000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

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
                    JSONObject clicked = jsonArray.getJSONObject(position);

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
        try {
            Log.d("JSONArray", jsonArray.getJSONObject(0).toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }

        try {
            JSONObject dbVal = jsonArray.getJSONObject(0);

            dbLat =  Double.parseDouble((String) dbVal.get("Latitude"));
            Log.d("dbLat:", dbLat + "");

            Log.d("curLat:", curLat + "");

            dbLon = Double.parseDouble((String) dbVal.get("Longitude"));
            Log.d("dbLon:", dbLon + "");

            Log.d("curLon:", curLon + "");

            Log.d("nearBy", isNearby(curLat, curLon, dbLat, dbLon, distance) + "");
        } catch (JSONException je) {
            je.printStackTrace();
        }

        //place the nearby code here
        if(isNearby(curLat, curLon, dbLat, dbLon, distance)) {
            //add item to list
            this.getLocationView.setAdapter(new GetLocationListViewAdapter(jsonArray, this));
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
        if (Math.sqrt(Math.pow(curLat - dbLat, 2) + Math.pow(curLon - dbLon, 2)) < distance) {
            return true;
        }
        return false;
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

        String strDate = sdf.format(cal.getTime());
        return strDate;
    }
}



