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
//import for zxing api
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//json import
import org.json.JSONArray;
import org.json.JSONObject;

import wortman.com.openshiftapplication.R;

/**
 * Class created to used the zxing api for barcode scanning.
 *
 * @author Jason Edwards
 */
public class IMBarcodeActivity extends ActionBarActivity {
    private Activity submitActivity = this;
    private Button scan;
    private String Label;
    private JSONObject jObj;
    int ItemID;
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
        // scan button
        scan = (Button) findViewById(R.id.scanBtn);
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
        // cancel button
        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
    }

    /**
     * Method to hand back press.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Method to get the results of the scan and store them.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
            } else {
                Log.d("MainActivity", "Scanned");
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

    /**
     * AsyncTack Class to get inventory items from JSON using APIConnector class.
     *
     * @author Jason Edwards
     */
    private class GetInventoryTask extends AsyncTask<ApiConnector, Long, JSONArray> {
        /**
         * Method to get inventory array.
         *
         * @param params
         * @return
         */
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            // it is executed on Background thread
            return params[0].GetInventory();
        }

        /**
         * Method ran after execute to process JsonArray JsonObjects to find scanned value.
         *
         * @param jsonArray
         */
        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            try {
                if (jsonArray != null) {
                    boolean isMatch = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        if (jObj.getString("Label").equals(Label)) {
                            ItemID = jObj.getInt("ItemID");
                            exists();
                            isMatch = true;
                        }
                    }
                    if (!isMatch) {
                        doesntExists();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to execute if scanned UPC exists to get item information from JsonObject.
     */
    private void exists() {
        new ScanInventoryDetailsAddEdit().execute(new ApiConnector());
        if (ItemID != 0) {
            Intent showDetails = new Intent(getApplicationContext(), IMViewEditActivity.class);
            showDetails.putExtra("ItemID", ItemID);
            startActivity(showDetails);
        }
    }

    /**
     * Method to execute when scanned UPC does not exist to open the
     * Add screen and add the UPC to the Serial EditText field.
     */
    private void doesntExists() {
        new ScanInventoryDetailsAddEdit().execute(new ApiConnector());
        if (Label != null) {
            Intent showDetails = new Intent(getApplicationContext(), IMAddActivity.class);
            showDetails.putExtra("Label", Label);
            startActivity(showDetails);
        }
    }

    /**
     * AsyncTack Class to get the complete JSONObject details.
     *
     * @author Jason Edwards
     */
    private class ScanInventoryDetailsAddEdit extends AsyncTask<ApiConnector, Long, JSONArray> {
        /**
         * Method to get row details from JSONObject.
         *
         * @param params
         * @return JsonObject details
         */
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            // it is executed on Background thread
            return params[0].ScanInventoryDetailsAddEdit(Label);
        }

        /**
         * Method post execute to set variable values.
         *
         * @param jsonArray
         */
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                if (jsonArray != null) {
                    JSONObject inventoryItem = jsonArray.getJSONObject(0);

                    ItemID = inventoryItem.getInt("ItemID");
                    Label = inventoryItem.getString("Label");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Method tpo inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
        return true;
    }

    /**
     * Method to set what shows in the overflow menu.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
