package wortman.com.inventorymanagement;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wortman.com.openshiftapplication.R;

/**
 * Created by Nicholas on 5/9/2015.
 */

public class FragmentTwo extends android.support.v4.app.Fragment {
    //this fragment will edit an item from the database
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false);
    }
}

