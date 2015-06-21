package wortman.com.inventorymanagement;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

import java.sql.Timestamp;

/**
 * Created by Nicholas on 6/20/2015.
 */
public class AssistActivities extends ActionBarActivity{
    //this is for navigation with the overflow menu
    private Activity submitActivity = this;

    //class variables that are automated
    int id;
    Double latitude;
    Double longitude;
    Timestamp createDate;
    Timestamp lastEditDate;
    String lastEditUser;
    //the getId should not be needed
    public int getId() {
        return id;
    }

    //the setId should not be needed
    public void setId(int id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(Timestamp lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public String getLastEditUser() {
        return lastEditUser;
    }

    public void setLastEditUser(String lastEditUser) {
        this.lastEditUser = lastEditUser;
    }
}
