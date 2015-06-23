package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Nicholas on 6/20/2015.
 */
public class AssistActivities extends Activity {
//    //this is for navigation with the overflow menu
    private Activity submitActivity = this;

    public static final String SESSION_DATA = "sessionData";

    //class variables that are automated
    String userName;
    String createDate;
    String lastEditDate;
    String lastEditUser;

    //latitude
    Geocoder geocoder;
    String bestProvider;
    List<Address> user = null;
    double lat;
    double lng;

    LocationManager lm;

    //Assist methods (Latitude)
    public double findLongitude() {
        lm = (LocationManager) submitActivity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);
        Location loc = lm.getLastKnownLocation(bestProvider);

        if(loc == null) {
            Toast.makeText(submitActivity, "Location not found", Toast.LENGTH_LONG);
        } else {
            geocoder = new Geocoder(submitActivity);
            try {
                user = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                lat = (double) user.get(0).getLatitude();
                lng = (double) user.get(0).getLongitude();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return lng;
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Location location;
//
//        if(lm == null) {
//            Toast.makeText(AssistActivities.this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
//        } else {
//            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if(location != null) {
//                latitude = location.getLatitude();
//            }
//        }
//
//        return latitude;
    }

    //Assist methods (Latitude)
    public double findLatitude() {
        lm = (LocationManager) submitActivity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);
        Location loc = lm.getLastKnownLocation(bestProvider);

        if(loc == null) {
            Toast.makeText(submitActivity, "Location not found", Toast.LENGTH_LONG);
        } else {
            geocoder = new Geocoder(submitActivity);
            try {
                user = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                lat = (double) user.get(0).getLatitude();
                lng = (double) user.get(0).getLongitude();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return lat;
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Location location;
//
//        if(lm == null) {
//            Toast.makeText(AssistActivities.this, "GPS not enabled, please enable GPS in system settings", Toast.LENGTH_SHORT).show();
//        } else {
//            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if(location != null) {
//                latitude = location.getLatitude();
//            }
//        }
//
//        return latitude;
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
