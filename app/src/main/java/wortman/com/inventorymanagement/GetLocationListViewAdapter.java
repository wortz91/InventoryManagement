package wortman.com.inventorymanagement;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wortman.com.openshiftapplication.R;

/**
 * Apater class to help with populating nearby location ListView.
 *
 * @author Jason Edwards
 */
public class GetLocationListViewAdapter extends BaseAdapter{

    private JSONArray dataArray2;
    private Activity activity;
    private static LayoutInflater inflater = null;

    /**
     * Method to populate ListView.
     *
     * @param jsonArray
     * @param a
     */
    public GetLocationListViewAdapter(JSONArray jsonArray, Activity a) {
        this.dataArray2 = jsonArray;
        this.activity = a;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Method to keep track of array size.
     *
     * @return array length
     */
    @Override
    public int getCount() {
        return this.dataArray2.length();
    }

    /**
     * Method to identify which object in ListView is being selected.
     *
     * @param position
     * @return selected item position
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Method to identify which itemId in ListView is being selected.
     *
     * @param position
     * @return selected item position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Method get item selected in ListView
     *
     * @param position
     * @param convertView
     * @param parent
     * @return item selected
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // set up convert view if it is null
        ListCell cell;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nearby_view, null);
            cell = new ListCell();
            cell.Label = (TextView) convertView.findViewById(R.id.valLabel);
            cell.ItemName = (TextView) convertView.findViewById(R.id.valItemName);
            cell.Category = (TextView) convertView.findViewById(R.id.valCategory);
            cell.ModelNumber = (TextView) convertView.findViewById(R.id.valModelNum);
            cell.Condition = (TextView) convertView.findViewById(R.id.valCondition);
            cell.Location = (TextView) convertView.findViewById(R.id.valLocation);
            cell.Latitude = (TextView) convertView.findViewById(R.id.valLatitude);
            cell.Longitude = (TextView) convertView.findViewById(R.id.valLongitude);
            convertView.setTag(cell);
        } else {
            cell = (ListCell) convertView.getTag();
        }
        // change the data of cell
        try {
            JSONObject jsonObject = this.dataArray2.getJSONObject(position);
            cell.Label.setText("Serial Number: "+ jsonObject.getString("Label"));
            cell.ItemName.setText("Item Name: "+ jsonObject.getString("ItemName"));
            cell.Category.setText("Category: "+ jsonObject.getString("Category"));
            cell.ModelNumber.setText("Model Number: "+ jsonObject.getString("ModelNumber"));
            cell.Condition.setText("Condition ID: "+ jsonObject.getString("ConditionID"));
            cell.Location.setText("Location: "+ jsonObject.getString("Location"));
            cell.Latitude.setText("Latitude:" + jsonObject.getString("Latitude"));
            cell.Longitude.setText("Longitude:" + jsonObject.getString("Longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * Simple class for cells.
     *
     * @author Jason Edwards
     */
    private  class  ListCell {
        private TextView Label;
        private TextView ItemName;
        private TextView Category;
        private TextView ModelNumber;
        private TextView Condition;
        private TextView Location;
        private TextView Latitude;
        private TextView Longitude;
    }
}
