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
import android.widget.EditText;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import wortman.com.openshiftapplication.R;


public class IMAddActivity extends ActionBarActivity {
    //this is for navigation with the overflow menu
    private Activity submitActivity = this;

    //class variables from table
    String label;
    String itemName;
    String category;
    String model;
    int condition;
    String location;

    //class variables that are automated
    int id;
    Double latitude;
    Double longitude;
    String createDate;
    String lastEditDate;
    String lastEditUser;


    InputStream is = null;
    String result = null;
    String line = null;
    int code;

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

        final EditText lbl = (EditText) findViewById(R.id.label_editText);
        final EditText itm = (EditText) findViewById(R.id.itemName_editText);
        final EditText cat = (EditText) findViewById(R.id.category_editText);
        final EditText modl = (EditText) findViewById(R.id.model_editText);
        final EditText cond = (EditText) findViewById(R.id.condition_editText);
        final EditText loc = (EditText) findViewById(R.id.location_editText);

        findViewById(R.id.add_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(IMAddActivity.this, "Add Button Clicked", Toast.LENGTH_SHORT).show();

                        //global strings get text from EditText fields
                        label = lbl.getText().toString();
                        itemName = itm.getText().toString();
                        category = cat.getText().toString();
                        model = modl.getText().toString();
                        condition = Integer.parseInt(cond.getText().toString());
                        location = loc.getText().toString();

                        //hidden variables
                        latitude = new AssistActivities().findLatitude();
                        longitude = 2.9; //new AssistActivities().findLongitude();
                        createDate = "test"; //new AssistActivities().getCreateDate();
                        lastEditDate = "test"; //new AssistActivities().getCreateDate();
                        lastEditUser = "nicholas"; //new AssistActivities().getLastEditUser();

                        //call method to parse the strings into the proper table column field
                        insertIntoDatabase();

                        //call successful
                        //Toast.makeText(IMAddActivity.this, "Add Successful", Toast.LENGTH_SHORT).show();

                    }
                });

        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(IMAddActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

    }

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
                nameValuePairs.add(new BasicNameValuePair("ConditionID", condition + ""));
                nameValuePairs.add(new BasicNameValuePair("Location", location));
                nameValuePairs.add(new BasicNameValuePair("Latitude", latitude + ""));
                nameValuePairs.add(new BasicNameValuePair("Longitude", longitude + ""));
                nameValuePairs.add(new BasicNameValuePair("CreateDate", createDate));
                nameValuePairs.add(new BasicNameValuePair("LastEditDate", lastEditDate));
                nameValuePairs.add(new BasicNameValuePair("LastEditUser", lastEditUser));

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
                                Toast.makeText(getBaseContext(), "Inserted Successfully",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Sorry, Try Again",
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
            Toast.makeText(IMAddActivity.this, "Settings Button Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(submitActivity, IMSettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_help) {
            Toast.makeText(IMAddActivity.this, "Help Button Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(submitActivity, IMHelpActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_print) {
            Toast.makeText(IMAddActivity.this, "Print Button Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(submitActivity, IMReportActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_main) {
            Toast.makeText(IMAddActivity.this, "Main Menu Button Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(submitActivity, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
