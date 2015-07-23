package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import wortman.com.openshiftapplication.R;

/**
 * Activity which displays a login screen to the user.
 * Created by Jason on 6/8/2015.
 * Edited by Bob on 6/21/15.
 */
public class IMLoginActivity extends Activity {

    /**
     * The default user to populate the user field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
    public static final String SESSION_DATA = "sessionData";
    private Activity submitActivity = this;
    // UI references.
    private EditText user_field;
    private EditText password_field;
    // Values for user and password at the time of the login attempt.
    private String user;
    private String password;

    private InputStream is = null;
    private String result = null;
    private String line = null;
    private int code;
    private boolean successful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        user = getIntent().getStringExtra(EXTRA_EMAIL);
        user_field = (EditText) findViewById(R.id.user_field);
        user_field.setText(user);
        password_field = (EditText) findViewById(R.id.password_field);
        password_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                }
                return false;
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected()) {
                            attemptLogin();
                        }else{
                            Toast.makeText(getApplicationContext(), "You must be connected to " +
                                            "a WIFI or cellular connection to use this application. " +
                                            "Please connect to a network and try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Make sure there is a network connection.
     * @return true if there is
     */
    public boolean isConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (Exception e)  {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid user, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

                // Reset errors.
                user_field.setError(null);
                password_field.setError(null);

                // Store values at the time of the login attempt.
                user = user_field.getText().toString();
                password = password_field.getText().toString();

                boolean error = false;
                View focusView = null;

                // Check for a password.
                if (TextUtils.isEmpty(password)) {
                    password_field.setError(getString(R.string.error_field_required));
                    focusView = password_field;
                    error = true;
                }

                // Check for a user.
                if (TextUtils.isEmpty(user)) {
                    user_field.setError(getString(R.string.error_field_required));
                    focusView = user_field;
                    error = true;
                }

                if (error) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else{

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                //DB calls go here
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

                nameValuePairs.add(new BasicNameValuePair("username", user));
                nameValuePairs.add(new BasicNameValuePair("password", password));

                SharedPreferences userId =  getSharedPreferences(SESSION_DATA,0);
                SharedPreferences.Editor editor = userId.edit();
                editor.putString("username", user);
                editor.commit();

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s15inventory.franklinpracticum.com/php/login.php");
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

                try {
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
//                    String[] r = result.split(":");
//                    result = r[1].substring(0,1);
//                    Log.d("result", result);
//                    code = Integer.parseInt(result);
//                    Log.d("code", code +"");
//                    Log.d("in try", result);

                    JSONObject json_data = new JSONObject(result);
                    Log.d("json_data", json_data.toString());
                    Log.d("code before", code + "");

                    code = (json_data.getInt("code"));
                    Log.d("code", code + "");

                    if (code == 1) {
                        Log.d("test", "test");
                        successful = true;
                        Log.d("successful", successful + "");
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Login Successful",
                                        Toast.LENGTH_SHORT).show();
                                switchActivity();
                            }
                        });
                    } else {
                        submitActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Username or Password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());
                }
                return null;
            }

        }.execute();
    }}

    private void switchActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}