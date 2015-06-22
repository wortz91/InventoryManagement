package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Nicholas on 6/20/2015.
 */
public class AssistActivities extends ActionBarActivity{
    //this is for navigation with the overflow menu
    private Activity submitActivity = this;

    public static final String SESSION_DATA = "sessionData";

    //class variables that are automated
    int id;
    Double latitude;
    Double longitude;
    String createDate;
    String lastEditDate;
    String lastEditUser;

    //Assist methods (Latitude)
    public Double findLatitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if(lm == null) {
            Toast.makeText(AssistActivities.this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                latitude = location.getLatitude();
            }
        }

        return latitude;
    }

    //Assist methods (Latitude)
    public Double findLongitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if(lm == null) {
            Toast.makeText(AssistActivities.this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                longitude = location.getLongitude();
            }
        }

        return longitude;
    }

    public String getCreateDate() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");

        String strDate = sdf.format(cal.getTime());
        return strDate;
    }

    public String getLastEditUser() {
        SharedPreferences prefs = getSharedPreferences(SESSION_DATA, 0);
        String user = prefs.getString("user", "IT DIDN't SAVE");
        return user;
    }
}
