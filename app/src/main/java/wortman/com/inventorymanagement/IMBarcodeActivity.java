package wortman.com.inventorymanagement;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import wortman.com.openshiftapplication.R;


public class IMBarcodeActivity extends ActionBarActivity {

    private Activity submitActivity = this;
    private Button scan;
    private String Label;

    private JSONObject jObj;
    int ItemID;

    //private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        //SCAN INITIATION AND LAUNCH
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(IMCaptureActivityOrientation.class);

        scan= (Button)findViewById(R.id.scanBtn);
        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("SCAN A BARCODE");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.initiateScan();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Label = result.getContents().toString();
                //GET INVENTORY ARRAY
                new GetInventoryTask().execute(new ApiConnector());
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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

            try {
                if(jsonArray != null) {
                    boolean isMatch = false;
                    for (int i = 0 ; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        if (jObj.getString("Label").equals(Label)) {
                            ItemID = jObj.getInt("ItemID");
                            exists();
                            isMatch = true;
                        }
                    }
                    if (!isMatch){
                        doesntExists();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void exists(){
        new ScanInventoryDetailsAddEdit().execute(new ApiConnector());
        if(ItemID != 0) {
            Intent showDetails = new Intent(getApplicationContext(), IMViewEditActivity.class);
            showDetails.putExtra("ItemID", ItemID);
            startActivity(showDetails);
        }
    }

    private void doesntExists(){
        new ScanInventoryDetailsAddEdit().execute(new ApiConnector());
        if(Label != null) {
            Intent showDetails = new Intent(getApplicationContext(), IMAddActivity.class);
            showDetails.putExtra("Label", Label);
            startActivity(showDetails);
        }
    }

    private class ScanInventoryDetailsAddEdit extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread

            return params[0].ScanInventoryDetailsAddEdit(Label);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            try
            {
                if(jsonArray != null) {
                    JSONObject inventoryItem = jsonArray.getJSONObject(0);

                    ItemID = inventoryItem.getInt("ItemID");
                    Label = inventoryItem.getString("Label");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
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
