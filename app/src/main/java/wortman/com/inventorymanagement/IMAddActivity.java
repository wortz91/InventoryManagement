package wortman.com.inventorymanagement;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wortman.com.openshiftapplication.R;


public class IMAddActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    //this is for navigation with the overflow menu
    private Activity submitActivity = this;
    public static final String SESSION_DATA = "sessionData";
    String Label;
    private EditText lbl;

    //class variables from table
    String label;
    String itemName;
    String category;
    String model;
    String condition;
    Spinner conditionSpinner;
    String location;

    //class variables that are automated
    int id;
    double latitude;
    double longitude;
    String createDate;
    String lastEditDate;
    String lastEditUser;

    //the input stream
    InputStream is = null;
    String result = null;
    String line = null;
    int code;

    //search requirements
    private MenuItem searchItem;
    private SearchView searchView;
    public static String query;
    private JSONObject jObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        //sets up the search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(1000);

        //adds the search bar to the ActionBar
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

        //get ItemID to pass to DB call
        this.Label = getIntent().getStringExtra("Label");
        if (Label != null){
            this.lbl = (EditText) this.findViewById(R.id.label_editText);
            lbl.setText(Label);
        }

        final EditText lbl = (EditText) findViewById(R.id.label_editText);
        final EditText itm = (EditText) findViewById(R.id.itemName_editText);
        final EditText cat = (EditText) findViewById(R.id.category_editText);
        final EditText modl = (EditText) findViewById(R.id.model_editText);

        //calls a create spinner variable
        addListenerOnSpinnerItemSelection();

        final EditText loc = (EditText) findViewById(R.id.location_editText);
        Log.d("User",SESSION_DATA);

        final SharedPreferences prefs = getSharedPreferences(SESSION_DATA, 0);
        Log.d("User",SESSION_DATA);

        //determines what to do upon add button click
        findViewById(R.id.add_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean error = false;
                        View focusView = null;
                        //global strings get text from EditText fields
                        label = lbl.getText().toString();
                        if (TextUtils.isEmpty(label)){
                            lbl.setError(getString(R.string.error_field_required));
                            focusView = lbl;
                            error = true;
                        }
                        itemName = itm.getText().toString();
                        if (TextUtils.isEmpty(itemName)){
                            itm.setError(getString(R.string.error_field_required));
                            focusView = itm;
                            error = true;
                        }
                        category = cat.getText().toString();
                        if (TextUtils.isEmpty(category)){
                            cat.setError(getString(R.string.error_field_required));
                            focusView = cat;
                            error = true;
                        }
                        model = modl.getText().toString();
                        if (TextUtils.isEmpty(model)){
                            modl.setError(getString(R.string.error_field_required));
                            focusView = modl;
                            error = true;
                        }
                        condition = conditionSpinner.getSelectedItem().toString();
                        location = loc.getText().toString();
                        if (TextUtils.isEmpty(location)){
                            loc.setError(getString(R.string.error_field_required));
                            focusView = loc;
                            error = true;
                        }

                        //hidden variables
                        latitude = findLatitude();
                        longitude = findLongitude();
                        createDate = getCreateDate();
                        lastEditDate = getCreateDate();
                        lastEditUser = prefs.getString("username", "user");

                        if (error) {
                            // There was an error; don't attempt login and focus the first
                            // form field with an error.
                            focusView.requestFocus();
                        }else{
                        //call method to parse the strings into the proper table column field
                        insertIntoDatabase();

                        goHome();}
                    }
                });

        //determines what is done when cancel is cared
        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent:", intent.toString());
        showSearch(false);

        //creates a bundle of intents
        Bundle extras = intent.getExtras();

        //stores the variables from the bundle to pass to different intents
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        query = String.valueOf(extras.get(SearchManager.QUERY));

        Log.d("query:", query);
        Log.d("userQuery:", userQuery);

        //checks for null
        if(userQuery != null) {
            Intent searchResults = new Intent(this, IMSearchResultsActivity.class);
            searchResults.putExtra("SearchResults", userQuery);
            startActivity(searchResults);
        }

        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
    }

    /**
     * this will display search results
     * @param visible   if true
     */
    protected void showSearch(boolean visible) {
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
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

    public void addListenerOnSpinnerItemSelection() {
        conditionSpinner = (Spinner) findViewById(R.id.condition_editText);
        conditionSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goHome() {
        Intent intent = new Intent(submitActivity, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Inserts item into database
     * @return  true if added
     */
    public boolean insertIntoDatabase() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //create an ArrayList for the values to be stored in
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

                nameValuePairs.add(new BasicNameValuePair("ItemID", id + ""));
                nameValuePairs.add(new BasicNameValuePair("Label", label));
                nameValuePairs.add(new BasicNameValuePair("ItemName", itemName));
                nameValuePairs.add(new BasicNameValuePair("Category", category));
                nameValuePairs.add(new BasicNameValuePair("ModelNumber", model + ""));
                nameValuePairs.add(new BasicNameValuePair("ConditionID", condition));
                nameValuePairs.add(new BasicNameValuePair("Location", location));
                nameValuePairs.add(new BasicNameValuePair("Latitude", latitude + ""));
                nameValuePairs.add(new BasicNameValuePair("Longitude", longitude + ""));
                nameValuePairs.add(new BasicNameValuePair("CreateDate", createDate));
                nameValuePairs.add(new BasicNameValuePair("LastEditDate", lastEditDate));
                nameValuePairs.add(new BasicNameValuePair("LastEditUser", lastEditUser));

                for(int i = 0; i < nameValuePairs.size(); i++) {
                    Log.d("ArrayList:", nameValuePairs.get(i).toString());
                }

                //creates a post to the php server
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost("http://s15inventory.franklinpracticum.com/php/insert.php");

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

                //creates the JSON
                try

                {
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(is, "iso-8859-1"), 8);

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

                //creates the JSON object from the buffered reader object
                try {
                    JSONObject json_data = new JSONObject(result);
                    System.out.println(json_data);
                    Log.d("code before,", code +"");
                    code = (json_data.getInt("code"));
                    Log.d("code after grab,", code +"");

                    if (code == 1) {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Added successfully",
                                        Toast.LENGTH_SHORT).show();

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

                        //PHP script not updating 'code' variable this indicates that something is
                        //wrong with the insert.php script
                        //I am not exactly sure what it is though.
                        //the script is outputting ": 0", where it should be "1"
                        Log.d("Failure code:", code + "");
                        Log.d("Failure 3", "Inserted Unsuccessfully");
                    }
            }
            catch(Exception e) {
                Log.e("Fail 3", e.toString());
            }

            return null;
        }
    }.execute();
    return true;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secondary, menu);

        //adds search bar to the actionbar
        searchItem = menu.add(android.R.string.search_go);

        searchItem.setIcon(R.drawable.ic_action_search);

        MenuItemCompat.setActionView(searchItem, searchView);

        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

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

    /**
     *     Latitude calculator
     * @return Latitude
     */
    public double findLatitude() {
        //grabs the location from the GPS
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if(lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                latitude = location.getLatitude();
            } else {
                latitude = 0.0;
            }
        }

        return latitude;
    }

    /**
     *     Longitude calculator

     * @return Longitude
     */
    public double findLongitude() {
        //grabs the location from the GPS

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if(lm == null) {
            Toast.makeText(this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                longitude = location.getLongitude();
            } else {
                longitude = 0.0;
            }
        }

        return longitude;
    }

    /**
     *     sets the date format for the database using the Calendar object
     */
    public String getCreateDate() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");

        String strDate = sdf.format(cal.getTime());
        return strDate;
    }
}
