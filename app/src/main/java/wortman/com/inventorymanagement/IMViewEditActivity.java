package wortman.com.inventorymanagement;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import wortman.com.openshiftapplication.R;


public class IMViewEditActivity extends ActionBarActivity {

    private EditText label;
    private EditText itemName;
    private EditText category;
    private EditText model;
    private EditText condition;
    private EditText location;

    private String LabelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.label = (EditText) this.findViewById(R.id.label_editText);
        this.itemName = (EditText) this.findViewById(R.id.itemName_editText);
        this.category = (EditText) this.findViewById(R.id.catagory_editText);
        this.model = (EditText) this.findViewById(R.id.model_editText);
        this.condition = (EditText) this.findViewById(R.id.condition_editText);
        this.location = (EditText) this.findViewById(R.id.location_editText);

        //get LabelID to pass to DB call
        this.LabelID = getIntent().getStringExtra("LabelID");

        if (this.LabelID != null){
            new EditInventoryDetails().execute(new ApiConnector());
        }

        final Button edit = (Button)findViewById(R.id.edit_button);
        final Button save = (Button)findViewById(R.id.save_button);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);

                EditText label_editText =(EditText)findViewById(R.id.label_editText);
                label_editText.setCursorVisible(true);
                label_editText.setClickable(true);
                label_editText.setFocusableInTouchMode(true);

                EditText model_editText =(EditText)findViewById(R.id.model_editText);
                model_editText.setCursorVisible(true);
                model_editText.setClickable(true);
                model_editText.setFocusableInTouchMode(true);

                EditText itemName_editText =(EditText)findViewById(R.id.itemName_editText);
                itemName_editText.setCursorVisible(true);
                itemName_editText.setClickable(true);
                itemName_editText.setFocusableInTouchMode(true);

                EditText catagory_editText =(EditText)findViewById(R.id.catagory_editText);
                catagory_editText.setCursorVisible(true);
                catagory_editText.setClickable(true);
                catagory_editText.setFocusableInTouchMode(true);

                EditText condition_editText =(EditText)findViewById(R.id.condition_editText);
                condition_editText.setCursorVisible(true);
                condition_editText.setClickable(true);
                condition_editText.setFocusableInTouchMode(true);

                EditText location_editText =(EditText)findViewById(R.id.location_editText);
                location_editText.setCursorVisible(true);
                location_editText.setClickable(true);
                location_editText.setFocusableInTouchMode(true);
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);

                EditText label_editText =(EditText)findViewById(R.id.label_editText);
                label_editText.setCursorVisible(false);
                label_editText.setClickable(false);
                label_editText.setFocusableInTouchMode(false);

                EditText model_editText =(EditText)findViewById(R.id.model_editText);
                model_editText.setCursorVisible(false);
                model_editText.setClickable(false);
                model_editText.setFocusableInTouchMode(false);

                EditText itemName_editText =(EditText)findViewById(R.id.itemName_editText);
                itemName_editText.setCursorVisible(false);
                itemName_editText.setClickable(false);
                itemName_editText.setFocusableInTouchMode(false);

                EditText catagory_editText =(EditText)findViewById(R.id.catagory_editText);
                catagory_editText.setCursorVisible(false);
                catagory_editText.setClickable(false);
                catagory_editText.setFocusableInTouchMode(false);

                EditText condition_editText =(EditText)findViewById(R.id.condition_editText);
                condition_editText.setCursorVisible(false);
                condition_editText.setClickable(false);
                condition_editText.setFocusableInTouchMode(false);

                EditText location_editText =(EditText)findViewById(R.id.location_editText);
                location_editText.setCursorVisible(false);
                location_editText.setClickable(false);
                location_editText.setFocusableInTouchMode(false);
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(IMViewEditActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

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
            Toast.makeText(IMViewEditActivity.this, "Settings Button Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_help) {
            Toast.makeText(IMViewEditActivity.this, "Help Button Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_print) {
            Toast.makeText(IMViewEditActivity.this, "Print Button Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_main) {
            Toast.makeText(IMViewEditActivity.this, "Main Menu Button Clicked", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class EditInventoryDetails extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread

            return params[0].EditInventoryDetails(LabelID);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            try
            {
                JSONObject inventoryItem = jsonArray.getJSONObject(0);

                label.setText(inventoryItem.getString("Label"));
                itemName.setText(inventoryItem.getString("ItemName"));
                category.setText(inventoryItem.getString("Category"));
                model.setText(inventoryItem.getString("ModelNumber"));
                condition.setText(""+inventoryItem.getInt("ConditionID"));
                location.setText(inventoryItem.getString("Location"));

               // idOfCustomer = inventoryItem.getString("id");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
