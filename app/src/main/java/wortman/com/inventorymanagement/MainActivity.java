package wortman.com.inventorymanagement;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import wortman.com.openshiftapplication.R;


public class MainActivity extends ActionBarActivity {

    //private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //ACTION BAR TO BE ON EACH ACTIVITY
        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        //Action bar settings
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.inv_man);
        //getSupportActionBar().setHomeButtonEnabled(true);

        RelativeLayoutButton button1 = new RelativeLayoutButton(this,R.id.inventory_button);
        RelativeLayoutButton button2 = new RelativeLayoutButton(this,R.id.add_button);
        RelativeLayoutButton button3 = new RelativeLayoutButton(this,R.id.barcode_button);
        RelativeLayoutButton button4 = new RelativeLayoutButton(this,R.id.report_button);
        RelativeLayoutButton button5 = new RelativeLayoutButton(this,R.id.nearby_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "View Inventory Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add Inventory Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Barcode Scanner Button clicked", Toast.LENGTH_LONG).show();

            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Generate Report Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Locate Nearby Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void selectFrag(View view) {
        android.support.v4.app.Fragment fr;

        if(view == findViewById(R.id.button2)) {
            fr = new FragmentTwo();
        } else if (view == findViewById(R.id.button3)) {
            fr = new FragmentThree();
        } else {
            fr = new FragmentOne();
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.test_fragment, fr);
        fragmentTransaction.commit();
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
            Toast.makeText(MainActivity.this, "Settings Button Clicked", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_help) {
            Toast.makeText(MainActivity.this, "Help Button Clicked", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_print) {
            Toast.makeText(MainActivity.this, "Print Button Clicked", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_main) {
            Toast.makeText(MainActivity.this, "Main Menu Button Clicked", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
