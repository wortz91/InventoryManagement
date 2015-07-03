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
 * Created by Jason on 6/21/2015.
 */
public class GetLocationListViewAdapter extends BaseAdapter{

    private JSONArray dataArray2;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public GetLocationListViewAdapter(JSONArray jsonArray, Activity a)
    {
        this.dataArray2 = jsonArray;
        this.activity = a;

        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.dataArray2.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // set up convert view if it is null
        ListCell cell;
        if (convertView == null)
        {
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
        }
        else
        {
            cell = (ListCell) convertView.getTag();
        }

        // change the data of cell

        try
        {
            JSONObject jsonObject = this.dataArray2.getJSONObject(position);

            cell.Label.setText("Label: "+ jsonObject.getString("Label"));
            cell.ItemName.setText("Item Name: "+ jsonObject.getString("ItemName"));
            cell.Category.setText("Category: "+ jsonObject.getString("Category"));
            cell.ModelNumber.setText("Model Number: "+ jsonObject.getString("ModelNumber"));
            cell.Condition.setText("Condition ID: "+ jsonObject.getInt("ConditionID"));
            cell.Location.setText("Location: "+ jsonObject.getString("Location"));
            cell.Latitude.setText("Latitude:" + jsonObject.getString("Latitude"));
            cell.Longitude.setText("Longitude:" + jsonObject.getString("Longitude"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return convertView;
    }

    private  class  ListCell
    {
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
