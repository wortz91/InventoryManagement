package wortman.com.inventorymanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import wortman.com.openshiftapplication.R;

/**
 *
 * Created by Nicholas on 5/9/2015.
 **/

public class FragmentOne extends android.support.v4.app.Fragment {
    //this fragment will add an item to the database

    String name;
    String id;
    InputStream is=null;
    String result=null;
    String line=null;
    int code;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_one, container, false);
        final EditText e_id=(EditText) view.findViewById(R.id.editText1);
        final EditText e_name=(EditText) view.findViewById(R.id.editText2);
        Button insert=(Button) view.findViewById(R.id.button1);

        insert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                id = e_id.getText().toString();
                name = e_name.getText().toString();

                insertItem();
            }
        });

        return view;

    }

    public boolean insertItem()
    {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id", id));
                nameValuePairs.add(new BasicNameValuePair("name", name));

                try
                {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://s15inventory.franklinpracticum.com/insert.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Winner",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("pass 1", "connection success ");
                }
                catch(Exception e)
                {
                    Log.e("Fail 1", e.toString());


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Invalid IP Address :(",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                try
                {
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(is,"iso-8859-1"),8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                }
                catch(Exception e)
                {
                    Log.e("Fail 2", e.toString());
                }

                try
                {
                    JSONObject json_data = new JSONObject(result);
                    code=(json_data.getInt("code"));

                    if(code==1)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getBaseContext(), "Inserted Successfully",
                                Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d("Success 3", "Inserted Successfully");
                    }
                    else
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getBaseContext(), "Sorry, Try Again",
                                Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.d("Failure 3", "Inserted Unsuccessfully");
                    }
                }
                catch(Exception e)
                {
                    Log.e("Fail 3", e.toString());
                }
                return null;
            }
        }.execute();
        return true;
    }
}
