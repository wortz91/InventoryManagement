package wortman.com.inventorymanagement;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import wortman.com.openshiftapplication.R;

/**
 * Activity which displays a login screen to the user.
 * Created by Jason on 6/8/2015.
 */
public class IMLoginActivity extends Activity {

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
    public static final String SESSION_DATA = "sessionData";

    // UI references.
    private EditText email_field;
    private EditText password_field;
    // Values for email and password at the time of the login attempt.
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        email = getIntent().getStringExtra(EXTRA_EMAIL);
        email_field = (EditText) findViewById(R.id.email_field);
        email_field.setText(email);

        password_field = (EditText) findViewById(R.id.password_field);
        password_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        email_field.setError(null);
        password_field.setError(null);

        // Store values at the time of the login attempt.
        email = email_field.getText().toString();
        password = password_field.getText().toString();

        boolean error = false;
        View focusView = null;

        //DB calls go here

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            password_field.setError(getString(R.string.error_field_required));
            focusView = password_field;
            error = true;
        } else if (password.length() < 4) {
            password_field.setError(getString(R.string.error_invalid_password));
            focusView = password_field;
            error = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            email_field.setError(getString(R.string.error_field_required));
            focusView = email_field;
            error = true;
        } else if (!email.contains("@")) {
            email_field.setError(getString(R.string.error_invalid_email));
            focusView = email_field;
            error = true;
        }

        if (error) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }

        }
}
