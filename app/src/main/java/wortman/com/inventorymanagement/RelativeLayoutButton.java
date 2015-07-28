package wortman.com.inventorymanagement;

/**
 * Class created for the button design for the main menu.
 * Created by Jason on 6/7/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class RelativeLayoutButton extends RelativeLayout {

    public RelativeLayoutButton(Context context, int id)
    {
        super(context);
        // if our context is not Activity we can't get View supplied by id
        if (!(context instanceof Activity))
            return;
        // find relative layout by id
        View v = ((Activity)context).findViewById(id);
        // is it RelativeLayout ?
        if (!(v instanceof RelativeLayout))
            return;
        //cast it to relative layout
        RelativeLayout layout = (RelativeLayout)v;
        // copy layout parameters
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        this.setLayoutParams(params);
        // copy all child from relative layout to this button
        while (layout.getChildCount() > 0) {
            View vchild = layout.getChildAt(0);
            layout.removeViewAt(0);
            this.addView(vchild);
            vchild.setClickable(false);
            vchild.setFocusable(false);
            vchild.setFocusableInTouchMode(false);
        }
        // set that this button is clickable, focusable, ...
        this.setClickable(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(false);
        // replace relative layout in parent with this one modified to looks like button
        ViewGroup vp = (ViewGroup)layout.getParent();
        int index = vp.indexOfChild(layout);
        vp.removeView(layout);
        vp.addView(this,index);
        this.setId(id);
    }

    /**
     * Method for setting texts for the text views.
     * @param id
     * @param text
     */
    public void setText(int id, CharSequence text) {
        View v = findViewById(id);
        if (null != v && v instanceof TextView) {
            ((TextView)v).setText(text);
        }
    }

    /**
     * method for setting drawable for the images
     * @param id
     * @param drawable
     */
    public void setImageDrawable(int id, Drawable drawable) {
        View v = findViewById(id);
        if (null != v && v instanceof ImageView) {
            ((ImageView)v).setImageDrawable(drawable);
        }
    }
    /**
     * Method for setting images by resource id.
     * @param id
     * @param image_resource_id
     */
    public void setImageResource(int id, int image_resource_id) {
        View v = findViewById(id);
        if (null != v && v instanceof ImageView) {
            ((ImageView)v).setImageResource(image_resource_id);
        }
    }
}

