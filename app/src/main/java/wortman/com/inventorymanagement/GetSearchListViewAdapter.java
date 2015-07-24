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
 * Creates a custom adapter object for the displaying of the search results
 *
 * Created by Nicholas on 7/19/2015.
 */
public class GetSearchListViewAdapter extends BaseAdapter {

    /**
     * private variables
     */
    private JSONArray dataArray;
    private Activity activity;

    /**
     * variable for the layouts
     */
    private static LayoutInflater inflater = null;

    /**
     * Passes in the variables for the arrays and activities
     * @param jsonArray the json array
     * @param a the activity
     */
    public GetSearchListViewAdapter(JSONArray jsonArray, Activity a)
    {
        this.dataArray = jsonArray;
        this.activity = a;

        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.dataArray.length();
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
            convertView = inflater.inflate(R.layout.location_view, null);
            cell = new ListCell();

            cell.Label = (TextView) convertView.findViewById(R.id.valLabel);
            cell.ItemName = (TextView) convertView.findViewById(R.id.valItemName);
            cell.Category = (TextView) convertView.findViewById(R.id.valCategory);
            cell.ModelNumber = (TextView) convertView.findViewById(R.id.valModelNum);
            cell.Condition = (TextView) convertView.findViewById(R.id.valCondition);
            cell.Location = (TextView) convertView.findViewById(R.id.valLocation);

            convertView.setTag(cell);
        }
        else
        {
            cell = (ListCell) convertView.getTag();
        }

        // change the data of cell

        try
        {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);

            cell.Label.setText("Serial Number: "+ jsonObject.getString("Label"));
            cell.ItemName.setText("Item Name: "+ jsonObject.getString("ItemName"));
            cell.Category.setText("Category: "+ jsonObject.getString("Category"));
            cell.ModelNumber.setText("Model Number: "+ jsonObject.getString("ModelNumber"));
            cell.Condition.setText("Condition: "+ jsonObject.getString("ConditionID"));
            cell.Location.setText("Location: "+ jsonObject.getString("Location"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return convertView;
    }


    /**
     * creates a model for the object
     */
    private  class  ListCell
    {
        private TextView Label;
        private TextView ItemName;
        private TextView Category;
        private TextView ModelNumber;
        private TextView Condition;
        private TextView Location;
    }
}