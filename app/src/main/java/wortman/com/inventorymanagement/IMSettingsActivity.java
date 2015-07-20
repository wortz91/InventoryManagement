package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import wortman.com.openshiftapplication.R;

public class IMSettingsActivity extends ActionBarActivity {
    private Activity submitActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        Switch wirelessSwitch = (Switch)findViewById(R.id.wirelessSwitch);
        wirelessSwitch.setChecked(true);
        wirelessSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifi.setWifiEnabled(true);
                    Toast.makeText(IMSettingsActivity.this, "Wifi Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    wifi.setWifiEnabled(false);
                    Toast.makeText(IMSettingsActivity.this, "Wifi Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button gpsButton = (Button) findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
