package wortman.com.inventorymanagement;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import wortman.com.openshiftapplication.R;


public class IMReportActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    private Activity submitActivity = this;

    //private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //ACTION BAR TO BE ON EACH ACTIVITY
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Action bar settings
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.inv_man);
        getSupportActionBar().setHomeButtonEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.reportSpinner);
        spinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item
        parent.getItemAtPosition(pos);



        if(pos == 0) {
            //do nothing
        }
        //Category Report
        else if(pos == 1){
            WebView mWebView=new WebView(IMReportActivity.this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //Need to change URL to reflect actual report URL after the +
            mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+"http://s15inventory.franklinpracticum.com/php/categoryReportPDF.php");
            setContentView(mWebView);
        }
        //Condition Report
        else if(pos == 2){
            WebView mWebView=new WebView(IMReportActivity.this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //Need to change URL to reflect actual report URL after the +
            mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+"http://s15inventory.franklinpracticum.com/php/conditionReportPDF.php");
            setContentView(mWebView);

        }
        //Inventory Report
        else if(pos == 3){
            WebView mWebView=new WebView(IMReportActivity.this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //Need to change URL to reflect actual report URL after the +
            mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+"http://s15inventory.franklinpracticum.com/php/inventoryReportPDF.php");
            setContentView(mWebView);

        }
        //Location Report
        else if(pos == 4){
            WebView mWebView=new WebView(IMReportActivity.this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+"http://s15inventory.franklinpracticum.com/php/locationReportPDF.php");
            setContentView(mWebView);

        }
        else {
            Toast.makeText(IMReportActivity.this, "There Was An Error With Your Selection. Please Try Again", Toast.LENGTH_LONG).show();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
