package wortman.com.inventorymanagement;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import wortman.com.openshiftapplication.R;


public class MainActivity extends ActionBarActivity implements LocationListener {

    private Activity submitActivity = this;
    private double latitude;
    private double longitude;

    //private Toolbar toolbar;

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


   // public void selectFrag(View view) {
    //    android.support.v4.app.Fragment fr;

   //     if(view == findViewById(R.id.button2)) {
   //         fr = new FragmentTwo();
   //     } else if (view == findViewById(R.id.button3)) {
   //         fr = new FragmentThree();
   //     } else {
   //         fr = new FragmentOne();
   //     }

   //     android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
   //     android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
   //     fragmentTransaction.replace(R.id.test_fragment, fr);
   //    fragmentTransaction.commit();
   // }


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
            Toast.makeText(MainActivity.this, "Settings Button Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(submitActivity, IMHelpActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_print) {
            Toast.makeText(MainActivity.this, "Print Button Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_main) {
            Toast.makeText(MainActivity.this, "Main Menu Button Clicked", Toast.LENGTH_SHORT).show();
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
