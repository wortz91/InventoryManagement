package wortman.com.inventorymanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wortman.com.openshiftapplication.R;

/**
 * Class for editing and removing inventory items.
 *
 * @author Jason Edwards
 */
public class IMViewEditActivity extends ActionBarActivity {
    private Activity submitActivity = this;
    public static final String SESSION_DATA = "sessionData";
    Context context;
    // Edit text variable to get user changes
    private EditText lbl;
    private EditText itm;
    private EditText cat;
    private EditText modl;
    private Spinner conditionSpinner;
    private EditText loc;
    //class variables from table
    String label;
    String itemName;
    String category;
    String model;
    String condition;
    String location;
    //class variables that are automated
    double latitude;
    double longitude;
    String lastEditDate;
    String lastEditUser;
    InputStream is = null;
    String result = null;
    String line = null;
    int code;
    int itemID;
    int ItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_view_edit);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Mapping EditText field to local variable
        this.lbl = (EditText) this.findViewById(R.id.label_editText);
        this.itm = (EditText) this.findViewById(R.id.itemName_editText);
        this.cat = (EditText) this.findViewById(R.id.catagory_editText);
        this.modl = (EditText) this.findViewById(R.id.model_editText);
        addListenerOnSpinnerItemSelection();
        this.loc = (EditText) this.findViewById(R.id.location_editText);
        //get ItemID to pass to DB call
        this.ItemID = getIntent().getIntExtra("ItemID", ItemID);
        // Looking for ItemId passed from Inventory screen
        if (this.ItemID != 0) {
            new EditInventoryDetails().execute(new ApiConnector());
        }
        // Getting Session data
        final SharedPreferences prefs = getSharedPreferences(SESSION_DATA, 0);
        // Mapping Buttons
        final Button edit = (Button) findViewById(R.id.edit_button);
        final Button save = (Button) findViewById(R.id.save_button);
        final Button delete = (Button) findViewById(R.id.delete_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Showing save button and hidding edit button.
                edit.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                // Setting EditText field to editable
                EditText label_editText = (EditText) findViewById(R.id.label_editText);
                label_editText.setCursorVisible(true);
                label_editText.setClickable(true);
                label_editText.setFocusableInTouchMode(true);
                // Setting EditText field to editable
                EditText model_editText = (EditText) findViewById(R.id.model_editText);
                model_editText.setCursorVisible(true);
                model_editText.setClickable(true);
                model_editText.setFocusableInTouchMode(true);
                // Setting EditText field to editable
                EditText itemName_editText = (EditText) findViewById(R.id.itemName_editText);
                itemName_editText.setCursorVisible(true);
                itemName_editText.setClickable(true);
                itemName_editText.setFocusableInTouchMode(true);
                // Setting EditText field to editable
                EditText catagory_editText = (EditText) findViewById(R.id.catagory_editText);
                catagory_editText.setCursorVisible(true);
                catagory_editText.setClickable(true);
                catagory_editText.setFocusableInTouchMode(true);
                // Setting Spinner to clickable
                Spinner condition_editText = (Spinner) findViewById(R.id.condition_editText);
                condition_editText.setClickable(true);
                condition_editText.setFocusableInTouchMode(true);
                // Setting EditText field to editable
                EditText location_editText = (EditText) findViewById(R.id.location_editText);
                location_editText.setCursorVisible(true);
                location_editText.setClickable(true);
                location_editText.setFocusableInTouchMode(true);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                View focusView = null;
                //global strings get text from EditText fields
                label = lbl.getText().toString();
                // Error handling for blank EditText field on submit
                if (TextUtils.isEmpty(label)) {
                    lbl.setError(getString(R.string.error_field_required));
                    focusView = lbl;
                    error = true;
                }
                itemName = itm.getText().toString();
                // Error handling for blank EditText field on submit
                if (TextUtils.isEmpty(itemName)) {
                    itm.setError(getString(R.string.error_field_required));
                    focusView = itm;
                    error = true;
                }
                category = cat.getText().toString();
                // Error handling for blank EditText field on submit
                if (TextUtils.isEmpty(category)) {
                    cat.setError(getString(R.string.error_field_required));
                    focusView = cat;
                    error = true;
                }
                model = modl.getText().toString();
                // Error handling for blank EditText field on submit
                if (TextUtils.isEmpty(model)) {
                    modl.setError(getString(R.string.error_field_required));
                    focusView = modl;
                    error = true;
                }
                condition = conditionSpinner.getSelectedItem().toString();
                // Error handling for blank Spinner on submit
                if (TextUtils.isEmpty(condition)) {
                    focusView = conditionSpinner;
                    error = true;
                }
                location = loc.getText().toString();
                // Error handling for blank EditText field on submit
                if (TextUtils.isEmpty(location)) {
                    loc.setError(getString(R.string.error_field_required));
                    focusView = loc;
                    error = true;
                }
                //hidden variables
                itemID = ItemID;
                latitude = findLatitude();
                longitude = findLongitude();
                lastEditDate = getLastEditDate();
                lastEditUser = prefs.getString("username", "user");

                if (error) {
                    // There was an error; don't attempt login and focus the last
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    //call method to parse the strings into the proper table column field
                    updateDatabase();
                    // Showing edit button and hidding save button.
                    edit.setVisibility(View.VISIBLE);
                    save.setVisibility(View.INVISIBLE);
                    // Setting EditText field to  not editable
                    EditText label_editText = (EditText) findViewById(R.id.label_editText);
                    label_editText.setCursorVisible(false);
                    label_editText.setClickable(false);
                    label_editText.setFocusableInTouchMode(false);
                    // Setting EditText field to  not editable
                    EditText model_editText = (EditText) findViewById(R.id.model_editText);
                    model_editText.setCursorVisible(false);
                    model_editText.setClickable(false);
                    model_editText.setFocusableInTouchMode(false);
                    // Setting EditText field to  not editable
                    EditText itemName_editText = (EditText) findViewById(R.id.itemName_editText);
                    itemName_editText.setCursorVisible(false);
                    itemName_editText.setClickable(false);
                    itemName_editText.setFocusableInTouchMode(false);
                    // Setting EditText field to  not editable
                    EditText catagory_editText = (EditText) findViewById(R.id.catagory_editText);
                    catagory_editText.setCursorVisible(false);
                    catagory_editText.setClickable(false);
                    catagory_editText.setFocusableInTouchMode(false);
                    // Setting Spinner to not clickable
                    Spinner condition_editText = (Spinner) findViewById(R.id.condition_editText);
                    condition_editText.setClickable(false);
                    condition_editText.setFocusableInTouchMode(false);
                    // Setting EditText field to  not editable
                    EditText location_editText = (EditText) findViewById(R.id.location_editText);
                    location_editText.setCursorVisible(false);
                    location_editText.setClickable(false);
                    location_editText.setFocusableInTouchMode(false);
                }
            }
        });
        // Remove button setup
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("Are You Sure You Want To Remove This Item?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        }
                );
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Archives selected inventory item
                                itemID = ItemID;
                                archiveDatabase();
                                dialog.dismiss();
                                onBackPressed();
                                return;
                            }
                        }
                );
                dialog.show();
            }
        });
        // Cancel Button setup
        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
    }

    /**
     * Method to get Spinner value.
     */
    public void addListenerOnSpinnerItemSelection() {
        conditionSpinner = (Spinner) findViewById(R.id.condition_editText);
        conditionSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    /**
     * Method on handle back button.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Method used to update inventory item after edit.
     *
     * @return true if successful
     */
    public boolean updateDatabase() {
        new AsyncTask<Void, Void, Void>() {
            /**
             * AsyncTask Method to create an ArrayList to pass for update.
             * @param params
             * @return
             */
            @Override
            protected Void doInBackground(Void... params) {
                //create an ArrayList for the values to be stored in
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("ItemID", itemID + ""));
                nameValuePairs.add(new BasicNameValuePair("Label", label));
                nameValuePairs.add(new BasicNameValuePair("ItemName", itemName));
                nameValuePairs.add(new BasicNameValuePair("Category", category));
                nameValuePairs.add(new BasicNameValuePair("ModelNumber", model));
                nameValuePairs.add(new BasicNameValuePair("ConditionID", condition + ""));
                nameValuePairs.add(new BasicNameValuePair("Location", location));
                nameValuePairs.add(new BasicNameValuePair("Latitude", latitude + ""));
                nameValuePairs.add(new BasicNameValuePair("Longitude", longitude + ""));
                nameValuePairs.add(new BasicNameValuePair("LastEditDate", lastEditDate));
                nameValuePairs.add(new BasicNameValuePair("LastEditUser", lastEditUser));
                // Try to connect to PHP script
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s15inventory.franklinpracticum.com/php/update.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }
                // Try verify connection
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    Log.d("sb", sb.toString());
                    is.close();
                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());
                }
                // Try to pass the results to the PHP script
                try {
                    JSONObject json_data = new JSONObject(result);
                    System.out.println(json_data);
                    Log.d("code before,", code + "");
                    code = (json_data.getInt("code"));
                    Log.d("code after grab,", code + "");
                    if (code == 1) {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Updated successfully",
                                        Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    } else {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("Failure code:", code + "");
                        Log.d("Failure 3", "Inserted Unsuccessfully");
                    }
                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());
                }
                return null;
            }
        }.execute();
        return true;
    }

    /**
     * Method to archive selected inventory item.
     *
     * @return true if successful
     */
    public boolean archiveDatabase() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //create an ArrayList for the values to be stored in
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("ItemID", itemID + ""));
                // Try to connect to PHP script
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s15inventory.franklinpracticum.com/php/archiveAPP.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }
                // Try verify connection
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    Log.d("sb", sb.toString());
                    is.close();
                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());
                }
                // Try to pass the results to the PHP script
                try {
                    JSONObject json_data = new JSONObject(result);
                    System.out.println(json_data);
                    Log.d("code before,", code + "");
                    code = (json_data.getInt("code"));
                    Log.d("code after grab,", code + "");
                    if (code == 1) {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Removed successfully",
                                        Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    } else {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Item already exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("Failure code:", code + "");
                        Log.d("Failure 3", "Delete Unsuccessful");
                    }
                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());
                }
                return null;
            }
        }.execute();
        return true;
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
            Intent intent = new Intent(submitActivity, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * AsyncTask Class to handle inventory edits.
     */
    private class EditInventoryDetails extends AsyncTask<ApiConnector, Long, JSONArray> {
        /**
         * Method to get edited Inventory JSONArray.
         *
         * @param params
         * @return inventory details
         */
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            // it is executed on Background thread
            return params[0].EditInventoryDetails(ItemID);
        }

        /**
         * Method to set JSONArray after edit
         *
         * @param jsonArray
         */
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            int position = 0;
            try {
                JSONObject inventoryItem = jsonArray.getJSONObject(0);
                Log.d("inventoryItem JSON:", inventoryItem.toString());
                lbl.setText(inventoryItem.getString("Label"));
                itm.setText(inventoryItem.getString("ItemName"));
                cat.setText(inventoryItem.getString("Category"));
                modl.setText(inventoryItem.getString("ModelNumber"));
                String compare = inventoryItem.getString("ConditionID");
                switch (compare) {
                    case "Excellent":
                        position = 0;
                        break;
                    case "Good":
                        position = 1;
                        break;
                    case "Fair":
                        position = 2;
                        break;
                    case "Poor":
                        position = 3;
                        break;
                }
                conditionSpinner.setSelection(position);
                loc.setText(inventoryItem.getString("Location"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assist methods (Latitude)
     *
     * @return latitude double
     */
    public double findLatitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
            } else {
                latitude = 0.0;
            }
        }
        return latitude;
    }

    /**
     * Assist methods (Longitude)
     *
     * @return longitude double
     */
    public double findLongitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
            } else {
                longitude = 0.0;
            }
        }
        return longitude;
    }

    /**
     * Method to get last edit date.
     *
     * @return date string
     */
    public String getLastEditDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String strDate = sdf.format(cal.getTime());
        return strDate;
    }
}
